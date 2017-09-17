package com.plicku.flowla.util;

import com.plicku.flowla.model.vo.FlowContentEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StepContentParserUitl
{

    static public final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
    public static List<FlowContentEntry> getFlowConentSteps(String flowContent, String delimter)
    {

        flowContent=Arrays.asList(flowContent.split(System.lineSeparator())).stream().filter(s -> !s.startsWith(Constants.COMMENT)).collect(Collectors.joining(System.lineSeparator()));
        List<FlowContentEntry> entries = new ArrayList<>();
        String[] entryStr = flowContent.split(String.format(WITH_DELIMITER, delimter));

        for (int i = 0; i < entryStr.length; i++) {
            if("".equals(entryStr[i].trim())) continue;
            String keyword=entryStr[i].trim()+" ";
            String stepname="";
            StringBuilder stringBuilder = new StringBuilder();
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
            FlowContentEntry entry = new FlowContentEntry(keyword,stepname,stringBuilder.toString());
            i++;
            entries.add(entry);
        }
        return entries;

    }

    public static boolean validateFlowContentList(List<FlowContentEntry> entries)
    {
        boolean isValid = false;
        //Check for matching ifLoops

        return isValid;

    }



}
