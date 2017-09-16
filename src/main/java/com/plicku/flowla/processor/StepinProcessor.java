package com.plicku.flowla.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.flowla.anotations.operators.*;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.model.MethodMap;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.util.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.plicku.flowla.util.Constants.*;

public class StepinProcessor {

    public static GlobalContext globalContext = new GlobalContext();
    public static Map<Class,Object> classMap = new ConcurrentHashMap<>();
    public static MethodMap methodMap = new MethodMap();
    private Pattern flowKeywordPattern = Pattern.compile("Given |When |Then |And |If |End If|Else If");
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

        methods = reflections.getMethodsAnnotatedWith(If.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(If.class).value(),method);
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
        Boolean ifElseEligibleBlockBeginRegistered= false;
        Boolean ifToProcess=false;
        List<String> ifLinesToExecute = new ArrayList<>();
        List<String> ElseifLinesToExecute = new ArrayList<>();
        for (String line:lines) {
            try{
            if (!line.startsWith(COMMENT)) {
                if (KEYWORDS.stream().anyMatch(line.trim()::startsWith)) {

                    if(ifElseEligibleBlockBeginRegistered && !(line.startsWith(END_IF)||(line.startsWith(ELSE_IF))))
                    {
                        //process if blocks
                        process(ifLinesToExecute);
                        ifElseEligibleBlockBeginRegistered=false;
                        ifToProcess=false;
                        ifLinesToExecute = new ArrayList<>();
                    }
                    else if(ifElseEligibleBlockBeginRegistered)
                    {
                        ifLinesToExecute.add(line);
                    }
                    else if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                        //complete previous step execution if pending
                        if(IF.equals(stepExecutor.getKeyword())||ELSE_IF.equals(stepExecutor.getKeyword()))
                        {
                            Boolean if_elseif_result = (Boolean) stepExecutor.executeMethod(sequenceContext);
                            if(if_elseif_result)
                            {
                                ifElseEligibleBlockBeginRegistered=true;
                            }
                            else {
                                continue;
                            }

                        }
                        else
                        {
                            stepExecutor.executeMethod(sequenceContext);
                        }
                    }
                    stepExecutor = new StepExecutor();
                    Matcher matcher = flowKeywordPattern.matcher(line);
                    matcher.find();
                    String keyword = matcher.group(0);
                    line = line.replaceFirst(keyword, "").trim();
                    StepMethodProperties stepMethodProperties = methodMap.get(line);
                    if (stepMethodProperties == null) throw new Exception("Unable to find step definition for " + line);
                    stepMethodProperties.setKeyword(keyword);
                    if(IF.equals(keyword))
                        ifToProcess=true;
                    stepExecutor.setStepMethodProperties(stepMethodProperties);
                }
                else if(line.trim().startsWith(Constants.IF))
                {
                    if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                        //complete previous step execution if pending
                        stepExecutor.executeMethod(sequenceContext);
                    }
                    stepExecutor = getStepExecutor(line);
                    ifElseEligibleBlockBeginRegistered=true;
                }
                else if(ifElseEligibleBlockBeginRegistered)
                {
                    ifLinesToExecute.add(line);
                }
                else if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
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
        if(ifToProcess)
            throw new Exception("If block not closed. Make sure to close if with End If");
        if(stepExecutor.isMethodToBeExecuted())
            stepExecutor.executeMethod(sequenceContext);
    }

    private StepExecutor getStepExecutor(String line) throws Exception
    {
        StepExecutor stepExecutor = new StepExecutor();
        Matcher matcher = flowKeywordPattern.matcher(line);
        matcher.find();
        String registeredType = matcher.group(0);
        line = line.replaceFirst(registeredType, "").trim();
        StepMethodProperties stepMethodProperties = methodMap.get(line);
        if (stepMethodProperties == null) throw new Exception("Unable to find step definition for " + line);
        stepExecutor.setStepMethodProperties(stepMethodProperties);
        return stepExecutor;
    }



}
