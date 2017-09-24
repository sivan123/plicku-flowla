package com.plicku.flowla.util;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PatternArgumentMatcherTest {
    @Test
    public void argumentsFrom() throws Exception {

        SoftAssertions assertions = new SoftAssertions();
        PatternArgumentMatcher patternArgumentMatcher1 = new PatternArgumentMatcher(Pattern.compile("Testing (\\d+) plus (\\d+) equals (\\d+)"));
        assertions.assertThat(new ArrayList<>(patternArgumentMatcher1.argumentsFrom("Testing 5 plus 4 equals 9")))
                .isEqualTo(Arrays.asList("5","4","9"));

        PatternArgumentMatcher patternArgumentMatcher2 = new PatternArgumentMatcher(Pattern.compile("Testing ([-+]?[0-9]*\\.?[0-9]+) plus ([-+]?[0-9]*\\.?[0-9]+) equals (\\d+)"));
        assertions.assertThat(new ArrayList<>(patternArgumentMatcher2.argumentsFrom("Testing 5.5 plus 4.5 equals 10")))
                .isEqualTo(Arrays.asList("5.5","4.5","10"));

        assertions.assertAll();

    }

}