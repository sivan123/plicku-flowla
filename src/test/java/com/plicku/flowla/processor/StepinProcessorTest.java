package com.plicku.flowla.processor;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class StepinProcessorTest {

    static StepinProcessor stepinProcessor;

    @BeforeClass
    public static void init() throws InstantiationException, IllegalAccessException {
        stepinProcessor = new StepinProcessor("com.plicku.flowla.processor.stepdefs");
    }

    public static List<MethodCallRegistryEntry> methodRegistryExp = new ArrayList<>();
    public static List<MethodCallRegistryEntry> methodRegistryAct = new ArrayList<>();
    @Test
    public void testSimpleProcessingFromFile() throws Exception {

        methodRegistryExp.add(new MethodCallRegistryEntry("Simple Given Test",true));
        methodRegistryExp.add(new MethodCallRegistryEntry("Simple When Test",true));
        methodRegistryExp.add(new MethodCallRegistryEntry("Test with simple bean matchedMethod param",true));
        methodRegistryExp.add(new MethodCallRegistryEntry("Test with simple bean json matchedMethod param",true));
        methodRegistryExp.add(new MethodCallRegistryEntry("Simple When Test",true));
        methodRegistryExp.add(new MethodCallRegistryEntry("Test with list of beans json matchedMethod param",true));


        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("test1.flowla").getFile()));
        Assert.assertEquals("Method Call expectation failed",methodRegistryExp,methodRegistryAct);
    }

    @Test
    public void testRegexExpressions() throws Exception {
        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("test2regex.flowla").getFile()));
    }

    @Test
    public void methodMapWIthYamlBeanAndStringArgAndIntegerArg() throws Exception{

        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("test2regex.flowla").getFile()));
    }

    @Test
    public void ifTest() throws Exception{
        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("ifTest.flowla").getFile()));

    }


}