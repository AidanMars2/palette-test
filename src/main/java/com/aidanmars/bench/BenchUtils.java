package com.aidanmars.bench;

import com.aidanmars.Palette;
import com.aidanmars.PaletteImpl;
import com.aidanmars.test.TestPaletteImpl;

import java.util.Random;

public class BenchUtils {
    public static Palette referencePalette() {
        return new PaletteImpl((byte) 16, (byte) 4, (byte) 8, (1 << 16) - 1);
    }

    public static Palette[] randomReferenceSet(int size) {
        final var result = new Palette[size];
        for (int index = 0; index < size; index++) {
            result[index] = BenchUtils.referencePalette();
        }
        return result;
    }

    public static Palette testPalette() {
        return new TestPaletteImpl((byte) 16, (byte) 4, (byte) 8, (1 << 16) - 1);
    }

    public static Palette[] randomTestSet(int size) {
        final var result = new Palette[size];
        for (int index = 0; index < size; index++) {
            result[index] = BenchUtils.testPalette();
        }
        return result;
    }
}
