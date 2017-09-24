package com.plicku.flowla.model;

import com.plicku.flowla.anotations.operators.Given;
import com.plicku.flowla.processor.StepinProcessor;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MethodMapTest {

    StepinProcessor stepinProcessor = null;


    @Before
    public void setUp() throws InstantiationException, IllegalAccessException {
        System.out.println("setting up..");
        stepinProcessor = new StepinProcessor("com.plicku.flowla.model");
    }


    @Test
    public void withNoRegexParams() throws Exception {
        Reflections reflections = new Reflections("com.plicku.flowla.model", new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
        Method method = reflections.getMethodsAnnotatedWith(Given.class).stream().filter(m -> m.getName().equals("methodMapStepTests_simpleStepWithNoParam")).findAny().get();
        assertEquals(method,stepinProcessor.methodMap.get("Simple Step with no param").getMatchedMethod());
        assertEquals(null,stepinProcessor.methodMap.get("Simple Step with no param").getStepAurguments());
    }


    @Test
    public void withOneRegexParam() throws Exception {
        Reflections reflections = new Reflections("com.plicku.flowla.model", new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
        Method method = reflections.getMethodsAnnotatedWith(Given.class).stream().filter(m -> m.getName().equals("methodMapStepTests_simpleStepWithOneParam")).findAny().get();
        assertEquals(method,stepinProcessor.methodMap.get("Simple Step with one param which is One").getMatchedMethod());
        assertEquals("One", stepinProcessor.methodMap.get("Simple Step with one param which is One").getStepAurguments().get(0));
    }
    @Test
    public void withTwoRegexParam() throws Exception {
        Reflections reflections = new Reflections("com.plicku.flowla.model", new MethodAnnotationsScanner(),new TypeAnnotationsScanner(), new SubTypesScanner());
        Method method = reflections.getMethodsAnnotatedWith(Given.class).stream().filter(m -> m.getName().equals("methodMapStepTests_simpleStepWithTwoParam")).findAny().get();
        assertEquals(method,stepinProcessor.methodMap.get("Simple Step with two param which is Apple and 23").getMatchedMethod());
        assertEquals(method,stepinProcessor.methodMap.get("Simple Step with two param which is Apple and 23").getMatchedMethod());
        assertEquals("Apple", stepinProcessor.methodMap.get("Simple Step with two param which is Apple and 23").getStepAurguments().get(0));
        assertEquals(23, Integer.parseInt(stepinProcessor.methodMap.get("Simple Step with two param which is Apple and 23").getStepAurguments().get(1)));

    }
}