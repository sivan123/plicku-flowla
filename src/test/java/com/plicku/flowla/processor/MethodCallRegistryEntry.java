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
    Boolean called =false;

    public MethodCallRegistryEntry(String name, Boolean called) {
        this.name = name;
        this.called = called;
    }
}
