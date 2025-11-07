package com.aidanmars.bench;

import com.aidanmars.test.TestPaletteImpl;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class CollectPaletteBench {
    private final TestPaletteImpl palette = (TestPaletteImpl) BenchUtils.testPalette();

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
    public void collect(Blackhole bh) {
        bh.consume(palette.collectOptimizedPalette((byte) 8));
    }

    @Benchmark
    public void manual(Blackhole bh) {
        IntSet unique = new IntOpenHashSet();
        palette.getAll((_, _, _, v) -> unique.add(v));
        bh.consume(unique);
    }
}
