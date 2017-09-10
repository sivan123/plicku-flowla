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
public class StepMethodProperty {

    String stepName;
    String matchedStepName;
    Method matchedMethod;
    Class declaringClass;
    List<Argument> stepAurguments;
    List<MethodParameter> methodParameters = new ArrayList<>();
    int methodArgCount;



    public StepMethodProperty(Method matchedMethod) {
        this.matchedMethod = matchedMethod;
        populateMethodArgs();
    }


    public StepMethodProperty() {
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
