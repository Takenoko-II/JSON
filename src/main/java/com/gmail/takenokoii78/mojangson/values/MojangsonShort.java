package com.gmail.takenokoii78.mojangson.values;

import org.jetbrains.annotations.NotNull;

public final class MojangsonShort extends MojangsonNumber<Short> {
    private MojangsonShort(short value) {
        super(value);
    }

    public static @NotNull MojangsonShort valueOf(short value) {
        return new MojangsonShort(value);
    }
}
