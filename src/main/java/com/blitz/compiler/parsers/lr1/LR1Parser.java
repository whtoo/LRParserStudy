package com.blitz.compiler.parsers.lr1;

import com.blitz.compiler.parsers.lr0.LR0Item;
import com.blitz.compiler.utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class LR1Parser extends LRParser {
    private ArrayList<LR1State> canonicalCollection;

    public LR1Parser(Grammar grammar) {
        super(grammar);
    }

    protected void createStatesForCLR1() {
        canonicalCollection = new ArrayList<>();
        HashSet<LR1Item> start =new HashSet<>();
        Production startRule = grammar.getRules().get(0);
        HashSet<String> startLookahead = new HashSet<>();
        startLookahead.add(Constants.EOI.getValue());
        start.add(new LR1Item(startRule.getLeftSide(),startRule.getRightSide(),0,startLookahead));

        LR1State startState = new LR1State(grammar,start);
        canonicalCollection.add(startState);

        for (int i = 0; i < canonicalCollection.size(); i++) {
            HashSet<String> stringWithDot = new HashSet<>();
            for(var item : canonicalCollection.get(i).getItems()){
                if(item.getCurrent() != null) {
                    stringWithDot.add(item.getCurrent());
                }
            }
            for (var str : stringWithDot
                 ) {
                HashSet<LR1Item> nextStateItems = new HashSet<>();
                for (var item: canonicalCollection.get(i).getItems()
                     ) {
                    if(item.getCurrent() != null && item.getCurrent().equals(str)) {
                        LR1Item temp = new LR1Item(item.getLeftSide(),item.getRighSide(),item.getDotPointer()+1,item.getLookAhead());
                        nextStateItems.add(temp);
                    }
                    LR1State nextState = new LR1State(grammar,nextStateItems);
                    boolean isExist = false;
                    for (int j = 0; j < canonicalCollection.size(); j++) {
                        if(canonicalCollection.get(j).getItems().containsAll(nextState.getItems()) &&
                         nextState.getItems().containsAll(canonicalCollection.get(j).getItems())) {
                            isExist = true;
                            canonicalCollection.get(i).getTransition().put(str,canonicalCollection.get(j));
                        }
                    }
                    if(!isExist) {
                        canonicalCollection.add(nextState);
                        canonicalCollection.get(i).getTransition().put(str,nextState);
                    }
                }
            }
        }
    }
    public boolean parseCLR1() {
        createStatesForCLR1();
        createGoToTable();
        return createActionTable();
    }

    public boolean parserLALR1() {
        createStatesForLALR1();
        createGoToTable();
        return createActionTable();
    }

    public void createStatesForLALR1() {
        createStatesForCLR1();
        ArrayList<LR1State> temp = new ArrayList<>();
        for (int i = 0; i < canonicalCollection.size(); i++) {
            var lookahead = new HashSet<>();
            var itemsi = new HashSet<>();
            for(var item : canonicalCollection.get(i).getItems()) {
                itemsi.add(new LR0Item(item.getLeftSide(),item.getRighSide(),item.getDotPointer()));
            }
            for (int j = i+1; j < canonicalCollection.size(); j++) {
                var itemsj = new HashSet<>();
                for(var item : canonicalCollection.get(j).getItems()) {
                    itemsj.add(new LR0Item(item.getLeftSide(),item.getRighSide(),item.getDotPointer()));
                }
                if(itemsi.containsAll(itemsj) && itemsj.containsAll(itemsi)) {
                    for(var itemi : canonicalCollection.get(i).getItems()) {
                        for(var itemj : canonicalCollection.get(j).getItems()) {
                            if(itemi.equalLR0(itemj)) {
                                itemi.getLookAhead().addAll(itemj.getLookAhead());
                                break;
                            }
                        }
                    }
                    for (int k = 0; k < canonicalCollection.size(); k++) {
                        for (var s : canonicalCollection.get(k).getTransition().keySet()
                             ) {
                            if(canonicalCollection.get(k).getTransition().get(s).getItems().containsAll(canonicalCollection.get(j).getItems()) &&
                                    canonicalCollection.get(j).getItems().containsAll(canonicalCollection.get(k).getTransition().get(s).getItems())) {
                                canonicalCollection.get(k).getTransition().put(s,canonicalCollection.get(i));
                            }
                        }
                    }
                    canonicalCollection.remove(j);
                    j--;
                }
            }
        temp.add(canonicalCollection.get(i));
        }
        canonicalCollection = temp;
    }

    private int findStateIndex(LR1State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if(canonicalCollection.get(i).equals(state)){
                return i;
            }
        }
        return -1;
    }

    private boolean createActionTable() {
        actionTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            actionTable[i] = new HashMap<>();
        }

        for (int i = 0; i < canonicalCollection.size(); i++) {
            for(var s : canonicalCollection.get(i).getTransition().keySet()) {
                if(grammar.getTerminals().contains(s)) {
                    actionTable[i].put(s,new Action(ActionType.SHIFT,findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }

        for (int i = 0; i < canonicalCollection.size(); i++) {
            for(var item : canonicalCollection.get(i).getItems()){
                if(item.getDotPointer() == item.getRighSide().length) {
                    if(item.getLeftSide().equals(Constants.EOI.getValue())) {
                        actionTable[i].put(Constants.EOI.getValue(),new Action(ActionType.ACCEPT,0));
                    } else  {
                        Production rule = new Production(item.getLeftSide(),item.getRighSide().clone());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(ActionType.REDUCE,index);
                        for (var str: item.getLookAhead()
                             ) {
                            if(actionTable[i].get(str) != null) {
                                System.out.println("it has a Reduce-" + actionTable[i].get(str).getType() + "conflict with state " + i);
                                return false;
                            } else {
                                actionTable[i].put(str,action);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void createGoToTable() {
        goToTable = new HashMap[canonicalCollection.size()];
        for (int i = 0; i < goToTable.length; i++) {
            goToTable[i] = new HashMap<>();
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for(var s : canonicalCollection.get(i).getTransition().keySet()){
                if(grammar.isVariable(s)) {
                    goToTable[i].put(s,findStateIndex(canonicalCollection.get(i).getTransition().get(s)));
                }
            }
        }
    }


    public String cannocalCollectionStr() {
        String str = "Cannonical Collection : \n";
        for (int i = 0; i < canonicalCollection.size(); i++) {
            str += "State " + i + ": \n";
            str += canonicalCollection.get(i) + "\n";
        }
        return str;
    }

}
