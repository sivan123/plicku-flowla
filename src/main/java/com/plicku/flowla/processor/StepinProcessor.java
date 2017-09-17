package com.plicku.flowla.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.flowla.anotations.operators.*;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.model.MethodMap;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.model.vo.FlowContentEntry;
import com.plicku.flowla.util.Constants;
import com.plicku.flowla.util.StepContentParserUitl;
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
    public static final String keywordRegex = "Given |When |Then |And |If |End If|Else If";
    private Pattern flowKeywordPattern = Pattern.compile(keywordRegex);
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

    public void process(String flowContentStr) throws Exception {

        List<FlowContentEntry> contentBlocks = StepContentParserUitl.getFlowConentSteps(flowContentStr,keywordRegex);
        process(contentBlocks);

    }
    public void process(File storyFile) throws Exception {

        String fileContentStr = FileUtils.readFileToString(storyFile,"UTF-8");
        process(fileContentStr);

    }

    public void process(List<FlowContentEntry> entries) throws Exception {

        SequenceContext sequenceContext = new SequenceContext();
        StepExecutor stepExecutor =null;
        Boolean ifElseEligibleBlockBeginRegistered= false;
        Boolean ifToProcess=false;
        List<String> ifLinesToExecute = new ArrayList<>();
        List<FlowContentEntry> ifOrElseifEntriesToProcess = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            FlowContentEntry entry = entries.get(i);
            try {

                if (entry.ifOrElseIf()) {
                    Boolean if_elseif_result = (Boolean) stepExecutor.executeMethod(sequenceContext);
                    if (if_elseif_result) {
                        while (entries.get(i+1).isEndIfOrElseIf()){
                            i++;
                            ifOrElseifEntriesToProcess.add(entries.get(i));
                        }
                        process(ifOrElseifEntriesToProcess);
                    } else //skip until end if or else if
                    {
                        while (entries.get(i+1).isEndIfOrElseIf()){
                                i++;
                        }
                        continue;
                    }
                }
                else if (KEYWORDS.contains(entry.getKeyword())) {

                    if (ifElseEligibleBlockBeginRegistered && !(entry.startsWith(END_IF) || (entry.startsWith(ELSE_IF)))) {
                        //process if blocks
                        process(ifLinesToExecute);
                        ifElseEligibleBlockBeginRegistered = false;
                        ifToProcess = false;
                        ifLinesToExecute = new ArrayList<>();
                    } else if (ifElseEligibleBlockBeginRegistered) {
                        ifLinesToExecute.add(entry);
                    } else if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                        //complete previous step execution if pending
                        if (IF.equals(stepExecutor.getKeyword()) || ELSE_IF.equals(stepExecutor.getKeyword())) {
                            Boolean if_elseif_result = (Boolean) stepExecutor.executeMethod(sequenceContext);
                            if (if_elseif_result) {
                                ifElseEligibleBlockBeginRegistered = true;
                            } else {
                                continue;
                            }

                        } else {
                            stepExecutor.executeMethod(sequenceContext);
                        }
                    }
                    stepExecutor = new StepExecutor();
                    Matcher matcher = flowKeywordPattern.matcher(entry);
                    matcher.find();
                    String keyword = matcher.group(0);
                    entry = entry.replaceFirst(keyword, "").trim();
                    StepMethodProperties stepMethodProperties = methodMap.get(entry);
                    if (stepMethodProperties == null)
                        throw new Exception("Unable to find step definition for " + entry);
                    stepMethodProperties.setKeyword(keyword);
                    if (IF.equals(keyword))
                        ifToProcess = true;
                    stepExecutor.setStepMethodProperties(stepMethodProperties);
                } else if (entry.trim().startsWith(Constants.IF)) {
                    if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                        //complete previous step execution if pending
                        stepExecutor.executeMethod(sequenceContext);
                    }
                    stepExecutor = getStepExecutor(entry);
                    ifElseEligibleBlockBeginRegistered = true;
                } else if (ifElseEligibleBlockBeginRegistered) {
                    ifLinesToExecute.add(entry);
                } else if (stepExecutor != null && stepExecutor.isMethodToBeExecuted()) {
                    //add data
                    stepExecutor.addParamDataLine(entry);
                }

            } catch (Exception e) {
                System.out.println("Exception Processing " + entry);
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
