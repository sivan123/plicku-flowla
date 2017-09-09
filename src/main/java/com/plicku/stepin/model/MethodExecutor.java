package com.plicku.stepin.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.plicku.stepin.processor.StepinProcessor;
import lombok.Data;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.plicku.stepin.processor.StepinProcessor.objectMapper;

@Data
public class MethodExecutor {

    StepinEnums.InstructionType instructionType;   //Givem
    StepinEnums.StepProcessType stepProcessType;
    boolean methodToBeExecuted=false;
    Class declaringClass;
    Method method;
    List<Class> paramTypes;
    List dataTableHeaders;

    boolean dataTableHeadersRegistered=false;
    private List<String> dataTableDataRow;
    private List<List<String>> dataTableDataRows= new ArrayList<>();

    boolean jsonStringRegistered=false;
    StringBuffer json = new StringBuffer();
    private Type[] genericParameterTypes;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
        this.declaringClass=method.getDeclaringClass();
        this.paramTypes = Arrays.asList(method.getParameterTypes());
        this.methodToBeExecuted=true;
        this.genericParameterTypes = method.getGenericParameterTypes();
    }


    public void setDataTableHeaders(String[] headers) {
    this.dataTableHeaders=Arrays.asList(headers).stream().map(String::trim).collect(Collectors.toList());
    this.dataTableHeadersRegistered=true;
    }

    public void setDataTableData(String[] datarow) {
        this.dataTableDataRow = Arrays.asList(datarow);
        this.dataTableDataRows.add(dataTableDataRow);
    }

    public void executeMethod() throws InvocationTargetException, IllegalAccessException, InstantiationException, IOException, ClassNotFoundException {
        method.setAccessible(true);
        if(paramTypes.size()==0)
                method.invoke(StepinProcessor.classMap.get(declaringClass));
        else //create bean an inject
        {
            List<Object> params = new ArrayList<>();
            for (Class aClass:paramTypes)
            {
                Object bean = null;
                if(dataTableHeadersRegistered){
                    bean=aClass.newInstance();
                    for (int i = 0; i < getDataTableHeaders().size() ; i++) {
                        BeanUtils.setProperty(bean,(String)getDataTableHeaders().get(i),getDataTableDataRow().get(i));
                    }
                }
                else if(jsonStringRegistered)
                {
//                    if(genericParameterTypes!=null && genericParameterTypes.length>0 && Collection.class.isAssignableFrom(aClass)){
//                        Type paramType=genericParameterTypes[0];
//                        Class typeClass=paramType.getClass();
////                        objectMapper.readValue(json.toString(), new TypeReference<List<Simpl>>() {});
//                        Class<?> clz = Class.forName(genericParameterTypes[0].getTypeName());
//                        JavaType type = objectMapper.getTypeFactory().constructCollectionType(aClass, clz);
//                        bean=objectMapper.readValue(json.toString(),type);
//                    }
//                    else
                        bean=objectMapper.readValue(json.toString(),aClass);
                }
                params.add(bean);
            }
            method.invoke(StepinProcessor.classMap.get(declaringClass),params.toArray());
        }
        this.setMethodToBeExecuted(false);
    }
}
