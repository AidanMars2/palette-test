package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SetAllBench {
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
        return values[idx = (idx + 1) % values.length];
    }

    @Setup(Level.Iteration)
    public void setupData() {
        values = BenchUtils.rng.ints(10_000, 0, 48).toArray();
    }

    @Benchmark
    public void setAll() {
        palette.setAll((_, _, _) -> nextValue());
    }

    @Benchmark
    public void manual() {
        for (int y = 0; y < 16; y++) {
            for (int z = 0; z < 16; z++) {
                for (int x = 0; x < 16; x++) {
                    palette.set(x, y, z, nextValue());
                }
            }
        }
    }
}
