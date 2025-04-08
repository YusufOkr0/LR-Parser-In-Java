package com.yusufokr0.dataloader;

import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
    private String value;
    private List<ParseTreeNode> children;

    public ParseTreeNode(String value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(ParseTreeNode child) {
        children.add(child);
    }

    public List<String> flatten() {
        List<String> result = new ArrayList<>();
        flatten("", result);
        return result;
    }

    private void flatten(String prefix, List<String> result) {
        result.add(prefix + value);

        for (ParseTreeNode child : children) {
            child.flatten(prefix + value + "/", result);
        }
    }
}