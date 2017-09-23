package com.plicku.flowla.util;

import com.plicku.flowla.model.contexts.Argument;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PatternArgumentMatcherTest {
    @Test
    public void argumentsFrom() throws Exception {

        SoftAssertions assertions = new SoftAssertions();
        PatternArgumentMatcher patternArgumentMatcher1 = new PatternArgumentMatcher(Pattern.compile("Testing (\\d+) plus (\\d+) equals (\\d+)"));
        assertions.assertThat(patternArgumentMatcher1.argumentsFrom("Testing 5 plus 4 equals 9")
                .stream().map(Argument::getVal).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("5","4","9"));

        PatternArgumentMatcher patternArgumentMatcher2 = new PatternArgumentMatcher(Pattern.compile("Testing ([-+]?[0-9]*\\.?[0-9]+) plus ([-+]?[0-9]*\\.?[0-9]+) equals (\\d+)"));
        assertions.assertThat(patternArgumentMatcher2.argumentsFrom("Testing 5.5 plus 4.5 equals 10")
                .stream().map(Argument::getVal).collect(Collectors.toList()))
                .isEqualTo(Arrays.asList("5.5","4.5","10"));

        assertions.assertAll();

    }

}