package com.emberdepths;

import java.util.Random;

public class FixedRandom extends Random {

    private final int value;

    public FixedRandom(int value) {
        this.value = value;
    }

    @Override
    public int nextInt(int bound) {
        return Math.min(value, bound - 1);
    }

    @Override
    public boolean nextBoolean() {
        return value % 2 == 0;
    }
}
