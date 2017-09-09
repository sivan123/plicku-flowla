package com.plicku.stepin.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.stepin.anotations.Given;
import com.plicku.stepin.anotations.StepDefinitions;
import com.plicku.stepin.model.MethodExecutor;
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
    public static Map<String,Method> methodMap = new ConcurrentHashMap();
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

        MethodExecutor methodExecutor =null;
        for (String line:lines) {
            line=line.trim();
            if(Stream.of("Given ","When ","Then ","And ").anyMatch(line::startsWith))
            {
                if(methodExecutor !=null && methodExecutor.isMethodToBeExecuted())
                {
                    //complete previous step execution if pending
                    methodExecutor.executeMethod();
                }
                methodExecutor = new MethodExecutor();
                Matcher matcher =flowKeywordPattern.matcher(line);
                matcher.find();
                String registeredType=matcher.group(0);
                line = line.replaceFirst(registeredType,"").trim();
                methodExecutor.setInstructionType(StepinEnums.InstructionType.valueOf(registeredType.trim()));
                Method method = methodMap.get(line);
                if(method==null) throw new Exception("Unable to find step definition for "+line);
                methodExecutor.setMethod(method);
            }
            else if (line.startsWith("|")) //data table
            {
                //read the first row as headers
                if(!methodExecutor.isDataTableHeadersRegistered())
                    methodExecutor.setDataTableHeaders(StringUtils.split(line,"\\|"));
                else
                    methodExecutor.setDataTableData(StringUtils.split(line,"\\|"));
            }
            else if((line.startsWith("{")||line.startsWith("["))&& methodExecutor !=null&& methodExecutor.isMethodToBeExecuted())
            {
                //json
                methodExecutor.setJsonStringRegistered(true);
                methodExecutor.getJson().append(line);
            }
            else if(methodExecutor.isJsonStringRegistered())
            {
                methodExecutor.getJson().append(line);
            }
        }
        //finish any pending method exec.
        if(methodExecutor.isMethodToBeExecuted())
            methodExecutor.executeMethod();
    }



}
