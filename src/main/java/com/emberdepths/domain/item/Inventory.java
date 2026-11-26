package com.emberdepths.domain.item;

import com.emberdepths.exception.InventoryFullException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Inventory<T extends Item> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int capacity;
    private final ArrayList<T> items;

    public Inventory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.items = new ArrayList<>();
    }

    public void add(T item) {
        Objects.requireNonNull(item, "item");
        if (isFull()) {
            throw new InventoryFullException(capacity);
        }
        items.add(item);
    }

    public T removeAt(int index) {
        if (index < 0 || index >= items.size()) {
            throw new IllegalArgumentException("There is no item at position " + (index + 1));
        }
        return items.remove(index);
    }

    public void sort(Comparator<? super T> comparator) {
        items.sort(Objects.requireNonNull(comparator, "comparator"));
    }

    public List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int totalValue() {
        return items.stream().mapToInt(Item::getValue).sum();
    }

    public int size() {
        return items.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return items.size() >= capacity;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
