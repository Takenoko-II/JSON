package com.gmail.takenokoii78.json.values;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class JSONBoolean extends JSONPrimitive<Boolean> {
    private JSONBoolean(boolean value) {
        super(value);
    }

    public static JSONBoolean valueOf(boolean value) {
        return new JSONBoolean(value);
    }
}
