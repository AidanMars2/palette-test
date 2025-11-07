package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GetSetBench {
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

    private int[] positions;
    private int posIdx;
    private int[] values;
    private int idx;

    int nextValue() {
        return values[idx = (idx + 1) & (1 << 10) - 1];
    }

    int nextPos() {
        return positions[posIdx = (posIdx + 1) & (1 << 16) - 1];
    }

    @Setup(Level.Iteration)
    public void setupData() {
        values = BenchUtils.rng.ints(1 << 10, 0, 128).toArray();
        positions = BenchUtils.rng.ints(1 << 16, 0, 1 << 12).toArray();
    }

    @Benchmark
    public void set() {
        for (int i = 0; i < 200; i++) {
            final int locIdx = nextPos();
            palette.set(locIdx & 15, (locIdx >> 4) & 15, (locIdx >> 8) & 15, nextValue());
        }
    }

    @Benchmark
    public void get(Blackhole bh) {
        for (int i = 0; i < 200; i++) {
            final int locIdx = nextPos();
            bh.consume(palette.get(locIdx & 15, (locIdx >> 4) & 15, (locIdx >> 8) & 15));
        }
    }
}
