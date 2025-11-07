package com.aidanmars;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntUnaryOperator;

@NotNullByDefault
public interface Palette {
    int get(int x, int y, int z);

    void getAll(EntryConsumer consumer);

    void getAllPresent(EntryConsumer consumer);

    void set(int x, int y, int z, int value);

    void setAll(EntrySupplier supplier);

    void replace(int oldValue, int newValue);

    void replace(int x, int y, int z, IntUnaryOperator operator);

    void replaceAll(EntryFunction function);

    void fill(int value);

    void fill(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int value);

    void offset(int offset);

    void copyFrom(Palette source, int offsetX, int offsetY, int offsetZ);

    void copyFrom(Palette source);

    void load(int[] palette, long[] values);

    int count();

    int count(int value);

    default boolean isEmpty() {
        return count() == 0;
    }

    boolean any(int value);

    int height(int x, int z, EntryPredicate predicate);

    int bitsPerEntry();

    int dimension();

    default int maxSize() {
        final int dimension = dimension();
        return dimension * dimension * dimension;
    }

    void optimize(Optimization focus);

    enum Optimization {
        SIZE,
        SPEED,
    }

    boolean compare(Palette palette);

    Palette clone();

    @ApiStatus.Internal
    int paletteIndexToValue(int value);

    @ApiStatus.Internal
    int valueToPaletteIndex(int value);

    @ApiStatus.Internal
    int singleValue();

    @ApiStatus.Internal
    long @Nullable [] indexedValues();

    @FunctionalInterface
    interface EntrySupplier {
        int get(int x, int y, int z);
    }

    @FunctionalInterface
    interface EntryConsumer {
        void accept(int x, int y, int z, int value);
    }

    @FunctionalInterface
    interface EntryFunction {
        int apply(int x, int y, int z, int value);
    }

    @FunctionalInterface
    interface EntryPredicate {
        boolean get(int x, int y, int z, int value);
    }
}
