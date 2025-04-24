package com.yusufokr0.dataloader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ActionTableLoader {

    private static final String ACTION_TABLE_FILE_NAME = "config/ActionTable.txt";

    public static Map<Integer, Map<String, String>> loadActionTable(){

        final Map<Integer, Map<String, String>> actionTable = new HashMap<>();

        InputStream actionTableFile = ClassLoader
                .getSystemResourceAsStream(ACTION_TABLE_FILE_NAME);

        if (actionTableFile == null) {
            throw new RuntimeException("ACTION TABLE FILE NOT FOUND IN THE CLASSPATH.");
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(actionTableFile, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String headerLine = reader.readLine();
            String[] headerParts = headerLine.trim().split("\\s+");
            String[] tokens = new String[headerParts.length - 1];
            for (int i = 1; i < headerParts.length; i++) {
                tokens[i-1] = headerParts[i];
            }

            String line;
            while ((line = reader.readLine()) != null) {
                Map<String, String> actions = new HashMap<>();

                String[] parts = line.trim().split("\\s+");

                int state = Integer.parseInt(parts[0]);

                for (int i = 1; i < parts.length; i+=1){
                    if (parts[i].equals("-")) {
                        continue;
                    }
                    actions.put(tokens[i-1], parts[i]);

                }
                actionTable.put(state, actions);

            }

            return actionTable;

        } catch (IOException e) {
            throw new RuntimeException("IOException occur while processing ActionTable.txt.");
        }catch (Exception e){
            throw new RuntimeException("Unexpected exception while reading ActionTable.txt. Please check if the given file is valid format.");
        }

    }

}