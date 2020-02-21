package com.blitz.compiler.utils;

public enum Constants {
    EPSILON("epsilon"),
    START("S'"),
    EOI("$");

    private String value;
    Constants(String val) {
        this.value = val;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
