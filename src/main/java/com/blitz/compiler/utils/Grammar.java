package com.blitz.compiler.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;

public class Grammar {
    private ArrayList<Production> rules;
    private HashSet<String> terminals;
    private HashSet<String> variables;
    private String startVariable;
    private HashMap<String,HashSet<String>> firstSets;
    private HashMap<String,HashSet<String>> followSets;

    public Grammar(String s) {
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        variables = new HashSet<>();
        Integer line = 0;
        /// Lex tokenizer
        for(var st : s.split("\n")) {
            var sides = st.split("->");
            var leftSide = sides[0].trim();
            variables.add(leftSide);
            var rulesrightSide = sides[1].trim().split("\\|");
            for (var rule: rulesrightSide) {
               var rightSide = rule.trim().split("\\s+");
               for(var terminal : rightSide){
                    terminals.add(terminal);
               }

               if(line == 0){
                   startVariable = leftSide;
                   rules.add(new Production(Constants.START.getValue(),new String[]{startVariable}));
               }
               rules.add(new Production(leftSide,rightSide));
               line++;
            }
        }

        for(var variable : variables) {
            terminals.remove(variable);
        }

        System.out.println("Productions: ");
        var i = 0;
        for(var rule : rules){
            System.out.println(i +" "+rule);
            i += 1;
        }
    }

    public HashSet<String> computeFirst(String[] strSeq,Integer index) {
        HashSet<String> first = new HashSet<>();
        if(index == strSeq.length) {
            return first;
        }

        if(terminals.contains(strSeq[index])) {
            first.add(strSeq[index]);
            return first;
        }

        if(variables.contains(strSeq[index])) {
            for(var str : firstSets.get(strSeq[index])) {
                first.add(str);
            }
        }

        if(first.contains(Constants.EPSILON.getValue())) {
            if(index != strSeq.length - 1) {
                first.addAll(computeFirst(strSeq,index+1));
                first.remove(Constants.EPSILON.getValue());
            }
        }
        return first;
    }

    public HashSet<Production> getRuleByLeftVariable(String variable) {
        HashSet<Production> variableRules = new HashSet<>();
        for(var rule : rules) {
            if(rule.getLeftSide().equals(variable)){
                variableRules.add(rule);
            }
        }

        return variableRules;
    }

    private void computeFirstSets(){
        firstSets = new HashMap<>();
        for(var s : variables){
            var temp = new HashSet<String>();
            firstSets.put(s,temp);
        }

        while (true) {
            boolean isChanged = false;
            for(var variable : variables) {
                HashSet<String> firstSet = new HashSet<>();
                for(Production rule : rules) {
                    if(rule.getLeftSide().equals(variable)){
                        HashSet<String> addAll = computeFirst(rule.getRightSide(),0);
                        firstSet.addAll(addAll);
                    }
                }
                if(!firstSets.get(variable).containsAll(firstSet)) {
                    isChanged = true;
                    firstSets.get(variable).addAll(firstSet);
                }
            }
            /// 如果不再变化就认为firstset构造完毕
            if(!isChanged) break;
        }

        firstSets.put(Constants.START.getValue(),firstSets.get(startVariable));
    }

    private void computeFollowSets() {
        followSets = new HashMap<>();
        for(var s : variables) {
            HashSet<String> temp = new HashSet<>();
            followSets.put(s,temp);
        }

        HashSet<String> start = new HashSet<>();
        start.add(Constants.EOI.getValue());
        followSets.put(Constants.START.getValue(),start);
        while (true) {
            boolean isChanged = false;
            for(var variable : variables) {
                for(Production rule : rules) {
                    for (int i = 0; i < rule.getRightSide().length; i++) {
                        if(rule.getRightSide()[i].equals(variable)){
                            followSets.get(variable).addAll(followSets.get(rule.leftSide));
                        } else {
                            HashSet<String> first = computeFirst(rule.getRightSide(),i+1);
                            /// 空串跳过
                            if(first.contains(Constants.EPSILON.getValue())) {
                                first.remove(Constants.EPSILON.getValue());
                                first.addAll(followSets.get(rule.leftSide));
                            }
                            if(!followSets.get(variable).containsAll(first)) {
                                isChanged = true;
                                followSets.get(variable).addAll(first);
                            }
                        }
                    }
                }
                if(!isChanged) break;
            }
        }
    }

    public int findRuleIndex(Production rule) {

        return 0;
    }

    public boolean isVariable(String v) {
        return variables.contains(v);
    }

    public HashMap<String, HashSet<String>> getFirstSets() {
        return firstSets;
    }

    public HashMap<String, HashSet<String>> getFollowSets() {
        return followSets;
    }

    public HashSet<String> getTerminals() {
        return terminals;
    }

    public HashSet<String> getVariables() {
        return variables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grammar grammar = (Grammar) o;
        return rules.equals(grammar.rules) &&
                terminals.equals(grammar.terminals) &&
                variables.equals(grammar.variables);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.rules);
        hash = 37 * hash + Objects.hashCode(this.terminals);
        hash = 37 * hash + Objects.hashCode(this.variables);
        return hash;
    }

    public ArrayList<Production> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "rules : { " + rules.stream().map(val -> val.toString() + "\n").collect(Collectors.joining()) +
                "}\\n}";
    }
}
