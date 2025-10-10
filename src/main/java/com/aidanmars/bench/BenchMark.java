package com.aidanmars.bench;

public interface BenchMark {
    String name();

    void runReference();

    void runTest();
}
