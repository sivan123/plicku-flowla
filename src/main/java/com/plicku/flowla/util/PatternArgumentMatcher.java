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

    public List<Argument> argumentsFrom(String stepname) {
        Matcher matcher = pattern.matcher(stepname);
        if (matcher.lookingAt()) {
            List<Argument> arguments = new ArrayList<>(matcher.groupCount());
            for (int i = 1; i <= matcher.groupCount(); i++) {
                int startIndex = matcher.start(i);
                arguments.add(new Argument(startIndex == -1 ? null : startIndex, matcher.group(i)));
            }
            return arguments;
        } else {
            return null;
        }
    }
}
