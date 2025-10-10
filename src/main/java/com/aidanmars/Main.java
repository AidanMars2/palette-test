package com.aidanmars;

import com.aidanmars.bench.BenchMark;
import com.aidanmars.bench.GetSetBenchmark;

import java.util.ArrayList;
import java.util.Random;

public class Main {
    private static final long NANO_TO_MILLI_MULT = 1000_000;

    public static void main(String[] args) {
        var rng = new Random(1234567890);
        var benches = new ArrayList<BenchMark>();
        benches.add(new GetSetBenchmark(rng));

        for (BenchMark bench : benches) {
            System.out.println("Running " + bench.name() + " benchmark");
            long startTime = System.nanoTime();
            while (System.nanoTime() - startTime < 100 * NANO_TO_MILLI_MULT) {
                bench.runReference();
                bench.runTest();
            }

            startTime = System.nanoTime();
            int referenceCount = 0;
            while (System.nanoTime() - startTime < 5000 * NANO_TO_MILLI_MULT) {
                bench.runReference();
                referenceCount++;
            }
            System.out.println("Reference throughput for " + bench.name() + ": " + referenceCount);

            startTime = System.nanoTime();
            int testCount = 0;
            while (System.nanoTime() - startTime < 5000 * NANO_TO_MILLI_MULT) {
                bench.runTest();
                testCount++;
            }
            System.out.println("Test throughput for " + bench.name() + ": " + testCount);
            System.out.println("Relative throughput for " + bench.name() + ": " + (((double) testCount) / referenceCount));
        }
    }
}