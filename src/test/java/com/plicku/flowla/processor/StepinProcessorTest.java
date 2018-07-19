package com.plicku.flowla.processor;

import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String,MethodCallRegistryEntry> ifElseTestMethodRegistry = new HashMap();
    public static List<MethodCallRegistryEntry> ifElseTestMmethodRegistryAct = new ArrayList<>();

    private void ifElseTestMethodRegistrySetUp(MethodCallRegistryEntry methodCallRegistryEntry)
    {
        ifElseTestMethodRegistry.put(methodCallRegistryEntry.getName(),methodCallRegistryEntry);
    }
    @Test
    public void ifElseTest() throws Exception{

        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 15 plus 15 equals 30",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 30",true,false));

        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 25 plus 25 equals 51",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 51",false,false));

        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 5 plus 5 equals 10",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 1 and Json param",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 4 plus 4 equals 8",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 2 and Json param",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 3 plus 3 equals 6",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 6",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 7 plus 7 equals 13",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 13",false,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 7 plus 7 equals 15",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 15",false,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 2 plus 2 equals 3",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 3",false,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 16",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 17",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 3 plus 3 equals 7",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 7",false,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Testing 9 plus 9 equals 18",true,false));
        ifElseTestMethodRegistrySetUp(new MethodCallRegistryEntry("Simple Test for If with value 18",true,false));
        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("ifTest.flowla").getFile()));
        SoftAssertions assertions = new SoftAssertions();
        ifElseTestMethodRegistry.forEach((s, methodCallRegistryEntry) -> {
            assertions.assertThat(methodCallRegistryEntry.called).as(methodCallRegistryEntry.name).isEqualTo(methodCallRegistryEntry.expectedToBeCalled);
        });
        assertions.assertAll();
    }




}