package com.yusufokr0.dataloader;

import java.io.*;
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

        try (InputStreamReader inputStreamReader = new InputStreamReader(actionTableFile);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                Map<String,String> actions = new HashMap<>();

                String[] parts = line.trim().split("\\s");
                int state = Integer.parseInt(parts[0]);

                for(int i = 1; i < parts.length - 1; i += 2){
                    String token = parts[i];
                    String action = parts[i + 1];
                    actions.put(token, action);
                }
                actionTable.put(state,actions);

            }
            return actionTable;

        } catch (IOException e) {
            throw new RuntimeException("IOException occur while processing ActionTable.txt.");
        }catch (Exception e){
            throw new RuntimeException("Unexpected exception while reading ActionTable.txt. Please check if the given file is valid format.");
        }

    }

}