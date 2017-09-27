package com.plicku.flowla.util;

import com.plicku.flowla.exceptions.FlowContentParsingException;
import com.plicku.flowla.exceptions.ValidationException;
import com.plicku.flowla.model.vo.FlowContentEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static List<FlowContentEntry> getFlowConentSteps(String flowContent, String delimter) throws FlowContentParsingException {


        flowContent=Arrays.asList(flowContent.split(System.lineSeparator())).stream().filter(s -> !s.startsWith(COMMENT)).collect(Collectors.joining(System.lineSeparator()));
        List<FlowContentEntry> entries = new ArrayList<>();
        String[] entryStr = flowContent.split(String.format(WITH_DELIMITER, delimter));
        String declaredVariable=null;
        int depth=1;
        for (int i = 0; i < entryStr.length; i++) {
            try{

            if("".equals(entryStr[i].trim())) continue;
            String keyword=(END_IF.equals(entryStr[i].trim())|| OTHERWISE.equals(entryStr[i].trim()) || END_FOR.equals(entryStr[i].trim())) ?entryStr[i].trim():entryStr[i].trim()+" ";
            String stepname="";
            StringBuilder stringBuilder = new StringBuilder();
            if(!(END_IF.equals(keyword)||END_FOR.equals(keyword)||END_REPEAT.equals(keyword))){

                String[] stepNamedata = StringUtils.split(entryStr[i+1], System.lineSeparator());
                boolean stepNameSet=false;
                for (int j = 0; j < stepNamedata.length; j++) {
                    if(!"".equals(stepNamedata[j].trim())&&!stepNameSet)
                    {
                        stepname=stepNamedata[j];
                        stepNameSet=true;
                    }
                    else if(stepNameSet && !"".equals(stepNamedata[j]))
                    {
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
        return entries;

    }

    public void validateFlowContentList(List<FlowContentEntry> entries) throws ValidationException {
       //check for valid
        List<FlowContentEntry> invalidKeyWordEntries = entries.stream().filter(FlowContentEntry::isNonValidKeyWord).collect(Collectors.toList());
        if (invalidKeyWordEntries.size()>0) throw new ValidationException ("Error parsing Flow Content. Invalid Steps detected. " +
                "Check if the keywords are valid and the step is defined after the key word. "+invalidKeyWordEntries);
        //Check for matching ifLoops
        Long ifCOunts= entries.stream().filter(FlowContentEntry::isIf).count();
        Long endifCOunts= entries.stream().filter(FlowContentEntry::isEndIf).count();
        if(ifCOunts!=endifCOunts) throw new ValidationException("Error Parsing the Flow Content. Check \"If\" statements are closed by a matching \"EndIf\".");

        //check for matching closing
        //check for each steps return a collection or a Datatable
        //check if/else if returns a boolean
        //check repeat returns an integer or long
        //check repeat until returns a boolean
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
