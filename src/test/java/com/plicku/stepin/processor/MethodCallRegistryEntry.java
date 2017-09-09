package com.plicku.stepin.processor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MethodCallRegistryEntry {
    String name;
    Boolean called=false;

}
