package com.plicku.flowla.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamTestBean {

    String name="gifthamper";
    List<String> basketItems= Arrays.asList("pen","pencil");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBasketItems() {
        return basketItems;
    }

    public void setBasketItems(List<String> basketItems) {
        this.basketItems = basketItems;
    }
}
