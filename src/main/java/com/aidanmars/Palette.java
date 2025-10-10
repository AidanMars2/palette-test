package com.aidanmars;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnknownNullability;

public interface Palette {
    int get(int x, int y, int z);

    void set(int x, int y, int z, int value);

    void fill(int value);

    /**
     * @return the number of entries in this palette.
     */
    int count();

    /**
     * @return the number of bits used per entry.
     */
    int bitsPerEntry();

    int directBits();

    /**
     * @return the side length of this palette
     */
    int dimension();

    /**
     * @return the maximum number of entries in this palette.
     */
    default int maxSize() {
        final int dimension = dimension();
        return dimension * dimension * dimension;
    }

    /**
     * Gets the value associated with the given palette index.
     * Assumes {@code {bitsPerEntry() != 0}}.
     */
    @ApiStatus.Internal
    int paletteIndexToValue(int paletteIndex);

    /**
     * Gets the palette index of the given value.
     * This may change the {@code {bitsPerEntry()}} of this palette.
     */
    @ApiStatus.Internal
    int valueToPaletteIndex(int value);

    /**
     * Gets fill value of this palette, if it is guaranteed to be filled with that value. Otherwise, returns -1.
     * If this returns -1, {@link Palette#bitsPerEntry()} != 0.
     */
    @ApiStatus.Internal
    int singleValue();

    /**
     * @return true if this palette is in direct mode
     */
    @ApiStatus.Internal
    boolean isDirect();

    /**
     * If {@link Palette#bitsPerEntry()} == 0: returns null,
     * else returns the value array.<p>
     * Should not be modified.
     */
    @ApiStatus.Internal
    long @UnknownNullability [] indexedValues();
}
