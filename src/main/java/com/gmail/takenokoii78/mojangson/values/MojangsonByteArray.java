package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MojangsonByteArray extends MojangsonArray<byte[], MojangsonByte> {
    public MojangsonByteArray(byte[] value) {
        super(value);
    }

    @Override
    public @NotNull MojangsonByteArray copy() {
        return from(listView());
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    @Override
    public @NotNull Iterator<MojangsonByte> iterator() {
        final List<MojangsonByte> bytes = new ArrayList<>();
        for (final byte byteValue : value) {
            bytes.add(MojangsonByte.valueOf(byteValue));
        }
        return bytes.iterator();
    }

    @Override
    public @NotNull String toString() {
        return "byte" + Arrays.toString(value);
    }

    @Override
    public @NotNull byte[] toArray() {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public @NotNull MojangsonList listView() {
        return getView((arr, ind, val) -> {
            arr[ind] = (byte) val;
        });
    }

    public static @NotNull MojangsonByteArray from(@NotNull MojangsonList list) {
        final byte[] bytes = new byte[list.length()];

        for (int i = 0; i < list.length(); i++) {
            if (!list.getTypeAt(i).equals(MojangsonValueTypes.BYTE)) {
                throw new IllegalArgumentException("キャストに失敗しました");
            }

            bytes[i] = list.get(i, MojangsonValueTypes.BYTE).byteValue();
        }

        return new MojangsonByteArray(bytes);
    }
}
