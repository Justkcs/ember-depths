package com.emberdepths.exception;

public class InventoryFullException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int capacity;

    public InventoryFullException(int capacity) {
        super("Your bag is full (" + capacity + "/" + capacity + ").");
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }
}
