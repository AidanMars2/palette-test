package com.aidanmars.bench;

import com.aidanmars.Palette;

import java.util.Random;

public class GetSetBenchmark implements BenchMark {
    final Random rng;
    final Palette[] reference, test;
    final int[] values;

    public GetSetBenchmark(Random rng) {
        this.rng = rng;
        this.reference = BenchUtils.randomReferenceSet(256);
        this.test = BenchUtils.randomTestSet(256);
        this.values = rng.ints(128, 0, 128).toArray();
    }

    @Override
    public String name() {
        return "Get/Set";
    }

    @Override
    public void runReference() {
        runPalette(reference[rng.nextInt(reference.length)]);
    }

    @Override
    public void runTest() {
        runPalette(test[rng.nextInt(test.length)]);
    }

    void runPalette(Palette palette) {
        int dimension = palette.dimension();
        int x = rng.nextInt(dimension);
        int y = rng.nextInt(dimension);
        int z = rng.nextInt(dimension);
        if (rng.nextBoolean()) {
            palette.get(x, y, z);
        } else {
            palette.set(x, y, z, values[rng.nextInt(values.length)]);
        }
    }
}
