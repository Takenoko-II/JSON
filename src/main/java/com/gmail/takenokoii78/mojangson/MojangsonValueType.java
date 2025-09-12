package com.gmail.takenokoii78.mojangson;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public abstract class MojangsonValueType<T extends MojangsonValue<?>> {
    protected final Class<T> clazz;

    protected MojangsonValueType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T cast(Object value);

    @Override
    public @NotNull String toString() {
        return clazz.getSimpleName();
    }

    public static @NotNull MojangsonValueType<?> of(Object value) {
        return switch (value) {
            case Boolean ignored -> MojangsonValueTypes.BYTE;
            case Byte ignored -> MojangsonValueTypes.BYTE;
            case Short ignored -> MojangsonValueTypes.SHORT;
            case Integer ignored -> MojangsonValueTypes.INT;
            case Long ignored -> MojangsonValueTypes.LONG;
            case Float ignored -> MojangsonValueTypes.FLOAT;
            case Double ignored -> MojangsonValueTypes.DOUBLE;
            case Character ignored -> MojangsonValueTypes.STRING;
            case String ignored -> MojangsonValueTypes.STRING;
            case byte[] ignored -> MojangsonValueTypes.BYTE_ARRAY;
            case int[] ignored -> MojangsonValueTypes.INT_ARRAY;
            case long[] ignored -> MojangsonValueTypes.LONG_ARRAY;
            case Map<?, ?> v -> {
                MojangsonValueTypes.COMPOUND.cast(v);
                yield MojangsonValueTypes.COMPOUND;
            }
            case List<?> v -> {
                MojangsonValueTypes.LIST.cast(v);
                yield MojangsonValueTypes.LIST;
            }
            case MojangsonValue<?> v -> of(v.value);
            case null -> MojangsonValueTypes.NULL;
            default -> throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました");
        };
    }
}
