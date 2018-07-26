package com.plicku.flowla.util;

import com.plicku.flowla.model.vo.FlowContentEntry;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StepContentParserUitlTest {
    @Test
    public void getFlowConentSteps() throws Exception {
        String delimter = Constants.ALL_KEYWORDS.stream().collect(Collectors.joining("|"));
        String flowContent1 = "\n" +
                "\n" +
                "Given I am the first step\n" +
                "   |testdatastep1|\n" +
                "Then I am the second step with JSON Param\n" +
                "{\"x\":123,\"y\":124}\n" +
                "\n" +
                "And I am the third step\n" +
                "\n";

        List<FlowContentEntry> entryList1 = StepContentParserUitl.getFlowConentSteps(flowContent1, delimter,false);
        SoftAssertions assertions = new SoftAssertions();
        assertions.assertThat(entryList1.get(0)).isEqualTo(new FlowContentEntry("Given ","I am the first step","|testdatastep1|",1,3));
        assertions.assertThat(entryList1.get(1)).isEqualTo(new FlowContentEntry("Then ","I am the second step with JSON Param","{\"x\":123,\"y\":124}",1,5));
        assertions.assertThat(entryList1.get(2)).isEqualTo(new FlowContentEntry("And ","I am the third step","",1,8));

        String flowContent2 = "If Testing 5 plus 5 equals 10\n" +
                "Then I have 10 eggs in my basket\n" +
                "EndIf";

        List<FlowContentEntry> entryList2 = StepContentParserUitl.getFlowConentSteps(flowContent2, delimter,false);
        assertions.assertThat(entryList2.get(0)).isEqualTo(new FlowContentEntry("If ","Testing 5 plus 5 equals 10","",2,1));
        assertions.assertThat(entryList2.get(1)).isEqualTo(new FlowContentEntry("Then ","I have 10 eggs in my basket","",2,2));

        assertions.assertAll();
    }

    @Test
    public void validateFlowContentList() throws Exception {

    }

}