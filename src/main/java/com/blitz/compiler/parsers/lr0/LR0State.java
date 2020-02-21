package com.blitz.compiler.parsers.lr0;

import com.blitz.compiler.utils.Grammar;
import com.blitz.compiler.utils.Production;

import java.util.*;

public class LR0State {
    LinkedHashSet<LR0Item> items;
    HashMap<String,LR0State> transitions;

    public HashMap<String, LR0State> getTransitions() {
        return transitions;
    }

    public LinkedHashSet<LR0Item> getItems() {
        return items;
    }

    public LR0State(Grammar grammar, HashSet<LR0Item> coreItems) {
        items = new LinkedHashSet<>();
        transitions = new HashMap<>();

    }

    private void closure(Grammar grammar) {
        boolean changedFlag = false;
        do {
            changedFlag = false;
            HashSet<LR0Item> temp = new HashSet<>();
            for (var item: items) {
                if(item.getCurrentTerminal() != null && grammar.isVariable(item.getCurrentTerminal())){
                    var rules = grammar.getRuleByLeftVariable(item.getCurrentTerminal());
                    temp.addAll(createLR0Item(rules));
                }
            }
            if(!items.containsAll(temp)) {
                items.addAll(temp);
                changedFlag = true;
            }
        } while (changedFlag);
    }

    private HashSet<LR0Item> createLR0Item(HashSet<Production> rules) {
        HashSet<LR0Item> results = new HashSet<>();
        for (var rule: rules
             ) {
            results.add(new LR0Item(rule));
        }
        return results;
    }

    public void addTransition(String s,LR0State state) {
        transitions.put(s,state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LR0State other = (LR0State) o;
        if(!(this.items.containsAll(other.items) && other.items.containsAll(this.items))){
            return false;
        }
        if(!Objects.equals(this.transitions,other.transitions)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.items);
        hash = 83 * hash + Objects.hashCode(this.transitions);
        return hash;
    }

    @Override
    public String toString() {
        String s = "";
        for(var item : items) {
            s += items + "\n";
        }
        return s;
    }
}
