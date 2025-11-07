package com.aidanmars.test;

public class TestMathUtils {
    public static int bitsToRepresent(int n) {
        if (n <= 0) throw new IllegalArgumentException("n must be greater than 0");
        return Integer.SIZE - Integer.numberOfLeadingZeros(n);
    }
}
