package com.plicku.flowla.processor.stepdefs;

import com.plicku.flowla.processor.MethodCallRegistryEntry;
import com.plicku.flowla.processor.StepinProcessor;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ForeachStepDefintionsTest {

     StepinProcessor   stepinProcessor = new StepinProcessor("com.plicku.flowla.processor.stepdefs");

    public ForeachStepDefintionsTest() throws InstantiationException, IllegalAccessException {
    }

    List<MethodCallRegistryEntry> methodRegistryExp = new ArrayList<>();
    List<MethodCallRegistryEntry> methodRegistryAct = new ArrayList<>();


    @Test
    public void foreachTest() throws Exception
    {


        stepinProcessor.process(new File(this.getClass().getClassLoader().getResource("foreachtest.flowla").getFile()));
    }

}