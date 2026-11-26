package com.emberdepths.exception;

public class InsufficientManaException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int required;
    private final int available;

    public InsufficientManaException(String skillName, int required, int available) {
        super("Not enough mana for " + skillName + ": need " + required + " MP, you have " + available + " MP.");
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public int getAvailable() {
        return available;
    }
}
