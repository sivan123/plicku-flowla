package com.plicku.flowla.model.contexts;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SequenceContext {
  private Map<String,Object> contextPropeties = new ConcurrentHashMap();

  public Object getProperty(String key)
  {
    return this.contextPropeties.get(key);
  }

  public void setProperty(String key, Object value)
  {
    this.contextPropeties.put(key, value);
  }


}
