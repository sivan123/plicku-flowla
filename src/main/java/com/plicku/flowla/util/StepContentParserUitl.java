package com.plicku.flowla.util;

import com.plicku.flowla.exceptions.FlowContentParsingException;
import com.plicku.flowla.exceptions.ValidationException;
import com.plicku.flowla.model.FlowValidationError;
import com.plicku.flowla.model.vo.FlowContentEntry;
import com.plicku.flowla.processor.StepinProcessor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.plicku.flowla.util.Constants.*;

public class StepContentParserUitl
{

    public static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    private static final String VARIABLE_DECLARATION_AS= ".*\\sas\\s\\$\\{(.*)\\s*$}";
    private static final String VARIABLE_DECLARATION_IN= "^\\$\\{(.*)}\\s+in\\s+";

    final static Pattern variableAsDeclarePattern = Pattern.compile(VARIABLE_DECLARATION_AS);
    final static Pattern variableInDeclarePattern = Pattern.compile(VARIABLE_DECLARATION_IN+".*");

    public static List<FlowContentEntry> getFlowConentSteps(String flowContent, String delimter, boolean validate) throws FlowContentParsingException, ValidationException {
        AtomicInteger lineNumCounter = new AtomicInteger(0);

        flowContent=Arrays.asList(flowContent.split("\n")).stream().map(s -> s+LINE_NUM_SEPERATOR+lineNumCounter.incrementAndGet()).collect(Collectors.joining(System.lineSeparator()));
        List<FlowContentEntry> entries = new ArrayList<>();
        String[] entryStr = flowContent.split(String.format(WITH_DELIMITER, delimter));
        String declaredVariable=null;
        int depth=1;
        for (int i = 0; i < entryStr.length; i++) {
            try{
            if(entryStr[i].trim().startsWith(COMMENT)) continue;
            if("".equals(entryStr[i].replaceAll(LINE_NUM_SEPERATOR+"\\d*","").trim())) continue;
            String keyword=(END_IF.equals(entryStr[i].trim())|| OTHERWISE.equals(entryStr[i].trim()) || END_FOR.equals(entryStr[i].trim())) ?entryStr[i].trim():entryStr[i].trim()+" ";
            String stepname="";
            StringBuilder stringBuilder = new StringBuilder();
            int j = 0;
            if(!(END_IF.equals(keyword)||END_FOR.equals(keyword)||END_REPEAT.equals(keyword))){
                String[] stepNamedata = entryStr[i+1].split(System.lineSeparator());
                boolean stepNameSet=false;
                for (j = 0; j < stepNamedata.length; j++) {
                    if(!"".equals(stepNamedata[j].trim())&&!stepNameSet)
                    {
                        stepname=stepNamedata[j];
                        stepNameSet=true;
                    }
                    else if(stepNameSet && !"".equals(stepNamedata[j]))
                    {
                        stepNamedata[j]=stepNamedata[j].replaceAll(LINE_NUM_SEPERATOR+"\\d*","");
                        if ("".equals(stringBuilder.toString())) {
                            stringBuilder.append(stepNamedata[j]);
                        } else {
                            stringBuilder.append(System.lineSeparator()).append(stepNamedata[j]);
                        }
                    }
                }
            }

            if(IF.equals(keyword)){
                depth++;
            }
            else if (FOR_EACH.equals(keyword))
            {
                depth++;
                declaredVariable=getInDeclaredVariable(stepname);
                if(declaredVariable!=null)
                    stepname=stepname.replaceAll(VARIABLE_DECLARATION_IN,"");
            }
            else if(END_IF.equals(keyword) || END_FOR.equals(keyword) || END_REPEAT.equals(keyword))
            {
                depth--;
            }


            FlowContentEntry entry = new FlowContentEntry(keyword,stepname,stringBuilder.toString(),declaredVariable,depth);
            i++;
            entries.add(entry);
            }catch (Exception e){
                throw new FlowContentParsingException("Error parsing block "+Arrays.asList(entryStr));
            }
        }
        if(validate){
            List<FlowValidationError> errors =validateFlowContentList(entries);
            if(errors.size()>0)
                throw new ValidationException("Validation Errors in the flow definition - "+errors.toString());
        }

        return entries;

    }

    public static List<FlowValidationError> validateFlowContentList(List<FlowContentEntry> entries)  {


        List<FlowValidationError> errors = new ArrayList<>();
        if(entries==null||entries.size()==0)
            return errors;
        //check for matching closing
        if(entries.get(entries.size()-1).getDepth()==0)
            errors.add(new FlowValidationError("Opening Key Words not matching with closing ones"));
        //check for each steps return a collection or a Datatable
        entries.forEach(entry -> {
            if (entry.isEndFor() || entry.isEndIf() || entry.isEndRepeat() || entry.isOtherWise()){
                if(!(entry.getStepName()==null||entry.getStepName().isEmpty())) errors.add(new FlowValidationError(entry.getKeyword()+" "+entry.getStepName(),"Invalid Step. Step not allowed with the keyword "+entry.getKeyword()+". Consider moving '"+entry.getStepName()+"' to the next line along with an appropriate processing key word."));
            }
            else {
                if(StepinProcessor.methodMap.get(entry.getStepName())==null)
                    System.out.println(entry.getStepName());
                Class aClass = StepinProcessor.methodMap.get(entry.getStepName()).getMethodReturnType();
                if (entry.isIfOrElseIf() || entry.isRepeatWhile()) {
                    if (!(Boolean.class.isAssignableFrom(aClass) || boolean.class.isAssignableFrom(aClass)))
                        errors.add(new FlowValidationError(entry.getStepName(), "Invalid Step Definition. Method is expected to return a true or false"));
                }
                if (entry.isRepeatFor()) {
                    if (!(Number.class.isAssignableFrom(aClass)))
                        errors.add(new FlowValidationError(entry.getStepName(), "Invalid Step Definition. Method is expected to return a number"));
                }
            }
        });

        return errors;
    }

    public static String getInDeclaredVariable(String stepName)
    {
        Matcher matcher=variableInDeclarePattern.matcher(stepName);
        if (matcher.lookingAt()) {
            System.out.println(matcher.groupCount());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                return matcher.group(i);
            }
        }
        return null;
    }



}
