package com.plicku.flowla.model.contexts;

import com.plicku.flowla.exceptions.ProcessingException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VariableMap {
    private Map<String,Object> variableMap= new ConcurrentHashMap<>();

    public Object getVariableVal(String variable) throws ProcessingException {
        if(!this.variableMap.containsKey(variable)){
            throw new ProcessingException(variable+" not found. Make sure the variable is set in the flow");
        }
        else
            return this.variableMap.get(variable);
    }
    public void removeVariable(String variable)
    {
        if(variable!=null)
        variableMap.remove(variable);
    }
    public boolean containsVariable(String variable){
        return variableMap.containsKey(variable);
    }
    public void setVariable(String variable,Object val)
    {
        this.variableMap.put(variable,val);
    }

}
