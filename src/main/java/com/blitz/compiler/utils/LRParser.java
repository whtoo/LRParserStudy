package com.blitz.compiler.utils;

import java.util.*;

public abstract class LRParser {
    protected HashMap<String,Integer>[] goToTable;
    protected HashMap<String,Action>[] actionTable;
    protected Grammar grammar;

    public LRParser(Grammar grammar){
        this.grammar = grammar;
    }

    protected abstract void createGoToTable();

    public boolean accept(ArrayList<String> inputs) {
        inputs.add(Constants.EOI.getValue());
        int index = 0;
        Stack<String> stack = new Stack<>();
        stack.add("0");
        while (index < inputs.size()) {
            int state = Integer.valueOf(stack.peek());
            String nextInput = inputs.get(index);
            Action action = actionTable[state].get(nextInput);

            if(action == null) {
                return false;
            } else if(action.getType() == ActionType.SHIFT) {
                stack.push(nextInput);
                stack.push(action.getOperand() + "");
            } else if(action.getType() == ActionType.REDUCE) {
                int ruleIndex = action.getOperand();
                Production rule = grammar.getRules().get(ruleIndex);
                String leftSide = rule.getLeftSide();
                int rightSideLength = rule.getRightSide().length;
                for (int i = 0; i < 2 * rightSideLength; i++) {
                    stack.pop();
                }

                int nextState = Integer.valueOf(stack.peek());
                stack.push(leftSide);
                int variableState = goToTable[nextState].get(leftSide);
                stack.push(variableState + "");
            } else if(action.getType() == ActionType.ACCEPT) {
                return true;
            }
        }
        return false;
    }

    public String gotToTableStr() {
        String str = "Go To Table : \n";
        str += "          ";
        for(var variable : grammar.getVariables()) {
            str += String.format("%-6s",variable);
        }
        str += "\n";

        for(int i = 0; i < goToTable.length; i++) {
            for (int j = 0; j < (grammar.getVariables().size() + 1) * 6 + 2; j++) {
                str += "-";
            }
            str += "\n";
            str += String.format("|%-6s|",i);
            for(var variable : grammar.getVariables()) {
                str += String.format("%6s",(goToTable[i].get(variable) == null ? "|" : goToTable[i].get(variable)+"|"));
            }
            str+="\n";
        }
        for (int i = 0; i < (grammar.getVariables().size() + 1) * 6 + 2; i++) {
            str += "-";
        }

        return str;
    }

    public String actionTableStr() {
        String str = "Action Table : \n";
        HashSet<String> terminals = new HashSet<>(grammar.getTerminals());
        terminals.add(Constants.EOI.getValue());
        str += "          ";
        for(var terminal : terminals) {
            str += String.format("%-10s",terminal);
        }
        str += "\n";
        for (int i = 0; i < actionTable.length; i++) {
            for (int j = 0; j < (terminals.size()+1) * 10 + 2; j++) {
                str += "-";
            }
            str += "\n";
            str += String.format("|%-10s",i);
            for(var terminal : terminals) {
                str += String.format("%10s",(actionTable[i].get(terminal) == null ? "|" : actionTable[i].get(terminal)));
            }
            str += "\n";
        }
        for (int i = 0; i < (terminals.size()+1) * 10 + 2; i++) {
            str += "-";
        }
        return str;
    }

    public Grammar getGrammar() {
        return grammar;
    }
}
