package com.plicku.stepin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.plicku.stepin.model.DataTable;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class ParamDataUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static Object getBean(List<String> data, Class parameterType) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        DataTable dataTable = new DataTable(data);
        return dataTable.getBean(parameterType);
    }

    public static Object getBeanFromJson(List<String> paramData, Class parameterType) throws IOException {
        String json =paramData.stream().collect(Collectors.joining());
        return jsonMapper.readValue(json,parameterType);
    }

    public static Object getBeanFromYaml(List<String> paramData, Class parameterType) throws IOException {
        String json =paramData.stream().collect(Collectors.joining());
        return yamlMapper.readValue(json,parameterType);
    }
}
