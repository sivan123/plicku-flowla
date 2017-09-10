package com.plicku.stepin.model;

import com.plicku.stepin.util.PatternArgumentMatcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MethodMap{

    private Map<String,StepMethodProperty> plainMethodMap = new ConcurrentHashMap<>();
    //cache for faster look up the second time
    private ConcurrentHashMap<String, StepMethodProperty> cachedRegMethodMap = new ConcurrentHashMap<>();

    public void put(String stepname, Method method)
    {
        plainMethodMap.put(stepname,new StepMethodProperty(method));
    }

    public StepMethodProperty get(String srchStepname)
    {
        if(plainMethodMap.containsKey(srchStepname)) return plainMethodMap.get(srchStepname);
        else if(cachedRegMethodMap.containsKey(srchStepname)) return cachedRegMethodMap.get(srchStepname);
        else {

            for (String stepname : plainMethodMap.keySet()) {
                Pattern pattern = Pattern.compile(stepname);
                if (pattern.matcher(srchStepname).matches()) {
                    StepMethodProperty stepMethodProperty = new StepMethodProperty();
                    stepMethodProperty.setMatchedMethod(plainMethodMap.get(stepname).getMatchedMethod());
                    stepMethodProperty.setStepAurguments(new PatternArgumentMatcher(pattern).argumentsFrom(srchStepname));
                    cachedRegMethodMap.put(srchStepname, stepMethodProperty);
                    return stepMethodProperty;
                }
            }
            return null;
        }
    }
}
