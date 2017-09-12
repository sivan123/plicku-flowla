package com.plicku.stepin.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.plicku.stepin.exceptions.DataParsingException;
import com.plicku.stepin.model.DataTable;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

public class ParamDataUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static Object getBean(List<String> data, Class parameterType) throws DataParsingException {
        DataTable dataTable = new DataTable(data);
        try {
            return dataTable.getBean(parameterType);
        } catch (Exception e) {
            throw new DataParsingException("Unable to process the data table correctly. Please check the validity of how the data is provided",e);
        }
    }

    public static Object getBeanFromJson(List<String> paramData, Class parameterType) throws DataParsingException {
        String json =paramData.stream().collect(Collectors.joining());
        try {
            return jsonMapper.readValue(json,parameterType);
        } catch (IOException e) {
            throw new DataParsingException("Unable to process json data correctly. Please check the validity of the json data",e);
        }
    }

    public static Object getBeanFromYaml(List<String> paramData, Class parameterType) throws DataParsingException {
        String json =paramData.stream().collect(Collectors.joining(System.lineSeparator()));
        try {
            return yamlMapper.readValue(json,parameterType);
        } catch (IOException e) {
            throw new DataParsingException("Unable to process yaml data correctly. Please check the validity of the yaml data",e);
        }
    }
}
