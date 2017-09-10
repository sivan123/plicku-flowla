package com.plicku.stepin.model.contexts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalContext {
    private Map<String,Object> globalContextProperties = new ConcurrentHashMap();

    public Object getProperty(String key)
    {
        return this.globalContextProperties.get(key);
    }

    public void setProperty(String key, Object value)
    {
        this.globalContextProperties.put(key, value);
    }
}
