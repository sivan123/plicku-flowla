package com.plicku.flowla.processor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodCallRegistryEntry {
    String name;
    Boolean expectedToBeCalled=false;
    Boolean actuallycalled =false;

    public MethodCallRegistryEntry(String name, Boolean actuallycalled) {
        this.name = name;
        this.actuallycalled = actuallycalled;
    }
}
