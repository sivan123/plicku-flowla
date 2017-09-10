package com.plicku.stepin.util;

public class Argument {
    private static final long serialVersionUID = 1L;

    private final Integer offset;
    private final Object val;

    public Argument(Integer offset, String val) {
        this.offset = offset;
        this.val = val;
    }

    public Object getVal() {
        return val;
    }

    public Integer getOffset() {
        return offset;
    }

    public String toString() {
        return String.valueOf(getVal());
    }
}
