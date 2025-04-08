package com.yusufokr0.dataloader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/***
 * E -> E + T
 * E -> T
 * T -> T * F
 * T -> F
 * F -> ( E )
 * F -> id
 * -----------------------------------------------------------------
 * RESPONSIBLE FOR READING A TXT FILE WHICH CONTAINS THE INPUT ABOVE.
 * -----------------------------------------------------------------
 */
public class GrammarLoader {

    private static final String GRAMMAR_FILE_NAME = "config/Grammar.txt";

    public static List<GrammarRule> loadGrammarRules(){

        final List<GrammarRule> grammarRules = new ArrayList<>();

        InputStream grammarFile = ClassLoader
                .getSystemResourceAsStream(GRAMMAR_FILE_NAME);

        if (grammarFile == null) {
            throw new RuntimeException("GRAMMAR FILE NOT FOUND IN THE CLASSPATH.");
        }

        try(InputStreamReader inputStreamReader = new InputStreamReader(grammarFile);
            BufferedReader reader = new BufferedReader(inputStreamReader)){

            int ruleNumberCounter = 1;
            String line;
            while ((line = reader.readLine()) != null){
                String[] grammarParts = line.split("->");

                String leftSide = grammarParts[0].trim();
                String rightSide = grammarParts[1].trim();

                GrammarRule newGrammarRule = new GrammarRule(
                        ruleNumberCounter,
                        leftSide,
                        rightSide
                );
                grammarRules.add(newGrammarRule);
                ruleNumberCounter++;
            }

            return grammarRules;

        } catch (IOException e) {
            throw new RuntimeException("Exception occur while processing Grammar.txt.");
        }

    }





}
