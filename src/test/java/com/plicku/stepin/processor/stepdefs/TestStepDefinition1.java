package com.plicku.stepin.processor.stepdefs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.plicku.stepin.anotations.operators.Given;
import com.plicku.stepin.anotations.types.StepDefinitions;
import com.plicku.stepin.processor.MethodCallRegistryEntry;
import com.plicku.stepin.processor.StepinProcessor;
import com.plicku.stepin.processor.StepinProcessorTest;
import com.plicku.stepin.processor.beans.SimpleTestBean;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

@StepDefinitions
public class TestStepDefinition1 {

    @Given("Simple Given Test")
    public void simpleGivenTest()
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
    }
    @Given("Simple When Test")
    public void simpleWhenTest()
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
    }
    @Given("Simple Then Test")
    public void simpleThenTest()
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
    }

    @Given("Test with simple bean matchedMethod param")
    public void testWithSimpleBeanMethodParam(SimpleTestBean simpleTestBean)
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean1,simpleTestBean);
    }

    @Given("Test with simple bean json matchedMethod param")
    public void testWithSimpleBeanJsonMethodParam(SimpleTestBean simpleTestBean)
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

    @Given("Test with list of beans json matchedMethod param")
    public void testWithSimpleBeanJsonMethodParam(List<SimpleTestBean> simpleTestBeans)
    {
        StepinProcessorTest.methodRegistryAct.add(new MethodCallRegistryEntry(new Object(){}.getClass().getEnclosingMethod().getDeclaredAnnotation(Given.class).value(),true));
        List<SimpleTestBean> pojos = StepinProcessor.objectMapper.convertValue(simpleTestBeans, new TypeReference<List<SimpleTestBean>>() { });
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        SimpleTestBean simpleTestBean2 = new SimpleTestBean("TestName1","TestAddress2",30879);
        List<SimpleTestBean> expectedList = new ArrayList<>();
        expectedList.add(simpleTestBean1);
        expectedList.add(simpleTestBean2);
        Assert.assertEquals(pojos,expectedList);

    }

}
