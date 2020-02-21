package com.blitz.compiler.parsers.lr1;

import com.blitz.compiler.utils.Production;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class LR1Item  {
    private HashSet<String> lookAhead;
    private String leftSide;
    private String[] righSide;
    private int dotPointer;


    public LR1Item(String leftSide,String[] righSide,int dotPointer,HashSet<String > lookAhead){
        this.leftSide = leftSide;
        this.righSide = righSide;
        this.dotPointer = dotPointer;
        this.lookAhead = lookAhead;
    }

    public String getCurrent() {
        if(dotPointer == righSide.length) {
            return null;
        }
        return righSide[dotPointer];
    }

    boolean goTo() {
        if(dotPointer >= righSide.length) {
            return false;
        }
        dotPointer++;
        return true;
    }

    public int getDotPointer() {
        return dotPointer;
    }

    public String[] getRighSide() {
        return righSide;
    }

    public HashSet<String> getLookAhead() {
        return lookAhead;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(String leftSide) {
        this.leftSide = leftSide;
    }

    public void setRighSide(String[] righSide) {
        this.righSide = righSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LR1Item lr1Item = (LR1Item) o;
        return dotPointer == lr1Item.dotPointer &&
                Objects.equals(lookAhead, lr1Item.lookAhead) &&
                Objects.equals(leftSide, lr1Item.leftSide) &&
                Arrays.equals(righSide, lr1Item.righSide);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.dotPointer;
        hash = 31 * hash + Objects.hashCode(this.leftSide);
        hash = 31 * hash + Arrays.deepHashCode(this.righSide);
        hash = 31 * hash + Objects.hashCode(this.lookAhead);

        return hash;
    }

    public  boolean equalLR0(LR1Item item){
        return leftSide.equals(item.getLeftSide()) && Arrays.equals(righSide,item.getRighSide()) && dotPointer == item.getDotPointer();
    }

    @Override
    public String toString() {
        String str = leftSide + " -> ";
        for (int i = 0; i < righSide.length ; i++) {
            if(i == dotPointer) {
                str += ".";
            }
            str += righSide[i];
            if(i != righSide.length - 1) {
                str += " ";
            }
        }
        if(righSide.length == dotPointer) {
            str += ".";
        }
        str += " , " + lookAhead;
        return str;
    }
}
