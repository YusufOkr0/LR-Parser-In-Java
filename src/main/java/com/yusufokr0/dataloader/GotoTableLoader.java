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
            String headerLine = reader.readLine();
            String[] headerParts = headerLine.trim().split("\\s+");
            String[] tokens = new String[headerParts.length-1];
            for(int i= 1;i<headerParts.length;i++){
                tokens[i-1] = headerParts[i];
            }
            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, Integer> gotos = new HashMap<>();
                String[] stringParts = line.trim().split("\\s+");

                int state = Integer.parseInt(stringParts[0]);

                for (int i = 1; i < stringParts.length; i++) {
                    if (stringParts[i].equals("-")) {
                        continue;
                    }
                    gotos.put(tokens[i - 1], Integer.parseInt(stringParts[i]));
                }

                gotoTable.put(state, gotos);
            }

            return gotoTable;

        } catch (IOException e) {
            throw new RuntimeException("Exception occurred while processing GotoTable.txt.");
        }
    }


}