package com.yusufokr0;

import com.yusufokr0.dataloader.ActionTableLoader;
import com.yusufokr0.dataloader.GotoTableLoader;
import com.yusufokr0.dataloader.GrammarLoader;
import com.yusufokr0.dataloader.GrammarRule;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        final Map<Integer, Map<String, String>> actionTable = ActionTableLoader.loadActionTable();
        final Map<Integer, Map<String, Integer>> gotoTable = GotoTableLoader.loadGotoTable();
        final List<GrammarRule> grammarRules = GrammarLoader.loadGrammarRules();

        final LRParser lrParser = new LRParser(
                actionTable,
                gotoTable,
                grammarRules
        );

        for(int i = 1;i <= 9;i++){
            lrParser.parse(String.format("input-0%s.txt",i));
        }

    }
}