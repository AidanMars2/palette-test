package com.aidanmars.bench;

import com.aidanmars.Palette;
import com.aidanmars.PaletteImpl;
import com.aidanmars.test.TestPaletteImpl;

import java.util.Random;

public class BenchUtils {
    public static Palette randomReference(Random rng) {
        if (rng.nextBoolean()) {
            return new PaletteImpl((byte) 16, (byte) 4, (byte) 8, (1 << 16) - 1);
        } else {
            return new PaletteImpl((byte) 4, (byte) 1, (byte) 3, (1 << 8) - 1);
        }
    }

    public static Palette[] randomReferenceSet(Random rng, int size) {
        final var result = new Palette[size];
        for (int index = 0; index < size; index++) {
            result[index] = BenchUtils.randomReference(rng);
        }
        return result;
    }

    public static Palette randomTest(Random rng) {
        if (rng.nextBoolean()) {
            return new TestPaletteImpl((byte) 16, (byte) 4, (byte) 8, (1 << 16) - 1);
        } else {
            return new TestPaletteImpl((byte) 4, (byte) 1, (byte) 3, 127);
        }
    }

    public static Palette[] randomTestSet(Random rng, int size) {
        final var result = new Palette[size];
        for (int index = 0; index < size; index++) {
            result[index] = BenchUtils.randomTest(rng);
        }
        return result;
    }
}
