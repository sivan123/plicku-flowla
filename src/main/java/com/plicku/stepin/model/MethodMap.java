package com.plicku.stepin.model;

import com.plicku.stepin.util.PatternArgumentMatcher;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class MethodMap{

    private Map<String,StepMethodProperties> plainMethodMap = new ConcurrentHashMap<>();
    //cache for faster look up the second time
    private ConcurrentHashMap<String, StepMethodProperties> cachedRegMethodMap = new ConcurrentHashMap<>();

    public void put(String stepname, Method method)
    {
        plainMethodMap.put(stepname,new StepMethodProperties(method,stepname));
    }

    public StepMethodProperties get(String srchStepname)
    {
        if(plainMethodMap.containsKey(srchStepname)) return plainMethodMap.get(srchStepname);
        else if(cachedRegMethodMap.containsKey(srchStepname)) return cachedRegMethodMap.get(srchStepname);
        else {

            for (String stepname : plainMethodMap.keySet()) {
                Pattern pattern = Pattern.compile(stepname);
                if (pattern.matcher(srchStepname).matches()) {
                    StepMethodProperties stepMethodProperties = new StepMethodProperties(plainMethodMap.get(stepname).getMatchedMethod(),srchStepname);
                    stepMethodProperties.setStepAurguments(new PatternArgumentMatcher(pattern).argumentsFrom(srchStepname));
                    cachedRegMethodMap.put(srchStepname, stepMethodProperties);
                    return stepMethodProperties;
                }
            }
            return null;
        }
    }
}
