package com.plicku.flowla.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.List;

@Data
public class MethodParameter {

    Class parameterType;
    boolean annotated=false;
    List<Annotation> argAnnotation;

}
