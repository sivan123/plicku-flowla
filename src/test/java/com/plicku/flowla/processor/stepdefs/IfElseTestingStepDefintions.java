package com.plicku.flowla.processor.stepdefs;

import com.plicku.flowla.anotations.operators.Given;
import com.plicku.flowla.anotations.operators.If;
import com.plicku.flowla.anotations.parameters.JSONParameter;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.processor.MethodCallRegistryEntry;
import com.plicku.flowla.processor.StepinProcessorTest;
import com.plicku.flowla.processor.beans.SimpleTestBean;
import org.junit.Assert;

@StepDefinitions
public class IfElseTestingStepDefintions {


    @If("Testing (\\d+) plus (\\d+) equals (\\d+)")
    public boolean additionCheck(int x,int y,int z)
    {
        StepinProcessorTest.ifElseTestMethodRegistry.get("Testing "+x+" plus "+y+" equals "+z).setActuallycalled(true);
        if((x+y)==z){
            System.out.println(x+" + "+y+" = "+z+" returning true");
            return true;
        }
        else{
            System.out.println(x+" + "+y+" = "+z+" returning false");
            return false;
        }
    }


    @Given("Simple Test for If with value (\\d+)")
    public void simpleGivenTestIfWithArg(int x)
    {
        StepinProcessorTest.ifElseTestMethodRegistry.get("Simple Test for If with value "+x).setActuallycalled(true);
    }

    @Given("Simple Test for If with value (\\d+) and Json param")
    public void simpleGivenTestIfWithArg(int x, @JSONParameter SimpleTestBean simpleTestBean)
    {
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean1,simpleTestBean);
        StepinProcessorTest.ifElseTestMethodRegistry.get("Simple Test for If with value "+x+" and Json param").setActuallycalled(true);
    }


}
