package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class GetAllBench {
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
        palette.fill(0);
        final var rng = BenchUtils.rng;
        for (int i = 0; i < 1024; i++) {
            palette.set(rng.nextInt(16), rng.nextInt(16), rng.nextInt(16), rng.nextInt(128));
        }
    }

    @Benchmark
    public void getAll(Blackhole bh) {
        palette.getAll((_, _, _, v) -> bh.consume(v));
    }

    @Benchmark
    public void getAllPresent(Blackhole bh) {
        palette.getAllPresent((_, _, _, v) -> bh.consume(v));
    }
}
