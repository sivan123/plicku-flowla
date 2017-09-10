package com.plicku.stepin.processor;

import com.plicku.stepin.anotations.parameters.DataTable;
import com.plicku.stepin.anotations.parameters.JSONParameter;
import com.plicku.stepin.anotations.parameters.YAMLParameter;
import com.plicku.stepin.model.MethodParameter;
import com.plicku.stepin.model.StepMethodProperties;
import com.plicku.stepin.model.contexts.GlobalContext;
import com.plicku.stepin.model.contexts.SequenceContext;
import com.plicku.stepin.util.ParamDataUtil;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepExecutor {


    private boolean methodToBeExecuted=false;
    private StepMethodProperties stepMethodProperties;
    private List<String> paramData = new ArrayList<>();


    public void setStepMethodProperties(StepMethodProperties stepMethodProperties) {
        this.stepMethodProperties = stepMethodProperties;
        this.methodToBeExecuted=true;
    }

    public void addParamDataLine(String line)
    {
        this.paramData.add(line);
    }

    public void executeMethod(SequenceContext sequenceContext) throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        Method method = stepMethodProperties.getMatchedMethod();
        method.setAccessible(true);
        if(stepMethodProperties.getMethodParameters().size()==0)
                method.invoke(StepinProcessor.classMap.get(stepMethodProperties.getDeclaringClass()));
        else //create bean an inject
        {
            List<Object> params = new ArrayList<>();

            for (MethodParameter methodParameter : stepMethodProperties.getMethodParameters())
            {
                Object bean = null;
                //check if datatable

                if((methodParameter.getArgAnnotation()!=null&&methodParameter.getArgAnnotation().contains(DataTable.class))||"\\|".startsWith(this.paramData.toString()))
                {
                    params.add(ParamDataUtil.getBean(this.paramData,methodParameter.getParameterType()));
                }
                else if((methodParameter.getArgAnnotation()!=null&&methodParameter.getArgAnnotation().contains(JSONParameter.class))||"{".startsWith(this.paramData.toString())||"[".startsWith(this.paramData.toString()))
                {
                    params.add(ParamDataUtil.getBeanFromJson(this.paramData,methodParameter.getParameterType()));
                }
                else if(methodParameter.getArgAnnotation()!=null&&methodParameter.getArgAnnotation().contains(YAMLParameter.class))
                {
                    params.add(ParamDataUtil.getBeanFromYaml(this.paramData,methodParameter.getParameterType()));
                }
                else if(SequenceContext.class.equals(methodParameter.getParameterType()))
                {
                    params.add(sequenceContext);
                }
                else if(GlobalContext.class.equals(methodParameter.getParameterType()))
                {
                    params.add(StepinProcessor.globalContext);
                }
                else
                {
                    params.add(stepMethodProperties.getNextArgValue());
                }
            }
            method.invoke(StepinProcessor.classMap.get(this.stepMethodProperties.getDeclaringClass()),params.toArray());
        }
        this.setMethodToBeExecuted(false);
    }

    public boolean isMethodToBeExecuted() {
        return methodToBeExecuted;
    }

    public void setMethodToBeExecuted(boolean methodToBeExecuted) {
        this.methodToBeExecuted = methodToBeExecuted;
    }
}
