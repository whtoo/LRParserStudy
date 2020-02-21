package com.blitz.compiler.utils;

public class Action {
    private ActionType type;
    private Integer operand;

    public Action(ActionType type,Integer operand) {
        this.type = type;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return type +  " " + (type == ActionType.ACCEPT ? "":operand);
    }

    public Integer getOperand() {
        return operand;
    }

    public ActionType getType() {
        return type;
    }
}
