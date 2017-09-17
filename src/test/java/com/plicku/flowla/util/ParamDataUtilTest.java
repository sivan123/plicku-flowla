package com.plicku.flowla.util;

import com.plicku.flowla.processor.beans.SimpleTestBean;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ParamDataUtilTest {
    @Test
    public void getBean() throws Exception {

        SoftAssertions softAssertions = new SoftAssertions();
        SimpleTestBean simpleTestBean = new SimpleTestBean("TestName","TestAddress1",30878);
        String data1 = "|name|address1|zip|\n" +
                "|TestName|TestAddress1|30878|";
        SimpleTestBean simpleTestBean1 = (SimpleTestBean) ParamDataUtil.getBean(data1, SimpleTestBean.class);
        softAssertions.assertThat(simpleTestBean1).isEqualTo(simpleTestBean);

        String data2 = "\t\n" +
                "\t|name|address1|zip|\n" +
                "|TestName|TestAddress1|30878|\n" +
                "\t\n" +
                "\t";
        SimpleTestBean simpleTestBean2 = (SimpleTestBean) ParamDataUtil.getBean(data2, SimpleTestBean.class);
        softAssertions.assertAll();

    }

    @Test
    public void getBeanFromJson() throws Exception {
        String json = "     {\"name\":\"TestName\"\n" +
                ",\"address1\":\"TestAddress1\",\"zip\":30878}";
        SimpleTestBean simpleTestBean = (SimpleTestBean) ParamDataUtil.getBeanFromJson(json, SimpleTestBean.class);
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

    @Test
    public void getBeanFromYaml() throws Exception {
        String yaml = "name: TestName\n" +
                "address1: TestAddress1\n" +
                "zip: 30878";
        SimpleTestBean simpleTestBean = (SimpleTestBean) ParamDataUtil.getBeanFromYaml(yaml, SimpleTestBean.class);
        SimpleTestBean simpleTestBean1 = new SimpleTestBean("TestName","TestAddress1",30878);
        Assert.assertEquals(simpleTestBean,simpleTestBean1);
    }

}