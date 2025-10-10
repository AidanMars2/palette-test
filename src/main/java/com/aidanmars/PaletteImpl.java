package com.aidanmars;

import it.unimi.dsi.fastutil.ints.*;
import org.jetbrains.annotations.UnknownNullability;

import static com.aidanmars.Palettes.*;

public final class PaletteImpl implements Palette {
    final byte dimension, minBitsPerEntry, maxBitsPerEntry;
    byte directBits;
    int maxValue;

    byte bitsPerEntry = 0;
    int count = 0; // Serve as the single value if bitsPerEntry == 0

    long[] values;
    // palette index = value
    IntArrayList paletteToValueList;
    // value = palette index
    Int2IntOpenHashMap valueToPaletteMap;

    public PaletteImpl(byte dimension, byte minBitsPerEntry, byte maxBitsPerEntry, int maxValue) {
        validateDimension(dimension);

        this.directBits = (byte) MathUtils.bitsToRepresent(maxValue);
        validateBitsPerEntry(minBitsPerEntry, maxBitsPerEntry, directBits);

        this.dimension = dimension;
        this.minBitsPerEntry = minBitsPerEntry;
        this.maxBitsPerEntry = maxBitsPerEntry;
        this.maxValue = maxValue;
    }

    public PaletteImpl(byte dimension, byte minBitsPerEntry, byte maxBitsPerEntry, int maxValue, byte bitsPerEntry) {
        this(dimension, minBitsPerEntry, maxBitsPerEntry, maxValue);
        if (bitsPerEntry != 0
                && (bitsPerEntry < minBitsPerEntry || bitsPerEntry > maxBitsPerEntry)
                && bitsPerEntry != this.directBits) {
            throw new IllegalArgumentException("Bits per entry must be in range [" + minBitsPerEntry +
                    ", " + maxBitsPerEntry + "] or equal to " + directBits + ". Got " + bitsPerEntry);
        }

        this.bitsPerEntry = bitsPerEntry;
        if (bitsPerEntry != 0) {
            this.values = new long[arrayLength(dimension, bitsPerEntry)];

            if (!isDirect()) {
                this.paletteToValueList = new IntArrayList();
                this.valueToPaletteMap = new Int2IntOpenHashMap();
                this.valueToPaletteMap.defaultReturnValue(-1);
                this.paletteToValueList.add(0);
                this.valueToPaletteMap.put(0, 0);
            }
        }
    }

    @Override
    public int get(int x, int y, int z) {
        validateCoord(dimension, x, y, z);
        if (bitsPerEntry == 0) return count;
        final int value = read(dimension(), bitsPerEntry, values, x, y, z);
        return paletteIndexToValue(value);
    }

    @Override
    public void set(int x, int y, int z, int value) {
        validateCoord(dimension, x, y, z);
        final int paletteIndex = valueToPaletteIndex(value);
        final int oldValue = Palettes.write(dimension(), bitsPerEntry, values, x, y, z, paletteIndex);
        // Check if block count needs to be updated
        final boolean currentAir = paletteIndexToValue(oldValue) == 0;
        if (currentAir != (value == 0)) this.count += currentAir ? 1 : -1;
    }

    @Override
    public void fill(int value) {
        validateValue(value, false);
        this.bitsPerEntry = 0;
        this.count = value;
        this.values = null;
        this.paletteToValueList = null;
        this.valueToPaletteMap = null;
    }

    @Override
    public int count() {
        if (bitsPerEntry == 0) {
            return count == 0 ? 0 : maxSize();
        } else {
            return count;
        }
    }


    @Override
    public int bitsPerEntry() {
        return bitsPerEntry;
    }

    @Override
    public int directBits() {
        return directBits;
    }

    @Override
    public int dimension() {
        return dimension;
    }

    @Override
    public int paletteIndexToValue(int value) {
        return isDirect() ? value : paletteToValueList.elements()[value];
    }

    @Override
    public int valueToPaletteIndex(int value) {
        validateValue(value, true);
        if (isDirect()) return value;
        if (values == null) initIndirect();

        final int lastPaletteIndex = this.paletteToValueList.size();
        final int lookup = valueToPaletteMap.putIfAbsent(value, lastPaletteIndex);
        if (lookup != -1) return lookup;
        if (lastPaletteIndex >= maxPaletteSize(bitsPerEntry)) {
            // Palette is full, must resize
            upsize();
            if (isDirect()) return value;
        }
        this.paletteToValueList.add(value);
        return lastPaletteIndex;
    }

    /// Assumes {@link PaletteImpl#bitsPerEntry} != 0
    int valueToPalettIndexOrDefault(int value) {
        return isDirect() ? value : valueToPaletteMap.get(value);
    }

    @Override
    public int singleValue() {
        return bitsPerEntry == 0 || count == 0 ? count : -1;
    }

    @Override
    public long @UnknownNullability [] indexedValues() {
        return values;
    }

    @Override
    public boolean isDirect() {
        return bitsPerEntry > maxBitsPerEntry;
    }

    void makeDirect() {
        if (isDirect()) return;
        if (bitsPerEntry == 0) {
            final int fillValue = this.count;
            this.values = new long[arrayLength(dimension, directBits)];
            if (fillValue != 0) {
                Palettes.fill(directBits, this.values, fillValue);
                this.count = maxSize();
            }
        } else {
            final int[] ids = paletteToValueList.elements();
            this.values = Palettes.remap(dimension, bitsPerEntry, directBits, values, v -> ids[v]);
        }
        this.paletteToValueList = null;
        this.valueToPaletteMap = null;
        this.bitsPerEntry = directBits;
    }

    void upsize() {
        final byte bpe = this.bitsPerEntry;
        byte newBpe = (byte) (bpe + 1);
        if (newBpe > maxBitsPerEntry) {
            makeDirect();
        } else {
            this.values = Palettes.remap(dimension, bpe, newBpe, values, Int2IntFunction.identity());
            this.bitsPerEntry = newBpe;
        }
    }

    void initIndirect() {
        final int fillValue = this.count;
        this.valueToPaletteMap = new Int2IntOpenHashMap();
        this.valueToPaletteMap.defaultReturnValue(-1);
        this.paletteToValueList = new IntArrayList();
        this.valueToPaletteMap.put(fillValue, 0);
        paletteToValueList.add(fillValue);
        this.bitsPerEntry = minBitsPerEntry;
        this.values = new long[arrayLength(dimension, minBitsPerEntry)];
        this.count = fillValue == 0 ? 0 : maxSize();
    }

    void validateValue(int value, boolean allowResize) {
        if (value < 0) throw new IllegalArgumentException("Palette values must be > 0, got " + value);
        if (value > maxValue) {
            this.maxValue = value;
            final byte newDirectBits = (byte) MathUtils.bitsToRepresent(value);
            if (allowResize && isDirect() && newDirectBits != bitsPerEntry) {
                this.values = Palettes.remap(dimension, bitsPerEntry, newDirectBits, values, Int2IntFunction.identity());
                this.bitsPerEntry = newDirectBits;
            }
            this.directBits = newDirectBits;
        }
    }
}
