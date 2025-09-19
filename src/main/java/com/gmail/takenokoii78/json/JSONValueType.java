package com.gmail.takenokoii78.json;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public abstract class JSONValueType<T extends JSONValue<?>> {
    protected final Class<T> clazz;

    protected JSONValueType(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        JSONValueType<?> that = (JSONValueType<?>) object;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }

    public abstract T cast(Object value) throws IllegalArgumentException;

    @Override
    public String toString() {
        return clazz.getSimpleName();
    }

    public static @NotNull JSONValueType<?> of(Object value) {
        return switch (value) {
            case JSONValue<?> jsonValue -> of(jsonValue.value);
            case Boolean ignored -> JSONValueTypes.BOOLEAN;
            case Number ignored -> JSONValueTypes.NUMBER;
            case String ignored -> JSONValueTypes.STRING;
            case Map<?, ?> ignored -> JSONValueTypes.OBJECT;
            case Iterable<?> ignored -> JSONValueTypes.ARRAY;
            case Character ignored -> JSONValueTypes.STRING;
            case null -> JSONValueTypes.NULL;
            default -> {
                if (value.getClass().isArray()) yield JSONValueTypes.ARRAY;
                else throw new IllegalArgumentException("渡された値はjsonで使用できない型です: " + value.getClass().getName());
            }
        };
    }
}
