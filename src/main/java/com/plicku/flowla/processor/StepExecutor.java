package com.plicku.flowla.processor;

import com.plicku.flowla.anotations.parameters.DataTableParameter;
import com.plicku.flowla.anotations.parameters.JSONParameter;
import com.plicku.flowla.anotations.parameters.YAMLParameter;
import com.plicku.flowla.model.MethodParameter;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.util.ParamDataUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class StepExecutor {


    private boolean methodToBeExecuted=false;
    private StepMethodProperties stepMethodProperties;

    private String paramData = "";

    public String getKeyword()
    {
        return stepMethodProperties.getKeyword();
    }


    public void setParamData(String paramData) {
        this.paramData = paramData;
    }


    public void setStepMethodProperties(StepMethodProperties stepMethodProperties) {
        this.stepMethodProperties = stepMethodProperties;
        this.methodToBeExecuted=true;
    }



    public Object executeMethod(SequenceContext sequenceContext) throws Exception {
        Object returnValue= null;
       try {
           Method method = stepMethodProperties.getMatchedMethod();
           method.setAccessible(true);
           if (stepMethodProperties.getMethodParameters().size() == 0)
               returnValue= method.invoke(StepinProcessor.classMap.get(stepMethodProperties.getDeclaringClass()));
           else //create bean an inject
           {
               List<Object> params = new ArrayList<>();
                int methodArgConsumedIndex=-1;
               for (MethodParameter methodParameter : stepMethodProperties.getMethodParameters()) {
                   Object currArgValue=null;
                   Object bean = null;
                   //check if datatable

                   if (methodParameter.getArgAnnotation() != null &&
                           methodParameter.getArgAnnotation().stream().anyMatch(annotation -> annotation.annotationType().equals(DataTableParameter.class))) {
                       params.add(ParamDataUtil.getBean(this.paramData, methodParameter.getParameterType()));
                   } else if (methodParameter.getArgAnnotation() != null &&
                           methodParameter.getArgAnnotation().stream().anyMatch(annotation -> annotation.annotationType().equals(JSONParameter.class))) {
                       params.add(ParamDataUtil.getBeanFromJson(this.paramData, methodParameter.getParameterType()));
                   } else if (methodParameter.getArgAnnotation() != null &&
                           methodParameter.getArgAnnotation().stream().anyMatch(annotation -> annotation.annotationType().equals(YAMLParameter.class))) {
                       params.add(ParamDataUtil.getBeanFromYaml(this.paramData, methodParameter.getParameterType()));
                   } else if (SequenceContext.class.equals(methodParameter.getParameterType())) {
                       params.add(sequenceContext);
                   } else if (GlobalContext.class.equals(methodParameter.getParameterType())) {
                       params.add(StepinProcessor.globalContext);
                   } else {
                       currArgValue=stepMethodProperties.getNextArgValue(currArgValue,methodParameter.getParameterType(),methodArgConsumedIndex);
                       params.add(currArgValue);
                       methodArgConsumedIndex++;
                   }
               }
               returnValue= method.invoke(StepinProcessor.classMap.get(this.stepMethodProperties.getDeclaringClass()), params.toArray());
           }
           this.setMethodToBeExecuted(false);
       }catch (Exception e){
           throw new Exception("Error Executiing step "+stepMethodProperties.getStepName(),e);
       }

        return returnValue;
    }

    public boolean isMethodToBeExecuted() {
        return methodToBeExecuted;
    }

    public void setMethodToBeExecuted(boolean methodToBeExecuted) {
        this.methodToBeExecuted = methodToBeExecuted;
    }


}
