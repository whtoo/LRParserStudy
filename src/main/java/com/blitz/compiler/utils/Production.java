package com.blitz.compiler.utils;

import java.util.Arrays;
import java.util.Objects;

public class Production {
    protected String leftSide;
    protected String[] rightSide;

    public Production(String leftSide,String[] rightSide){
        this.rightSide = rightSide;
        this.leftSide = leftSide;
    }

    public Production(Production production) {
        this.leftSide = production.leftSide;
        this.rightSide = production.rightSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public String[] getRightSide() {
        return rightSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return leftSide.equals(that.leftSide) &&
                Arrays.equals(rightSide, that.rightSide);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        int result = 31 * hash + Objects.hashCode(leftSide);
        result = 31 * result + Arrays.deepHashCode(rightSide);
        return result;
    }

    @Override
    public String toString() {
        String str = leftSide + " -> ";
        for (var rule: this.rightSide) {
            str += rule + " ";
        }
        return str;
    }
}
