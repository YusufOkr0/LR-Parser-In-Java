package com.yusufokr0;

import com.yusufokr0.dataloader.GrammarRule;
import com.yusufokr0.dataloader.ParseTreeNode;

import java.io.*;
import java.util.*;

public class LRParser {
    private static final String OUTPUT_BASE_PATH = "target/output-traces/";

    private final Map<Integer, Map<String, String>> actionTable;
    private final Map<Integer, Map<String, Integer>> gotoTable;
    private final List<GrammarRule> grammarRules;
    private final Stack<String> stateAndTokenStack;
    private final List<String> traceTable;


    private final Stack<ParseTreeNode> parseTreeStack;
    private ParseTreeNode rootNode;

    public LRParser(Map<Integer, Map<String, String>> actionTable,
                    Map<Integer, Map<String, Integer>> gotoTable,
                    List<GrammarRule> grammarRules) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        this.grammarRules = grammarRules;
        this.stateAndTokenStack = new Stack<>();
        this.traceTable = new ArrayList<>();
        this.parseTreeStack = new Stack<>();
        this.rootNode = null;
    }

    public void parse(String inputFileName) {
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("inputs/" + inputFileName);
            if (inputStream == null) {
                throw new RuntimeException(String.format("Input file {%s} not found in classpath. Please put the input files inside target/classes/inputs path.", inputFileName));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String input = reader.readLine().trim() + " $";
            String[] inputExpressionTokens = input.split("\\s+");
            reader.close();

            stateAndTokenStack.clear();
            traceTable.clear();
            parseTreeStack.clear();
            stateAndTokenStack.push("0");
            rootNode = null;
            int inputPointer = 0;

            while (true) {
                int currentState = Integer.parseInt(stateAndTokenStack.peek());
                String currentToken = inputExpressionTokens[inputPointer];
                String action = actionTable.getOrDefault(currentState, Collections.emptyMap())
                        .getOrDefault(currentToken, "");

                // Compact stack representation for output
                String stackStr = stateAndTokenStack.toString()
                        .replaceAll("[\\[\\],]", "")
                        .replaceAll("\\s+", "");

                String inputStr = String.join(" ", Arrays.copyOfRange(inputExpressionTokens, inputPointer, inputExpressionTokens.length));
                String actionStr;

                if (action.isEmpty()) {
                    actionStr = "ERROR: Syntax error";
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    break;
                } else if (action.equals("accept")) {
                    actionStr = "accept";
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    break;
                } else if (action.startsWith("S")) {
                    int nextState = Integer.parseInt(action.substring(1));
                    actionStr = "Shift " + nextState;
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);

                    stateAndTokenStack.push(currentToken);
                    stateAndTokenStack.push(String.valueOf(nextState));


                    if (!currentToken.equals("$")) {
                        parseTreeStack.push(new ParseTreeNode(currentToken));
                    }

                    inputPointer++;
                } else if (action.startsWith("R")) {
                    int ruleNum = Integer.parseInt(action.substring(1));
                    GrammarRule rule = grammarRules.get(ruleNum - 1);
                    String[] rightSide = rule.getRight().split("\\s+");
                    int popCount = rightSide.length * 2;


                    for (int i = 0; i < popCount; i++) {
                        stateAndTokenStack.pop();
                    }


                    int newState = Integer.parseInt(stateAndTokenStack.peek());


                    stateAndTokenStack.push(rule.getLeft());


                    Integer gotoState = gotoTable.getOrDefault(newState, Collections.emptyMap()).get(rule.getLeft());
                    if (gotoState == null) {
                        actionStr = "ERROR: No goto state found for " + rule.getLeft();
                        traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                        break;
                    }
                    actionStr = "Reduce " + ruleNum + " (GOTO [" + newState + ", " + rule.getLeft() + "])";
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    stateAndTokenStack.push(String.valueOf(gotoState));


                    ParseTreeNode nonTerminalNode = new ParseTreeNode(rule.getLeft());


                    List<ParseTreeNode> children = new ArrayList<>();
                    for (int i = 0; i < rightSide.length; i++) {
                        if (!parseTreeStack.isEmpty()) {
                            children.add(0, parseTreeStack.pop());
                        }
                    }


                    for (ParseTreeNode child : children) {
                        nonTerminalNode.addChild(child);
                    }


                    parseTreeStack.push(nonTerminalNode);


                    if (rule.getLeft().equals("E")) {
                        rootNode = nonTerminalNode;
                    }
                }
            }

            String outputFileName = inputFileName.replace("input", "output").replace(".txt", ".txt");
            createTraceOutputFile(outputFileName);

        } catch (IOException ex) {
            throw new RuntimeException(String.format("IOEXCEPTION WHILE PARSING THE GIVEN INPUT EXPRESSION FILE : %s.", inputFileName));
        }
    }

    private void createTraceOutputFile(String outputFile) {
        File outputDir = new File(OUTPUT_BASE_PATH);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(OUTPUT_BASE_PATH + outputFile))) {
            writer.println("Stack                                    Input                                    Action                                  ");
            writer.println("-".repeat(105));
            for (String trace : traceTable) {
                String[] parts = trace.split("\t");
                if (parts.length == 3) {
                    writer.printf("%-40s %-40s %-30s%n", parts[0], parts[1], parts[2]);
                } else {
                    writer.println(trace);
                }
            }
            writer.println("-".repeat(105));
            writer.println("Parse tree:");

            // Use the flatten method to generate parse tree output
            if (rootNode != null) {
                for (String node : rootNode.flatten()) {
                    writer.println(node);
                }
            } else {
                writer.println("No parse tree generated due to error.");
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("IOEXCEPTION WHILE CREATING A TRACE TABLE: %s.", e.getMessage()));
        }
    }
}