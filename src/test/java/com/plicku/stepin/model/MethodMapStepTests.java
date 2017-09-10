package com.plicku.stepin.model;

import com.plicku.stepin.anotations.operators.Given;
import com.plicku.stepin.anotations.types.StepDefinitions;

@StepDefinitions
public class MethodMapStepTests {

    @Given("Simple Step with no param")
    public void methodMapStepTests_simpleStepWithNoParam()
    {

    }

    @Given("Simple Step with one param which is (.*)")
    public void methodMapStepTests_simpleStepWithOneParam(String param)
    {
    }

    @Given("Simple Step with two param which is (.*) and (\\d+)")
    public void methodMapStepTests_simpleStepWithTwoParam(String param,int param2)
    {

    }
}
