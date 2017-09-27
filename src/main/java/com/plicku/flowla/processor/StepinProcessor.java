package com.plicku.flowla.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plicku.flowla.anotations.operators.*;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.exceptions.ProcessingException;
import com.plicku.flowla.model.DataTable;
import com.plicku.flowla.model.MethodMap;
import com.plicku.flowla.model.StepMethodProperties;
import com.plicku.flowla.model.contexts.GlobalContext;
import com.plicku.flowla.model.contexts.SequenceContext;
import com.plicku.flowla.model.contexts.VariableMap;
import com.plicku.flowla.model.vo.FlowContentEntry;
import com.plicku.flowla.util.StepContentParserUitl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.plicku.flowla.util.Constants.*;

public class StepinProcessor {

    public static GlobalContext globalContext = new GlobalContext();
    public static Map<Class,Object> classMap = new ConcurrentHashMap<>();
    public static MethodMap methodMap = new MethodMap();
    public static final String keywordRegex = KEYWD_BEGIN_PTTN+ALL_KEYWORDS.stream().collect(Collectors.joining("|"+KEYWD_BEGIN_PTTN));
    private Pattern flowKeywordPattern = Pattern.compile(keywordRegex);
    public static final ObjectMapper objectMapper = new ObjectMapper();




    public StepinProcessor(String... stepdefpackages) throws IllegalAccessException, InstantiationException {

        List<String> stepdefs = new ArrayList<>(Arrays.asList(stepdefpackages));
        stepdefs.add("com.plicku.flowla.stepdefintions");
        for (String s : stepdefs) {
            populateMethodAndClassMap(s);
        }

    }

