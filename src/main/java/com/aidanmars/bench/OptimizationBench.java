package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class OptimizationBench {
    @Param({"reference", "optimized"})
    private String impl;
    private Palette palette;

    @Setup(Level.Trial)
    public void setupPalette() {
        switch (impl) {
            case "reference": palette = BenchUtils.referencePalette(); break;
            case "optimized": palette = BenchUtils.testPalette(); break;
        }
    }

    private int[] bigValues;
    private int bigIdx;
    private int[] smallValues;
    private int smallIdx;

    int nextBig() {
        return bigValues[bigIdx = (bigIdx + 1) % 10_000];
    }

    int nextSmall() {
        return smallValues[smallIdx = (smallIdx + 1) % 10_000];
    }

    @Setup(Level.Iteration)
    public void setupData() {
        bigValues = BenchUtils.rng.ints(10_000, 0, 128).toArray();
        smallValues = BenchUtils.rng.ints(10_000, 0, 16).toArray();
    }

    @Setup(Level.Invocation)
    public void setupOptimizable() {
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    palette.set(x, y, z, nextBig());
                }
            }
        }
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    palette.set(x, y, z, nextSmall());
                }
            }
        }
    }

    @Benchmark
    public void optimize() {
        palette.optimize(Palette.Optimization.SIZE);
    }
}
