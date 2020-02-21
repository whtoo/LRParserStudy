package com.blitz.compiler.parsers.lr1;

import com.blitz.compiler.utils.Constants;
import com.blitz.compiler.utils.Grammar;
import com.blitz.compiler.utils.Production;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LR1State {
    private LinkedList<LR1Item> items;
    private HashMap<String,LR1State> transition;

    public LR1State(Grammar grammar, HashSet<LR1Item> coreItems) {
        items = new LinkedList<>(coreItems);
        transition = new HashMap<>();
        closure(grammar);
    }

    private void closure(Grammar grammar) {
        boolean changeFlag = false;
        do {
            changeFlag = false;
            HashSet<LR1Item> temp = new HashSet<>();
            for(var item : items) {
                if(item.getDotPointer() != item.getRighSide().length && grammar.isVariable(item.getCurrent())) {
                    HashSet<String> lookahead = new HashSet<>();
                    if(item.getDotPointer() == item.getRighSide().length - 1) {
                        lookahead.addAll(item.getLookAhead());
                    } else {
                        HashSet<String> firstSet = grammar.computeFirst(item.getRighSide(),item.getDotPointer()+1);
                        if(firstSet.contains(Constants.EPSILON.getValue())){
                            firstSet.remove(Constants.EPSILON.getValue());
                            firstSet.addAll(item.getLookAhead());
                        }
                        lookahead.addAll(firstSet);
                    }
                    HashSet<Production> rules = grammar.getRuleByLeftVariable(item.getCurrent());
                    for(var rule : rules) {
                        temp.add(new LR1Item(rule.getLeftSide(),rule.getRightSide(),0,lookahead));
                    }
                }
                if(!items.containsAll(temp)) {
                    items.addAll(temp);
                    changeFlag = true;
                }
            }
        } while (changeFlag);
    }

    public HashMap<String, LR1State> getTransition() {
        return transition;
    }

    public LinkedList<LR1Item> getItems() {
        return items;
    }

    @Override
    public String toString() {
        String s = "";
        for(var item : items) {
            s += item + "\n";
        }
        return s;
    }
}
