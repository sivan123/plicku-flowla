package com.plicku.flowla.model;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class DataTable {
    public List<List<Object>> datatable = new ArrayList<>();

    public DataTable(List<String> datalines) {
        datalines.forEach((String line) -> {
            line=line.trim();
           if(!"".equals(line.trim())) datatable.add(Arrays.asList(StringUtils.split(line,"\\|")));
        });
    }

    public List<String> getHeaders()
    {
        return datatable.get(0).stream().map(o -> o.toString()).collect(Collectors.toList());
    }

    public Object elementAt(int row,int col)
    {
        return datatable.get(row).get(col);
    }

    public List<Object> getRow(int row)
    {
        return datatable.get(row);
    }

    public List<Object> getColumn(int col)
    {
        return datatable.stream().map(objects -> {return objects.get(col);}).collect(Collectors.toList());
    }

    public <T> T getBean(Class<T> beanType) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        if(Collection.class.isAssignableFrom(beanType))
        {
            Collection collections = new ArrayList<>() ;
            for(List row:datatable)
            {
                T bean = beanType.newInstance();
                for(Object list :datatable.get(0))
                {
                    int i =0;
                    BeanUtils.setProperty(bean,list.toString(),elementAt(1,i));
                    i++;
                }
                collections.add(bean);
            }
            return (T) collections;

        }
        else if(Map.class.isAssignableFrom(beanType))
        {
            Map map = new HashMap();
            for(Object list :datatable.get(0))
            {
                int i =0;
                map.put(list.toString(),elementAt(1,i));
                i++;
            }
            return (T) map;
        }
        else
        {
            T bean = beanType.newInstance();
            int i =0;
            for(Object list :datatable.get(0))
            {
                BeanUtils.setProperty(bean,list.toString(),elementAt(1,i));
                i++;
            }
            return bean;
        }

    }

}
