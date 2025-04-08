package com.yusufokr0.dataloader;

import java.io.*;
import java.util.*;

public class GotoTableLoader {
    private static final String GOTO_TABLE_FILE_NAME = "config/GotoTable.txt";

    public static Map<Integer, Map<String, Integer>> loadGotoTable() {
        Map<Integer, Map<String, Integer>> gotoTable = new HashMap<>();

        InputStream gotoFile = ClassLoader
                .getSystemResourceAsStream(GOTO_TABLE_FILE_NAME);

        if (gotoFile == null) {
            throw new RuntimeException("GOTO TABLE FILE NOT FOUND IN THE CLASSPATH.");
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(gotoFile);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(" ");
                int state = Integer.parseInt(parts[0]);
                Map<String, Integer> gotos = new HashMap<>();

                if (parts.length > 1) {
                    for (int i = 1; i < parts.length; i += 2) {
                        String nonTerminal = parts[i];
                        int nextState = Integer.parseInt(parts[i + 1]);
                        gotos.put(nonTerminal, nextState);
                    }
                }
                gotoTable.put(state, gotos);
            }

            return gotoTable;

        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while processing GotoTable.txt.");
        }
    }


}