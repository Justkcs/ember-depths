package com.emberdepths.exception;

public class InputClosedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InputClosedException() {
        super("The input stream was closed");
    }
}
