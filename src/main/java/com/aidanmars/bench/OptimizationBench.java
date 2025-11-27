package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
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

    @Setup(Level.Iteration)
    public void setupData() {
        final var rng = BenchUtils.rng.ints(0, 32).iterator();
        palette.setAll((_, _, _) -> rng.nextInt());
    }

    @Benchmark
    public void optimize() {
        palette.makeDirect();
        palette.optimize(Palette.Optimization.SIZE);
    }
}
