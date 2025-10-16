package com.gmail.takenokoii78.json.values;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class JSONNumber extends JSONPrimitive<Number> {
    private JSONNumber(Number value) {
        super(value);
    }

    public byte byteValue() {
        return value.byteValue();
    }

    public short shortValue() {
        return value.shortValue();
    }

    public int intValue() {
        return value.intValue();
    }

    public long longValue() {
        return value.longValue();
    }

    public float floatValue() {
        return value.floatValue();
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public static JSONNumber valueOf(Number value) {
        return new JSONNumber(value);
    }
}
