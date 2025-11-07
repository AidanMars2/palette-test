package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class FillBench {
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

    private int[] values;
    private int idx;

    int nextValue() {
        return values[idx = (idx + 1) & (1 << 10) - 1];
    }

    @Setup(Level.Iteration)
    public void setupData() {
        values = BenchUtils.rng.ints(1 << 10, 0, 128).toArray();
    }

    @Benchmark
    public void fill() {
        palette.fill(0, 0, 0, 8, 8, 8, nextValue());
        palette.fill(3, 3, 3, 12, 12, 12, nextValue());
        palette.fill(8, 8, 8, 15, 15, 15, nextValue());
    }
}
