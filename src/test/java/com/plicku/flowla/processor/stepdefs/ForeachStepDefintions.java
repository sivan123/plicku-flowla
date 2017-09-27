package com.plicku.flowla.processor.stepdefs;

import com.plicku.flowla.anotations.operators.ForEach;
import com.plicku.flowla.anotations.operators.Then;
import com.plicku.flowla.anotations.types.StepDefinitions;

import java.util.Arrays;
import java.util.List;

@StepDefinitions
public class ForeachStepDefintions
{

    @ForEach("items of basket")
    public List<String> getItemsinBasket()
    {

        return Arrays.asList("Pen","Pencil","Flower","Bottle","Apple");
    }

    @Then("basket has (.*)")
    public void baskethas(String item)
    {
        System.out.println("basket has "+item);
    }

    @Then("simple print")
    public void printsimple()
    {
        System.out.println("hello");
    }

    @Then("(.*) lives at (.*) in (.*)")
    public void printNameLivesIn(String name,String address1,String address2)
    {
        System.out.println(String.format("%s lives at %s in %s",name,address1,address2));
    }
}
