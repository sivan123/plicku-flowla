package com.plicku.flowla.util;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class Report {

    List<StepResult> stepResults = new ArrayList<>();

}
class StepResult{
    String keyword;
    String stepName;
    String status;
    String errors;
    Map output;
}
