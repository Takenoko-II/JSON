package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MojangsonIntArray extends MojangsonArray<int[], MojangsonInt> {
    public MojangsonIntArray(int[] value) {
        super(value);
    }

    @Override
    public @NotNull MojangsonIntArray copy() {
        return from(listView());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public boolean clear() {
        boolean successful = false;
        for (int i = 0; i < value.length; i++) {
            if (value[i] != 0) {
                value[i] = 0;
                successful = true;
            }
        }
        return successful;
    }

    @Override
    public @NotNull Iterator<MojangsonInt> iterator() {
        final List<MojangsonInt> bytes = new ArrayList<>();
        for (final int intValue : value) {
            bytes.add(MojangsonInt.valueOf(intValue));
        }
        return bytes.iterator();
    }

    @Override
    public @NotNull String toString() {
        return "int" + Arrays.toString(value);
    }

    @Override
    public @NotNull int[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public @NotNull MojangsonList listView() {
        return getView((arr, ind, val) -> {
            arr[ind] = (int) val;
        });
    }

    public static @NotNull MojangsonIntArray from(@NotNull MojangsonList list) {
        final int[] ints = new int[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.INT)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            ints[i] = list.get(i, MojangsonValueTypes.INT).intValue();
        }

        return new MojangsonIntArray(ints);
    }
}
