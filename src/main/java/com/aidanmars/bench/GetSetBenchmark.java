package com.aidanmars.bench;

import com.aidanmars.Palette;

import java.util.Random;

public class GetSetBenchmark implements BenchMark {
    final Random rng;
    final Palette[] reference, test;

    public GetSetBenchmark(Random rng) {
        this.rng = rng;
        this.reference = BenchUtils.randomReferenceSet(rng, 64);
        this.test = BenchUtils.randomTestSet(rng, 64);
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
            int value = rng.nextInt(1 << palette.directBits());
            palette.set(x, y, z, value);
        }
    }
}
