package com.plicku.stepin.model;

import com.plicku.stepin.util.Argument;
import lombok.Getter;
import lombok.Setter;

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


    int arCnt=-1;
    public Object getNextArgValue()
    {
        arCnt++;
        return stepAurguments.get(arCnt).getVal();
    }

    public StepMethodProperties(Method matchedMethod) {
        this.matchedMethod = matchedMethod;
        populateMethodArgs();
    }


    public StepMethodProperties() {
    }

    private void populateMethodArgs()
    {
        this.declaringClass=this.matchedMethod.getDeclaringClass();
        for (int i = 0; i < this.matchedMethod.getParameterTypes().length; i++) {
            MethodParameter argument = new MethodParameter();
            argument.setParameterType(this.matchedMethod.getParameterTypes()[i]);
            methodParameters.add(argument);
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

}
