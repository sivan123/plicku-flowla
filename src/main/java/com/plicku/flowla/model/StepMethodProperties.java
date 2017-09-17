package com.plicku.flowla.model;

import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.util.Argument;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.ConvertUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Setter @Getter
public class StepMethodProperties {


    private Method matchedMethod;
    private Class declaringClass;
    private List<Argument> stepAurguments;
    private List<MethodParameter> methodParameters = new ArrayList<>();
    private String matchedStepname;
    private int methodArgCount;
    private String stepName;
    private String keyword;


    public Object getNextArgValue(Object currArgValue, Class parameterType,int currIndex) {

            return ConvertUtils.convert(stepAurguments.get(currIndex+1).getVal(),parameterType);

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
