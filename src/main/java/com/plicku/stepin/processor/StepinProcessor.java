package com.plicku.stepin.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.stepin.anotations.Given;
import com.plicku.stepin.anotations.StepDefinitions;
import com.plicku.stepin.model.MethodMap;
import com.plicku.stepin.model.StepinEnums;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StepinProcessor {

    public static Map<Class,Object> classMap = new ConcurrentHashMap<>();
    public static MethodMap methodMap = new MethodMap();
    private Pattern flowKeywordPattern = Pattern.compile("Given |When |Then |And ");
    public static final ObjectMapper objectMapper = new ObjectMapper();
    String stepdefpackage;




    public StepinProcessor(String stepdefpackage) throws IllegalAccessException, InstantiationException {
     this.stepdefpackage=stepdefpackage;
        Reflections reflections = new Reflections(stepdefpackage, new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(Given.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(Given.class).value(),method);
        });
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(StepDefinitions.class);
        for (Class aClass:classes){
            Object o = aClass.newInstance();
            classMap.put(aClass,o);
        }

    }
    private StepinProcessor(){}

    public void process(String stepinfileString) throws Exception {

        List<String> lines = Arrays.asList(StringUtils.split(stepinfileString, System.lineSeparator()));
        process(lines);

    }
    public void process(File storyFile) throws Exception {

        List<String> lines = FileUtils.readLines(storyFile,"UTF-8");
        process(lines);

    }

    public void process(List<String> lines) throws Exception {

        StepExecutor stepExecutor =null;
        for (String line:lines) {
            line=line.trim();
            if(Stream.of("Given ","When ","Then ","And ").anyMatch(line::startsWith))
            {
                if(stepExecutor !=null && stepExecutor.isMethodToBeExecuted())
                {
                    //complete previous step execution if pending
                    stepExecutor.executeMethod();
                }
                stepExecutor = new StepExecutor();
                Matcher matcher =flowKeywordPattern.matcher(line);
                matcher.find();
                String registeredType=matcher.group(0);
                line = line.replaceFirst(registeredType,"").trim();
                stepExecutor.setInstructionType(StepinEnums.InstructionType.valueOf(registeredType.trim()));
                Method method = methodMap.get(line).getMatchedMethod();
                if(method==null) throw new Exception("Unable to find step definition for "+line);
                stepExecutor.setStepMethodProperty(method);
            }
            else if(stepExecutor !=null && stepExecutor.isMethodToBeExecuted())
            {
                //add data
                stepExecutor.addParamDataLine(line);
            }
        }
        //finish any pending matchedMethod exec.
        if(stepExecutor.isMethodToBeExecuted())
            stepExecutor.executeMethod();
    }



}
