package com.plicku.flowla.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.plicku.flowla.exceptions.DataParsingException;
import com.plicku.flowla.model.DataTable;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParamDataUtil {
    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public static Object getBean(String data, Class parameterType) throws DataParsingException {
        DataTable dataTable = new DataTable(Arrays.asList(data.split("\\r?\\n")));
        try {
            return dataTable.getBean(parameterType);
        } catch (Exception e) {
            throw new DataParsingException("Unable to process the data table correctly. Please check the validity of how the data is provided",e);
        }
    }

    public static Object getBeanFromJson(String paramData, Class parameterType) throws DataParsingException {
     try {
            return jsonMapper.readValue(paramData,parameterType);
        } catch (IOException e) {
            throw new DataParsingException("Unable to process json data correctly. Please check the validity of the json data",e);
        }
    }

    public static Object getBeanFromYaml(String paramData, Class parameterType) throws DataParsingException {
        try {
            return yamlMapper.readValue(paramData,parameterType);
        } catch (IOException e) {
            throw new DataParsingException("Unable to process yaml data correctly. Please check the validity of the yaml data",e);
        }
    }
}
