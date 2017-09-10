package com.plicku.stepin.processor.stepdefs;

import com.plicku.stepin.anotations.Given;
import com.plicku.stepin.anotations.JSONParameter;
import com.plicku.stepin.anotations.StepDefinitions;
import com.plicku.stepin.processor.beans.SimpleTestBean;

@StepDefinitions
public class TestStepDefinition2 {

   @Given("Given I have (\\d+) (.*) in my basket")
    public void givenIhaveInMyBasket(int number, String item)
   {
       System.out.println(number + item);
   }


    @Given("Test with simple bean json matchedMethod param and a int arg with value 10")
    public void givenJsonAnnotatedWithPrimitiveParam(@JSONParameter SimpleTestBean simpleTestBean, int item)
    {

    }
}
