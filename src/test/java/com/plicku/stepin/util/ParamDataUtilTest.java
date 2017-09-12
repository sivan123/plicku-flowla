package com.plicku.stepin.util;

import com.plicku.stepin.processor.beans.SimpleTestBean;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ParamDataUtilTest {
    @Test
    public void getBean() throws Exception {

        List<String>lines = Arrays.asList("|name|address1|zip|","|TestName|TestAddress1|30878|");
        SimpleTestBean simpleTestBean = (SimpleTestBean) ParamDataUtil.getBean(lines, SimpleTestBean.class);
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

    @Test
    public void getBeanFromJson() throws Exception {
        List<String>lines = Arrays.asList("{\"name\":\"TestName\"," , "\"address1\":\"TestAddress1\",\"zip\":30878}");
        SimpleTestBean simpleTestBean = (SimpleTestBean) ParamDataUtil.getBeanFromJson(lines, SimpleTestBean.class);
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

    @Test
    public void getBeanFromYaml() throws Exception {
        List<String>lines = Arrays.asList("name: TestName","address1: TestAddress1","zip: 30878");
        SimpleTestBean simpleTestBean = (SimpleTestBean) ParamDataUtil.getBeanFromYaml(lines, SimpleTestBean.class);
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

}