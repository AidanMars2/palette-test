package com.aidanmars.bench;

import com.aidanmars.Palette;
import com.aidanmars.reference.PaletteImpl;
import com.aidanmars.test.TestPaletteImpl;

import java.util.Random;

public class BenchUtils {
    public static final Random rng = new Random(42);

    public static Palette referencePalette() {
        return new PaletteImpl((byte) 16, (byte) 4, (byte) 8, (byte) 16);
    }

    public static Palette testPalette() {
        return new TestPaletteImpl((byte) 16, (byte) 4, (byte) 8, (byte) 16);
    }
}
