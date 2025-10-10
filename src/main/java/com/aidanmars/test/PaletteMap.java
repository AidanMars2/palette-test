/*
	* Copyright (C) 2002-2024 Sebastiano Vigna
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	*     http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
package com.aidanmars.test;

import it.unimi.dsi.fastutil.HashCommon;

import static it.unimi.dsi.fastutil.Hash.DEFAULT_INITIAL_SIZE;
import static it.unimi.dsi.fastutil.Hash.DEFAULT_LOAD_FACTOR;
import static it.unimi.dsi.fastutil.HashCommon.arraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;

@SuppressWarnings("unused")
public final class PaletteMap {
	private int[] key;
    private int[] value;
    private int mask;
    private boolean containsNullKey;
    private int n;
    private int maxFill;
	private int size;
    private static final float f = DEFAULT_LOAD_FACTOR;

    PaletteMap(long bits) {
        n = 1 << (bits + 1);
        mask = n - 1;
        maxFill = 1 << bits;
        key = new int[n + 1];
        value = new int[n + 1];
    }

	PaletteMap(final int expected) {
		n = arraySize(expected, f);
		mask = n - 1;
		maxFill = maxFill(n, f);
		key = new int[n + 1];
		value = new int[n + 1];
	}

	public PaletteMap() {
		this(DEFAULT_INITIAL_SIZE);
	}

	private void insert(final int pos, final int k, final int v) {
		if (pos == n) containsNullKey = true;
		key[pos] = k;
		value[pos] = v;
		if (++size > maxFill) rehash(arraySize(size, f));
	}

	public int put(final int k, final int v) {
		final int pos = find(k);
		if (pos < 0) {
			insert(~pos, k, v);
			return -1;
		}
		final int oldValue = value[pos];
		value[pos] = v;
		return oldValue;
	}

	public int get(final int k) {
        final int pos = find(k);
        if (pos < 0) return -1;
        return value[pos];
	}

	public int putIfAbsent(final int k, final int v) {
		final int pos = find(k);
		if (pos >= 0) return value[pos];
		insert(~pos, k, v);
		return -1;
	}

    public int remove(final int k) {
        int oldValue;
        if (k == 0) {
            if (!containsNullKey) return -1;
            containsNullKey = false;
            oldValue = value[n];
        } else {
            int pos = find(k);
            if (pos < 0) return -1;
            oldValue = value[pos];
            shiftKeys(pos);
        }
        size--;
        if (size < maxFill >> 2 && n > DEFAULT_INITIAL_SIZE) rehash(n >> 1);
        return oldValue;
    }

    private int find(final int k) {
        if (k == 0) return containsNullKey ? n : ~n;
        int curr;
        final int[] key = this.key;
        int pos;
        // The starting point.
        if ((curr = key[pos = (HashCommon.mix((k))) & mask]) == 0) return ~pos;
        if (k == curr) return pos;
        // There's always an unused entry.
        while (true) {
            if ((curr = key[pos = (pos + 1) & mask]) == 0) return ~pos;
            if (k == curr) return pos;
        }
    }

    void shiftKeys(int pos) {
        int last, slot;
        int curr;
        final int[] key = this.key;
        final int[] value = this.value;
        while (true) {
            pos = ((last = pos) + 1) & mask;
            while (true) {
                if ((curr = key[pos]) == 0) {
                    key[last] = 0;
                    return;
                }
                slot = (HashCommon.mix(curr)) & mask;
                if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
                pos = (pos + 1) & mask;
            }
            key[last] = curr;
            value[last] = value[pos];
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void rehash(final int newN) {
		final int[] key = this.key;
		final int[] value = this.value;
		final int mask = newN - 1;
		final int[] newKey = new int[newN + 1];
		final int[] newValue = new int[newN + 1];
		int i = n, pos;
		for (int j = containsNullKey ? size - 1 : size; j-- != 0;) {
			while (key[--i] == 0);
			if (!(newKey[pos = HashCommon.mix(key[i]) & mask] == 0))
                while (!(newKey[pos = (pos + 1) & mask] == 0));
			newKey[pos] = key[i];
			newValue[pos] = value[i];
		}
		newValue[newN] = value[n];
		n = newN;
		this.mask = mask;
		maxFill = maxFill(n, f);
		this.key = newKey;
		this.value = newValue;
	}
}