    private void populateMethodAndClassMap(String packagename) throws IllegalAccessException, InstantiationException {
        Reflections reflections = new Reflections(packagename, new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
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
        methods = reflections.getMethodsAnnotatedWith(ForEach.class);
        methods.forEach(method -> {
            methodMap.put(method.getDeclaredAnnotation(ForEach.class).value(),method);
        });

    }

    private StepinProcessor(){}

    public void process(String flowContentStr) throws Exception {

        List<FlowContentEntry> contentBlocks = StepContentParserUitl.getFlowConentSteps(flowContentStr,keywordRegex);
        VariableMap variableMap = new VariableMap();
        process(contentBlocks,variableMap);

    }
    public void process(File storyFile) throws Exception {

        String fileContentStr = FileUtils.readFileToString(storyFile,"UTF-8");
        process(fileContentStr);

    }

    public void process(List<FlowContentEntry> entries, VariableMap variableMap) throws Exception {
        SequenceContext sequenceContext = new SequenceContext();
        List<FlowContentEntry> ifOrElseifOrElseEntriesToProcess = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            FlowContentEntry entry = entries.get(i);
            try {
                if (entry.isIfOrElseIf()) {

                    if(ifOrElseifOrElseEntriesToProcess.size()>0)
                    {
                        process(ifOrElseifOrElseEntriesToProcess,variableMap);
                        ifOrElseifOrElseEntriesToProcess.clear();
                    }
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    Boolean if_elseif_result = (Boolean) stepExecutor.executeMethod(sequenceContext,variableMap);
                    if (if_elseif_result) {
                       while (!((entries.get(i+1).getDepth()==entry.getDepth()-1 && entries.get(i+1).isEndIf()) ||
                                (entries.get(i+1).getDepth()==entry.getDepth() && entries.get(i+1).isElseIf()) ||
                               (entries.get(i+1).getDepth()==entry.getDepth() && entries.get(i+1).isOtherWise())
                              ))
                       {
                            i++;
                            ifOrElseifOrElseEntriesToProcess.add(entries.get(i));
                        }
                    } else //skip until EndIf or else if
                    {
                        while(!((entries.get(i+1).getDepth()==entry.getDepth()-1 && entries.get(i+1).isEndIf()) ||
                                (entries.get(i+1).getDepth()==entry.getDepth() && entries.get(i+1).isElseIf()) ||
                                (entries.get(i+1).getDepth()==entry.getDepth() && entries.get(i+1).isOtherWise())
                                ))
                        {
                                i++;
                        }
                    }
                }
                else if(entry.isOtherWise())
                {
                    while (!(entries.get(i+1).getDepth()==entry.getDepth()-1 && entries.get(i+1).isEndIf()))
                    {
                        i++;
                        ifOrElseifOrElseEntriesToProcess.add(entries.get(i));
                    }
                }
                else if(entry.isEndIf() && ifOrElseifOrElseEntriesToProcess.size()>0)
                {
                    process(ifOrElseifOrElseEntriesToProcess,variableMap);
                    ifOrElseifOrElseEntriesToProcess.clear();
                }
                else if(entry.isForEach()){
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    List<FlowContentEntry> forEachEntriestoProcess = new ArrayList<>();
                    Object result=stepExecutor.executeMethod(sequenceContext,variableMap);
                    while (!(entries.get(i + 1).getDepth() == entry.getDepth() - 1 && entries.get(i + 1).isEndFor())) {
                        i++;
                        forEachEntriestoProcess.add(entries.get(i));
                    }

                    if(DataTable.class.isAssignableFrom(result.getClass()))
                    {
                        DataTable dataTable = (DataTable) result;
                        for (int j = 0; j < dataTable.getRowMapList().size(); j++) {
                            dataTable.getRowMapList().get(j).forEach(variableMap::setVariable);
                            process(forEachEntriestoProcess, variableMap);
                        }
                    }
                    else if(Collection.class.isAssignableFrom(result.getClass())) {
                        Collection collection = (Collection) result;
                        for (int j = 0; j < collection.size(); j++) {
                            if (entry.getDeclaredVariable() != null)
                                variableMap.setVariable(entry.getDeclaredVariable(), CollectionUtils.get(collection, j));
                            process(forEachEntriestoProcess, variableMap);
                            variableMap.removeVariable(entry.getDeclaredVariable());
                        }
                    }
                    else throw new ProcessingException("Invalid Step. 'For Each' Step definitions should return a " +
                                "collection or data table object.");
                }
                else if(entry.isRepeatFor())
                {
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    List<FlowContentEntry> repeatEntriestoProcess = new ArrayList<>();
                    Number number=(Number)stepExecutor.executeMethod(sequenceContext,variableMap);
                    while (!(entries.get(i + 1).getDepth() == entry.getDepth() - 1 && entries.get(i + 1).isEndRepeat())) {
                        i++;
                        repeatEntriestoProcess.add(entries.get(i));
                    }
                        for (int j = 0; j < number.longValue(); j++) {
                            process(repeatEntriestoProcess,variableMap);
                        }
                }
                else if(entry.isRepeatWhile())
                {
                    StepExecutor stepExecutor = getStepExecutor(entry);
                    List<FlowContentEntry> repeatEntriestoProcess = new ArrayList<>();

                    while (!(entries.get(i + 1).getDepth() == entry.getDepth() - 1 && entries.get(i + 1).isEndRepeat())) {
                        i++;
                        repeatEntriestoProcess.add(entries.get(i));
                    }
                    int repetionCount=0;
                    while((Boolean)stepExecutor.executeMethod(sequenceContext,variableMap))
                    {
                        repetionCount++;
                        process(repeatEntriestoProcess,variableMap);
                        if(repetionCount> MAX_REPEAT_CNT_ALLOWED)
                            throw new ProcessingException("Maximum allowed repetion while count exceeded "+MAX_REPEAT_CNT_ALLOWED+". Potential Infinite" +
                                    " loop detected. Check step definition or increase the MAX_REPEAT_CNT_ALLOWED setting");
                    }
                }
//                else if(entry.isEndFor()||ent){}
                else if (PROCESS_KEYWORDS.contains(entry.getKeyword())) {

                    if(ifOrElseifOrElseEntriesToProcess.size()>0)
                    {
                        process(ifOrElseifOrElseEntriesToProcess,variableMap);
                        ifOrElseifOrElseEntriesToProcess.clear();
                    }

                    StepExecutor stepExecutor = getStepExecutor(entry);
                    stepExecutor.executeMethod(sequenceContext,variableMap);
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
