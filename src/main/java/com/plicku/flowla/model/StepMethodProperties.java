package com.plicku.flowla.model;

import com.plicku.flowla.exceptions.ProcessingException;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.model.contexts.Argument;
import com.plicku.flowla.model.contexts.VariableMap;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Setter @Getter
public class StepMethodProperties {
    Pattern variablePattern = Pattern.compile("\\$\\{(.*)\\}");
    private Method matchedMethod;
    private Class declaringClass;
    private List<Argument> stepAurguments;
    private List<MethodParameter> methodParameters = new ArrayList<>();
    private String matchedStepname;
    private int methodArgCount;
    private String stepName;
    private String keyword;


    public Object getNextArgValue(Object currArgValue, Class parameterType, int currIndex, VariableMap variableMap) throws ProcessingException {
            String val= (String) stepAurguments.get(currIndex+1).getVal();
            if(val.startsWith("${") && val.endsWith("}"))
            {
                val=getDeclaredVariableName(val);
                if(val.contains("."))
                {

                    return ConvertUtils.convert(variableMap.getVariableVal(getDeclaredVariableName(val)), parameterType);
                }
                else
                    return ConvertUtils.convert(variableMap.getVariableVal(getDeclaredVariableName(val)),parameterType);
            }
            else
                return ConvertUtils.convert(stepAurguments.get(currIndex+1).getVal(),parameterType);
    }

    private String getDeclaredVariableName(String val)
    {
        Matcher matcher=variablePattern.matcher(val);
        if (matcher.lookingAt()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                return matcher.group(i).trim();
            }
        }
        return null;
    }

    public StepMethodProperties(Method matchedMethod,String stepName) {
        this.matchedMethod = matchedMethod;
        this.stepName=stepName;
        populateMethodArgs();
    }

    public int getNonContextParamSize()
    {
        return (int) methodParameters.stream().filter(methodParameter -> {return  !methodParameter.equals(GlobalContext.class)&&!methodParameter.equals(SequenceContext.class);}).count();
    }

    public StepMethodProperties() {
    }

    private void populateMethodArgs()
    {
        this.declaringClass=this.matchedMethod.getDeclaringClass();
        for (int i = 0; i < this.matchedMethod.getParameterTypes().length; i++) {
            MethodParameter parameter = new MethodParameter();
            parameter.setParameterType(this.matchedMethod.getParameterTypes()[i]);
            methodParameters.add(parameter);
        }
        Annotation[][] annotations =  this.matchedMethod.getParameterAnnotations();
        for (int i = 0; i < annotations.length ; i++) {
            if(annotations[i].length>0) {
                methodParameters.get(i).setAnnotated(true);
                methodParameters.get(i).setArgAnnotation(Arrays.asList(annotations[i]));
            }
        }
        this.methodArgCount=this.matchedMethod.getParameterCount();
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }


}
