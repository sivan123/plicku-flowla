package com.plicku.flowla.model;

import com.plicku.flowla.model.contexts.VariableMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;

public class StepMethodPropertiesTest {
    @Test
    public void getNextArgValue() throws Exception {
        SoftAssertions assertions = new SoftAssertions();
        StepMethodProperties stepMethodProperties = new StepMethodProperties();
        stepMethodProperties.setStepAurguments(Arrays.asList("10","${item}","${basket.name}","${basket.basketItems[0]}"));
        VariableMap variableMap = new VariableMap();
        assertions.assertThat(stepMethodProperties.getNextArgValue(int.class,-1,variableMap)).isEqualTo(10);
        variableMap.setVariable("item","apple");
        assertions.assertThat(stepMethodProperties.getNextArgValue(String.class,0,variableMap)).isEqualTo("apple");
        ParamTestBean paramTestBean = new ParamTestBean();
        variableMap.setVariable("basket", paramTestBean);
        assertions.assertThat(stepMethodProperties.getNextArgValue(String.class,1,variableMap)).isEqualTo("gifthamper");
        assertions.assertThat(stepMethodProperties.getNextArgValue(String.class,2,variableMap)).isEqualTo("pen");


        assertions.assertAll();
    }


}