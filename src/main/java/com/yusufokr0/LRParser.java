package com.yusufokr0;

import com.yusufokr0.dataloader.GrammarRule;
import com.yusufokr0.dataloader.ParseTreeNode;

import java.io.*;
import java.util.*;

public class LRParser {
    private static final String OUTPUT_BASE_PATH = "target/output-traces/";
    private static final String LOGS_DIRECTORY_NAME = "target/logs/";

    private final Map<Integer, Map<String, String>> actionTable;
    private final Map<Integer, Map<String, Integer>> gotoTable;
    private final List<GrammarRule> grammarRules;
    private final Stack<String> stateAndTokenStack;
    private final Stack<ParseTreeNode> parseTreeStack;
    private ParseTreeNode rootNode;
    private PrintWriter logWriter;
    private final List<String> traceTable;

    public LRParser(Map<Integer, Map<String, String>> actionTable,
                    Map<Integer, Map<String, Integer>> gotoTable,
                    List<GrammarRule> grammarRules) {
        this.actionTable = actionTable;
        this.gotoTable = gotoTable;
        this.grammarRules = grammarRules;
        this.stateAndTokenStack = new Stack<>();
        this.parseTreeStack = new Stack<>();
        this.rootNode = null;
        this.logWriter = null;
        this.traceTable = new ArrayList<>();
    }

