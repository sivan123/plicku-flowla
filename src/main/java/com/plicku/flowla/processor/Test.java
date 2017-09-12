package com.plicku.flowla.processor;

import com.plicku.flowla.util.Argument;
import com.plicku.flowla.util.PatternArgumentMatcher;

import java.util.List;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) throws Exception {

        String x ="I have <10> pens in my <table> and <20> pencils in my cuboard";
        String p = "I have <(\\d+)> pens in my <(.*)> and <(\\d+)> pencils in my (.*)";
        Pattern pPatter = Pattern.compile(p);
        System.out.println(pPatter.matcher(x).matches());
        List<Argument> arguments = new PatternArgumentMatcher(pPatter).argumentsFrom(x);












    }
}
