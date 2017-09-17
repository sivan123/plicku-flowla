package com.plicku.flowla.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.flowla.anotations.operators.*;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.model.MethodMap;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.model.vo.FlowContentEntry;
import com.plicku.flowla.util.StepContentParserUitl;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.plicku.flowla.util.Constants.ALL_KEYWORDS;
import static com.plicku.flowla.util.Constants.KEYWD_BEGIN_PTTN;
import static com.plicku.flowla.util.Constants.PROCESS_KEYWORDS;

public class StepinProcessor {

    public static GlobalContext globalContext = new GlobalContext();
    public static Map<Class,Object> classMap = new ConcurrentHashMap<>();
    public static MethodMap methodMap = new MethodMap();
    public static final String keywordRegex = KEYWD_BEGIN_PTTN+ALL_KEYWORDS.stream().collect(Collectors.joining("|"+KEYWD_BEGIN_PTTN));
    private Pattern flowKeywordPattern = Pattern.compile(keywordRegex);
    public static final ObjectMapper objectMapper = new ObjectMapper();

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
        List<FlowContentEntry> ifOrElseifEntriesToProcess = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            FlowContentEntry entry = entries.get(i);
            try {
                if (entry.isIfOrElseIf()) {
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    Boolean if_elseif_result = (Boolean) stepExecutor.executeMethod(sequenceContext);
                    if (if_elseif_result) {
                        while (!entries.get(i+1).isEndIfOrElseIf()){
                            i++;
                            ifOrElseifEntriesToProcess.add(entries.get(i));
                        }
                    } else //skip until end if or else if
                    {
                        while (!entries.get(i+1).isEndIfOrElseIf()){
                                i++;
                        }
                    }
                }
                else if(entry.isEndIf() && ifOrElseifEntriesToProcess.size()>0)
                {
                    process(ifOrElseifEntriesToProcess);
                }
                else if (PROCESS_KEYWORDS.contains(entry.getKeyword())) {
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    stepExecutor.executeMethod(sequenceContext);
                }
            } catch (Exception e) {
                System.out.println("Exception Processing " + entry);
                e.printStackTrace();
            }
        }
    }

    private StepExecutor getStepExecutor(FlowContentEntry entry) throws Exception
    {
        StepExecutor stepExecutor = new StepExecutor();
        StepMethodProperties stepMethodProperties = methodMap.get(entry.getStepName());
        stepExecutor.setParamData(entry.getData());
        if (stepMethodProperties == null) throw new Exception("Unable to find step definition for " + entry.getStepName());
        stepExecutor.setStepMethodProperties(stepMethodProperties);
        return stepExecutor;
    }



}
