package com.plicku.flowla.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.flowla.anotations.operators.And;
import com.plicku.flowla.anotations.operators.Given;
import com.plicku.flowla.anotations.operators.Then;
import com.plicku.flowla.anotations.operators.When;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.model.MethodMap;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
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

    public static GlobalContext globalContext = new GlobalContext();
    public static Map<Class,Object> classMap = new ConcurrentHashMap<>();
    public static MethodMap methodMap = new MethodMap();
    private Pattern flowKeywordPattern = Pattern.compile("Given |When |Then |And ");
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String COMMENT="#";
    String stepdefpackage;




    public StepinProcessor(String stepdefpackage) throws IllegalAccessException, InstantiationException {
     this.stepdefpackage=stepdefpackage;
        Reflections reflections = new Reflections(stepdefpackage, new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
        loadMethodsForOperators(reflections);

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(StepDefinitions.class);
        for (Class aClass:classes){
            Object o = aClass.newInstance();
            classMap.put(aClass,o);
        }

    }

    private void loadMethodsForOperators(Reflections reflections){

        Set<Method> methods = reflections.getMethodsAnnotatedWith(And.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(And.class).value(),method);
        });

        methods = reflections.getMethodsAnnotatedWith(Given.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(Given.class).value(),method);
        });

        methods = reflections.getMethodsAnnotatedWith(Then.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(Then.class).value(),method);
        });

        methods = reflections.getMethodsAnnotatedWith(When.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(When.class).value(),method);
        });

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

        SequenceContext sequenceContext = new SequenceContext();
        StepExecutor stepExecutor =null;
        for (String line:lines) {
            try{
            if (!line.startsWith(COMMENT)) {
                if (Stream.of("Given ", "When ", "Then ", "And ").anyMatch(line.trim()::startsWith)) {
                    if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                        //complete previous step execution if pending
                        stepExecutor.executeMethod(sequenceContext);
                    }
                    stepExecutor = new StepExecutor();
                    Matcher matcher = flowKeywordPattern.matcher(line);
                    matcher.find();
                    String registeredType = matcher.group(0);
                    line = line.replaceFirst(registeredType, "").trim();
                    StepMethodProperties stepMethodProperties = methodMap.get(line);
                    if (stepMethodProperties == null) throw new Exception("Unable to find step definition for " + line);
                    stepExecutor.setStepMethodProperties(stepMethodProperties);
                } else if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                    //add data
                    stepExecutor.addParamDataLine(line);
                }
            }
            }catch (Exception e){
                System.out.println("Exception Processing "+line);
                e.printStackTrace();
            }
        }
        //finish any pending matchedMethod exec.
        if(stepExecutor.isMethodToBeExecuted())
            stepExecutor.executeMethod(sequenceContext);
    }



}
