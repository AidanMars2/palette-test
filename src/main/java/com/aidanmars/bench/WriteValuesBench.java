package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class WriteValuesBench {
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
        final var rng = BenchUtils.rng.ints(0, 1 << 13).iterator();
        palette.setAll((_, _, _) -> rng.nextInt());
    }

    @Benchmark
    public void write(Blackhole bh) {
        palette.writeMaybeResized(bh, 20);
    }
}
