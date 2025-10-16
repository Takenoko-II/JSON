package com.gmail.takenokoii78.mojangson;

import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public abstract class MojangsonValueType<T extends MojangsonValue<?>> {
    protected final Class<T> clazz;

    protected MojangsonValueType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public abstract T toMojangson(@Nullable Object value);

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MojangsonValueType<?> that = (MojangsonValueType<?>) object;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    public static MojangsonValueType<?> get(@Nullable Object value) {
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
                MojangsonValueTypes.COMPOUND.toMojangson(v);
                yield MojangsonValueTypes.COMPOUND;
            }
            case Collection<?> v -> {
                MojangsonValueTypes.LIST.toMojangson(v);
                yield MojangsonValueTypes.LIST;
            }
            case MojangsonValue<?> v -> get(v.value);
            case null -> MojangsonValueTypes.NULL;
            default -> throw new IllegalArgumentException("対応していない型の値(" + value.getClass().getName() + "型)が渡されました");
        };
    }
}
