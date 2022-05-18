package com.quandz.krakenmobcoins.utils;

public class Timing {

    private final long start;

    public Timing() {
        start = System.currentTimeMillis();
    }

    public long getTotalTime() {
        return System.currentTimeMillis() - start;
    }
}
