package com.blitz.compiler.parsers.lr0;

import com.blitz.compiler.utils.Production;

import java.util.Arrays;
import java.util.Objects;

/**
 * LR(0)特点是规约是不使用向前看
 */
public class LR0Item extends Production {
    protected int dotPointer;

    public LR0Item(Production p){
        super(p.getLeftSide(),p.getRightSide());
        this.dotPointer = 0;
    }

    public LR0Item(String leftSide,String[] rightSide,int dotPointer) {
        super(leftSide,rightSide);
        this.dotPointer = dotPointer;
    }

    LR0Item(LR0Item item){
        super(item);
        dotPointer = item.getDotPointer();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LR0Item lr0Item = (LR0Item) o;
        return dotPointer == lr0Item.dotPointer;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.dotPointer;
        hash = 89 * hash + Objects.hashCode(this.leftSide);
        hash = 89 * hash + Arrays.deepHashCode(this.rightSide);
        return hash;
    }

    @Override
    public String toString() {
        var buffer = new StringBuilder(leftSide + "->");
        for (int i = 0; i < rightSide.length; i++) {
            if(i == dotPointer) {
                buffer.append(".");
            }
            buffer.append(rightSide[i]);
            if( i != rightSide.length -1){
                buffer.append(" ");
            }
        }

        if(rightSide.length == dotPointer) {
            buffer.append(".");
        }

        return buffer.toString();
    }

    public int getDotPointer() {
        return dotPointer;
    }

    boolean goTo() {
        if(dotPointer >= rightSide.length) {
            return  false;
        }
        dotPointer++;
        return true;
    }

    String getCurrentTerminal(){
        if(dotPointer == rightSide.length) {
            return null;
        }
        return rightSide[dotPointer];
    }
}
