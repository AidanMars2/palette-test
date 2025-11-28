package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GetBench {
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

    @Param({"true", "false"})
    private boolean direct;

    private int[] positions;
    private int posIdx;

    int nextPos() {
        return positions[posIdx = (posIdx + 1) & (1 << 16) - 1];
    }

    @Setup(Level.Iteration)
    public void setupData() {
        positions = BenchUtils.rng.ints(1 << 16, 0, 1 << 12).toArray();
        palette.setAll((_, _, _) -> BenchUtils.rng.nextInt(direct ? 1 << 16 : 128));
        palette.optimize(direct ? Palette.Optimization.SPEED : Palette.Optimization.SIZE);
    }

    @Benchmark
    public void get(Blackhole bh) {
        for (int i = 0; i < 200; i++) {
            final int locIdx = nextPos();
            bh.consume(palette.get(locIdx & 15, (locIdx >> 4) & 15, (locIdx >> 8) & 15));
        }
    }
}
