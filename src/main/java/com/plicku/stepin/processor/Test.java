package com.plicku.stepin.processor;

import com.plicku.stepin.util.Argument;
import com.plicku.stepin.util.PatternArgumentMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
