package com.aidanmars.bench;

import com.aidanmars.Palette;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.function.IntUnaryOperator;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ReplaceAllBench {
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
    public void replaceAll(Blackhole bh) {
        palette.replaceAll((_, _, _, v) -> {
            bh.consume(v);
            return nextValue();
        });
    }

//    @Benchmark
//    public void manual(Blackhole bh) {
//        for (int y = 0; y < 16; y++) {
//            for (int z = 0; z < 16; z++) {
//                for (int x = 0; x < 16; x++) {
//                    bh.consume(palette.get(x, y, z));
//                    palette.set(x, y, z, nextValue());
//                }
//            }
//        }
//    }
}
