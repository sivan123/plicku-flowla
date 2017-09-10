package com.plicku.stepin.processor;

import com.plicku.stepin.anotations.DataTable;
import com.plicku.stepin.anotations.JSONParameter;
import com.plicku.stepin.anotations.YAMLParameter;
import com.plicku.stepin.model.MethodParameter;
import com.plicku.stepin.model.StepMethodProperty;
import com.plicku.stepin.model.StepinEnums;
import com.plicku.stepin.util.ParamDataUtil;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.plicku.stepin.processor.StepinProcessor.objectMapper;

public class StepExecutor {

    boolean methodToBeExecuted=false;
    private StepMethodProperty stepMethodProperty;
    List dataTableHeaders;
    StepinEnums.InstructionType instructionType;
    boolean dataTableHeadersRegistered=false;
    private List<String> dataTableDataRow;
    private List<List<String>> dataTableDataRows= new ArrayList<>();
    private List<String> paramData = new ArrayList<>();

    boolean jsonStringRegistered=false;
    StringBuffer json = new StringBuffer();


    public void setStepMethodProperty(Method method) {
        this.stepMethodProperty = new StepMethodProperty(method);
        this.methodToBeExecuted=true;
    }

    public void addParamDataLine(String line)
    {
        this.paramData.add(line);
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
        Method method = stepMethodProperty.getMatchedMethod();
        method.setAccessible(true);
        if(stepMethodProperty.getMethodParameters().size()==0)
                method.invoke(StepinProcessor.classMap.get(stepMethodProperty.getDeclaringClass()));
        else //create bean an inject
        {
            List<Object> params = new ArrayList<>();

            for (MethodParameter methodParameter :stepMethodProperty.getMethodParameters())
            {
                Object bean = null;
                //check if datatable
                if(methodParameter.getArgAnnotation().contains(DataTable.class)||"\\|".startsWith(this.paramData.toString()))
                {
                    params.add(ParamDataUtil.getBean(this.paramData,methodParameter.getParameterType()));
                }
                else if(methodParameter.getArgAnnotation().contains(JSONParameter.class)||"{".startsWith(this.paramData.toString())||"[".startsWith(this.paramData.toString()))
                {
                    params.add(ParamDataUtil.getBeanFromJson(this.paramData,methodParameter.getParameterType()));
                }
                else if(methodParameter.getArgAnnotation().contains(YAMLParameter.class))
                {
                    params.add(ParamDataUtil.getBeanFromYaml(this.paramData,methodParameter.getParameterType()));
                }
                else
                {
                    params.add(methodParameter.getParameterType().newInstance())
                }
                if(dataTableHeadersRegistered){
                    bean=methodParameter.getParameterType().newInstance();
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

    public void setInstructionType(StepinEnums.InstructionType instructionType) {
        this.instructionType = instructionType;
    }

    public boolean isMethodToBeExecuted() {
        return methodToBeExecuted;
    }

    public void setMethodToBeExecuted(boolean methodToBeExecuted) {
        this.methodToBeExecuted = methodToBeExecuted;
    }
}
