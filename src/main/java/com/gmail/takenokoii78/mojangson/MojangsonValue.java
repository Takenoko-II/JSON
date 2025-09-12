package com.gmail.takenokoii78.mojangson;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class MojangsonValue<T> {
    protected final T value;

    protected MojangsonValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MojangsonValue<?> mv = (MojangsonValue<?>) o;
        return Objects.equals(value, mv.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public @NotNull String toString() {
        return value.toString();
    }
}
