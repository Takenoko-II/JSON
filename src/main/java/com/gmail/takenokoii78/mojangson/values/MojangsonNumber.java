package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import org.jetbrains.annotations.NotNull;

public class MojangsonNumber<T extends Number> extends MojangsonPrimitive<T> {
    protected MojangsonNumber(@NotNull T value) {
        super(value);
    }

    public final byte byteValue() {
        return value.byteValue();
    }

    public final short shortValue() {
        return value.shortValue();
    }

    public final int intValue() {
        return value.intValue();
    }

    public final long longValue() {
        return value.longValue();
    }

    public final float floatValue() {
        return value.floatValue();
    }

    public final double doubleValue() {
        return value.doubleValue();
    }

    public static @NotNull MojangsonNumber<?> upcastedValueOf(@NotNull Number value) {
        return (MojangsonNumber<?>) MojangsonValueType.of(value).cast(value);
    }
}
