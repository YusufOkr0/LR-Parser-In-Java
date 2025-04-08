package com.yusufokr0.dataloader;

public class GrammarRule {

    private int ruleNumber;

    private String left;

    private String right;

    public GrammarRule(int ruleNumber,String left, String right){
        this.ruleNumber=ruleNumber;
        this.left=left;
        this.right=right;
    }


    public GrammarRule() {
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public int getRuleNumber() {
        return ruleNumber;
    }

    public void setRuleNumber(int ruleNumber) {
        this.ruleNumber = ruleNumber;
    }

    @Override
    public String toString() {
        return ruleNumber + ": " + left + " -> " + right;
    }

}
