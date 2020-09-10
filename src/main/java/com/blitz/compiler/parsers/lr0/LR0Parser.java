package com.blitz.compiler.parsers.lr0;

import com.blitz.compiler.utils.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LR0Parser extends LRParser {

    private ArrayList<LR0State> canonicalCollection;

    public LR0Parser(final Grammar grammar) {
        super(grammar);
    }

    public boolean parserSLR1() {
        createStates();
        createGoToTable();
        return createActionTableForSLR1();
    }

    public boolean parserLR0() {
        createStates();
        createGoToTable();
        return createActionTableForLR0();
    }

    protected void createStates() {
        canonicalCollection = new ArrayList<>();
        final HashSet<LR0Item> start = new HashSet<>();
        start.add(new LR0Item(grammar.getRules().get(0)));

        final LR0State startState = new LR0State(grammar, start);
        canonicalCollection.add(startState);

        for (int i = 0; i < canonicalCollection.size(); i++) {
            final HashSet<String> stringWithDot = new HashSet<>();
            for (final LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getCurrentTerminal() != null) {
                    stringWithDot.add(item.getCurrentTerminal());
                }
            }
            for (final String str : stringWithDot) {
                final HashSet<LR0Item> nextStateItems = new HashSet<>();
                for (final LR0Item item : canonicalCollection.get(i).getItems()) {

                    if (item.getCurrentTerminal() != null && item.getCurrentTerminal().equals(str)) {
                        final LR0Item temp = new LR0Item(item);
                        temp.goTo();
                        nextStateItems.add(temp);
                    }
                }
                final LR0State nextState = new LR0State(grammar, nextStateItems);
                boolean isExist = false;
                for (int j = 0; j < canonicalCollection.size(); j++) {
                    if (canonicalCollection.get(j).getItems().containsAll(nextState.getItems())
                            && nextState.getItems().containsAll(canonicalCollection.get(j).getItems())) {
                        isExist = true;
                        canonicalCollection.get(i).addTransition(str, canonicalCollection.get(j));
                    }
                }
                if (!isExist) {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(i).addTransition(str, nextState);
                }
            }
        }

    }

    protected void createGoToTable() {
        goToTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            goToTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (final String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getVariables().contains(s)) {
                    goToTable[i].put(s, findStateIndex(canonicalCollection.get(i).getTransition().get(s)));
                }
            }
        }
    }

    private boolean createActionTableForSLR1() {
        actionTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            actionTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (final String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(s)) {
                    actionTable[i].put(s, new Action(ActionType.SHIFT,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (final LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if (item.getLeftSide().equals("S'")) {
                        actionTable[i].put("$", new Action(ActionType.ACCEPT, 0));
                    } else {
                        final HashSet<String> follow = grammar.getFallowSets().get(item.getLeftSide());
                        final Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        final int index = grammar.findRuleIndex(rule);
                        final Action action = new Action(ActionType.REDUCE, index);
                        for (final String str : follow) {
                            if (actionTable[i].get(str) != null) {
                                System.out.println("it has a REDUCE-" + actionTable[i].get(str).getType()
                                        + " confilct in state " + i);
                                return false;
                            } else {
                                actionTable[i].put(str, action);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean createActionTableForLR0() {
        actionTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            actionTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (final String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(s)) {
                    actionTable[i].put(s, new Action(ActionType.SHIFT,
                            findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (final LR0Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if (item.getLeftSide().equals("s'")) {
                        actionTable[i].put("$", new Action(ActionType.ACCEPT, 0));
                    } else {
                        final HashSet<String> terminals = grammar.getTerminals();
                        terminals.add("$");
                        final Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        final int index = grammar.findRuleIndex(rule);
                        final Action action = new Action(ActionType.REDUCE, index);
                        for (final String str : terminals) {
                            if (actionTable[i].get(str) != null) {
                                System.out.println("it has a REDUCE-" + actionTable[i].get(str).getType()
                                        + " confilct in state " + i);
                                return false;
                            } else {
                                actionTable[i].put(str, action);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private int findStateIndex(final LR0State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if (canonicalCollection.get(i).equals(state)) {
                return i;
            }
        }
        return -1;
    }

    public String canonicalCollectionStr() {
        String str = "Canonical Collection : \n";
        for (int i = 0; i < canonicalCollection.size(); i++) {
            str += "State " + i + " : \n";
            str += canonicalCollection.get(i) + "\n";
        }
        return str;
    }

}
