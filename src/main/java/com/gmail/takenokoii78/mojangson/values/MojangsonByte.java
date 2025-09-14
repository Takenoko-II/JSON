package com.gmail.takenokoii78.mojangson.values;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MojangsonByte extends MojangsonNumber<Byte> {
    private MojangsonByte(byte value) {
        super(value);
    }

    public boolean isBooleanValue() {
        return value == 0 || value == 1;
    }

    public boolean getAsBooleanValue() throws MojangsonNonBooleanValueException {
        if (isBooleanValue()) return value != 0;
        else throw new MojangsonNonBooleanValueException(value);
    }

    public @Nullable Boolean getAsBooleanValueOrNull() {
        if (isBooleanValue()) return value != 0;
        else return null;
    }

    public static @NotNull MojangsonByte valueOf(byte value) {
        return new MojangsonByte(value);
    }

    public static final class MojangsonNonBooleanValueException extends RuntimeException {
        private MojangsonNonBooleanValueException(byte value) {
            super("MojangsonByteから真偽値への変換に失敗しました: ラップされた値は 0 または 1 でなければなりませんが、実際の値は " + value + " です");
        }
    }
}
