package com.plicku.flowla.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternArgumentMatcher {
    private final Pattern pattern;

    public PatternArgumentMatcher(Pattern pattern) {
        this.pattern = pattern;
    }

    public List<String> argumentsFrom(String stepname) {
        Matcher matcher = pattern.matcher(stepname);
        if (matcher.lookingAt()) {
            List<String> arguments = new ArrayList<>(matcher.groupCount());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                arguments.add( matcher.group(i));
            }
            return arguments;
        } else {
            return null;
        }
    }
}
