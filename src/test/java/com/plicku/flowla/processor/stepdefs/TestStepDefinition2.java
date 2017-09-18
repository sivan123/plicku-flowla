package com.plicku.flowla.processor.stepdefs;

import com.plicku.flowla.anotations.operators.Given;
import com.plicku.flowla.anotations.operators.If;
import com.plicku.flowla.anotations.parameters.JSONParameter;
import com.plicku.flowla.anotations.parameters.YAMLParameter;
import com.plicku.flowla.anotations.types.StepDefinitions;
import com.plicku.flowla.processor.beans.SimpleTestBean;

@StepDefinitions
public class TestStepDefinition2 {

   @Given("I have (\\d+) (.*) in my basket")
    public void givenIhaveInMyBasket(int number, String item)
   {


       System.out.println(number + item);
   }


    @Given("Test with simple bean json matchedMethod param and a int arg with value (\\d+)")
    public void givenJsonAnnotatedWithPrimitiveParam(@JSONParameter SimpleTestBean simpleTestBean, int item)
    {

    }

    @Given("Test with simple bean yaml matchedMethod param and a string arg with value (.*) and  Integer Arg with value (\\d+)")
    public void methodMapWIthYamlBeanAndStringArgAndIntegerArg(@YAMLParameter SimpleTestBean simpleTestBean, String param1, Integer param2)
    {
        System.out.println(simpleTestBean);
        System.out.println(param1);
        System.out.println(param2);
    }


}
