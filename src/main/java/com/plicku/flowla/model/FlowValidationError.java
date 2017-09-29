package com.plicku.flowla.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlowValidationError {

    String stepName;
    int lineNo;
    String message;

    public FlowValidationError(String stepName, String message) {
        this.stepName = stepName;
        this.message = message;
    }

    public FlowValidationError(String message) {
        this.message = message;
    }
}
