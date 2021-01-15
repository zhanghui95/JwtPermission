package com.github.jwtp.perm;

import com.github.jwtp.annotation.Logical;

/**
 * url自动对应权限接口的返回结果封装
 */
public class UrlPermResult {
    private String[] values;
    private Logical logical;

    public UrlPermResult() {
    }

    public UrlPermResult(String[] values, Logical logical) {
        this.values = values;
        this.logical = logical;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public Logical getLogical() {
        return logical;
    }

    public void setLogical(Logical logical) {
        this.logical = logical;
    }

}