    public void parse(String inputFileName) {
        String logBaseDirectoryPath = LOGS_DIRECTORY_NAME + "/";
        String logFileName = inputFileName.replace("input", "log");
        File logDirectory = new File(logBaseDirectoryPath);
        if (!logDirectory.exists()) {
            logDirectory.mkdirs();
        }

        try {
            logWriter = new PrintWriter(new FileWriter(logBaseDirectoryPath + logFileName));

            logWriter.println("Action table loaded:");
            logWriter.println("-".repeat(60));
            logWriter.println("State\tid\t+\t*\t(\t)\t$");
            for (Map.Entry<Integer, Map<String, String>> entry : actionTable.entrySet()) {
                logWriter.print(entry.getKey() + "\t");
                logWriter.print(entry.getValue().getOrDefault("id", "-") + "\t");
                logWriter.print(entry.getValue().getOrDefault("+", "-") + "\t");
                logWriter.print(entry.getValue().getOrDefault("*", "-") + "\t");
                logWriter.print(entry.getValue().getOrDefault("(", "-") + "\t");
                logWriter.print(entry.getValue().getOrDefault(")", "-") + "\t");
                logWriter.println(entry.getValue().getOrDefault("$", "-") + "\t");
            }
            logWriter.println("-".repeat(60));
            logWriter.println("GOTO table loaded:");
            logWriter.println("-".repeat(40));
            logWriter.println("State\tE\tT\tF");
            for (Map.Entry<Integer, Map<String, Integer>> entry : gotoTable.entrySet()) {
                logWriter.print(entry.getKey() + "\t");
                logWriter.print(String.valueOf(entry.getValue().getOrDefault("E", -1)).replace("-1", "-") + "\t");
                logWriter.print(String.valueOf(entry.getValue().getOrDefault("T", -1)).replace("-1", "-") + "\t");
                logWriter.println(String.valueOf(entry.getValue().getOrDefault("F", -1)).replace("-1", "-") + "\t");
            }
            logWriter.println("-".repeat(40));
            logWriter.println("Grammar rules loaded:");
            logWriter.println("-".repeat(50));
            for (int i = 0; i < grammarRules.size(); i++) {
                GrammarRule rule = grammarRules.get(i);
                logWriter.println("Rule " + (i + 1) + ": " + rule.getLeft() + " -> " + rule.getRight() + " (RHS token count: " + rule.getRight().split("\\s+").length + ")");
            }
            logWriter.println("-".repeat(50));
            logWriter.println("\nParsing started for the input: " + getInputLine(inputFileName));
            logWriter.println();

            InputStream inputStream = ClassLoader.getSystemResourceAsStream("inputs/" + inputFileName);
            if (inputStream == null) {
                throw new RuntimeException(String.format("Input file {%s} not found in classpath.", inputFileName));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String input = reader.readLine().trim();
            String[] inputExpressionTokens = input.split("\\s+");
            reader.close();

            stateAndTokenStack.clear();
            parseTreeStack.clear();
            stateAndTokenStack.push("0");
            rootNode = null;
            int inputPointer = 0;

            while (true) {
                int currentState = Integer.parseInt(stateAndTokenStack.peek());
                String currentToken = inputExpressionTokens[inputPointer];
                String action = actionTable.getOrDefault(currentState, Collections.emptyMap())
                        .getOrDefault(currentToken, "");

                String stackStr = stateAndTokenStack.toString().replaceAll("[\\[\\],]", "").replaceAll("\\s+", "");
                String inputStr = String.join(" ", Arrays.copyOfRange(inputExpressionTokens, inputPointer, inputExpressionTokens.length));
                String actionStr;

                logWriter.println("Current stack content " + stackStr);
                logWriter.println("Current state (stack-top) is " + currentState + ", next token is '" + currentToken + "' (action table column " + getActionTableColumn(currentToken) + ")");

                if (action.isEmpty()) {
                    actionStr = "ERROR: Syntax error at token: " + currentToken;
                    logWriter.println("Fetched action from the table: -");
                    logWriter.println("\nSYNTAX ERROR at token: *");
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    break;
                } else if (action.equals("accept")) {
                    actionStr = "accept";
                    logWriter.println("Fetched action from the table: a");
                    logWriter.println("\nACCEPTED");
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    break;
                } else if (action.startsWith("s")) {
                    int nextState = Integer.parseInt(action.substring(1));
                    actionStr = "Shift " + nextState;
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    logWriter.println("Fetched action from the table: s" + nextState);
                    logWriter.println("\nShift and goto state " + nextState + ": push the token \"" + currentToken + "\" and state " + nextState + " onto the stack");
                    if (!currentToken.equals("$")) {
                        logWriter.println("Parse tree: token \"" + currentToken + "\" becomes a node (leaf)");
                    }
                    stateAndTokenStack.push(currentToken);
                    stateAndTokenStack.push(String.valueOf(nextState));
                    if (!currentToken.equals("$")) {
                        parseTreeStack.push(new ParseTreeNode(currentToken));
                    }
                    inputPointer++;
                    logWriter.println("Move to next token\n");
                } else if (action.startsWith("r")) {
                    int ruleNum = Integer.parseInt(action.substring(1));
                    GrammarRule rule = grammarRules.get(ruleNum - 1);
                    String[] rightSide = rule.getRight().split("\\s+");
                    int popCount = rightSide.length * 2;

                    logWriter.println("Fetched action from the table: r" + ruleNum);
                    logWriter.println("\nReduce using grammar rule " + ruleNum + ": " + rule.getLeft() + " -> " + rule.getRight());
                    logWriter.println("Stack content before reduce: " + stackStr );
                    logWriter.println("Pop tokens and associated states from the stack considering the tokens at RHS of grammar rule (" + ruleNum + ": " + rule.getLeft() + " -> " + rule.getRight() + ")");

                    for (int i = 0; i < popCount; i++) {
                        stateAndTokenStack.pop();
                    }

                    int newState = Integer.parseInt(stateAndTokenStack.peek());
                    logWriter.println("State at stack is " + newState + " (current stack content: " + stateAndTokenStack.toString().replaceAll("[\\[\\],]", "").replaceAll("\\s+", "") + ")");
                    logWriter.println("Push LHS (" + rule.getLeft() + ") of the grammar rule (" + ruleNum + ": " + rule.getLeft() + " -> " + rule.getRight() + ") onto stack");
                    stateAndTokenStack.push(rule.getLeft());

                    Integer gotoState = gotoTable.getOrDefault(newState, Collections.emptyMap()).get(rule.getLeft());
                    if (gotoState == null) {
                        actionStr = "ERROR: No goto state found for " + rule.getLeft();
                        logWriter.println("ERROR: No goto state found for " + rule.getLeft());
                        traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                        break;
                    }
                    actionStr = "Reduce " + ruleNum + " (GOTO [" + newState + ", " + rule.getLeft() + "])";
                    traceTable.add(stackStr + "\t" + inputStr + "\t" + actionStr);
                    logWriter.println("Push new state comes from goto table onto stack: " + gotoState + " (GOTO[" + newState + ", " + rule.getLeft() + "])");
                    stateAndTokenStack.push(String.valueOf(gotoState));

                    ParseTreeNode nonTerminalNode = new ParseTreeNode(rule.getLeft());
                    List<ParseTreeNode> children = new ArrayList<>();
                    for (int i = 0; i < rightSide.length; i++) {
                        if (!parseTreeStack.isEmpty()) {
                            children.add(0, parseTreeStack.pop());
                        }
                    }
                    logWriter.println("Parse tree: grammar rule's LHS (" + rule.getLeft() + ") becomes a node (parent) and tokens at the RHS (" + rule.getRight() + ") becomes its children");
                    for (ParseTreeNode child : children) {
                        nonTerminalNode.addChild(child);
                    }
                    parseTreeStack.push(nonTerminalNode);

                    if (rule.getLeft().equals("E")) {
                        rootNode = nonTerminalNode;
                    }
                    logWriter.println("Stack content after reduce: " + stateAndTokenStack.toString().replaceAll("[\\[\\],]", "").replaceAll("\\s+", "") + "\n");
                }
            }
            String outputFileName = inputFileName.replace("input", "output").replace(".txt", ".txt");
            createTraceOutputFile(outputFileName);
            logWriter.println("\nParse tree:");
            if (rootNode != null) {
                for (String node : rootNode.flatten()) {
                    logWriter.println(node);
                }
            } else {
                logWriter.println("No parse tree generated due to error.");
            }
            logWriter.close();

        } catch (IOException ex) {
            throw new RuntimeException(String.format("IOEXCEPTION WHILE PARSING THE GIVEN INPUT EXPRESSION FILE : %s.", inputFileName));
        } finally {
            if (logWriter != null) {
                logWriter.close();
            }
        }
    }

    private void printParseTree(ParseTreeNode node, String indent, PrintWriter logWriter) {
        logWriter.println(indent + node.value);
        for (ParseTreeNode child : node.children) {
            printParseTree(child, indent + "/", logWriter);
        }
    }

    private String getInputLine(String inputFileName) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("inputs/" + inputFileName)))) {
            return reader.readLine();
        } catch (IOException e) {
            return "Error reading input file";
        }
    }

    private int getActionTableColumn(String token) {
        switch (token) {
            case "id": return 0;
            case "+": return 1;
            case "*": return 2;
            case "(": return 3;
            case ")": return 4;
            case "$": return 5;
            default: return -1;
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