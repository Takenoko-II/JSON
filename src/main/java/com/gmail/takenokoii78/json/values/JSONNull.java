package com.gmail.takenokoii78.json.values;

import org.jetbrains.annotations.Nullable;

public final class JSONNull extends JSONPrimitive<Object> {
    private JSONNull() {
        super(null);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public @Nullable Object getValue() {
        return super.getValue();
    }

    public static final JSONNull NULL = new JSONNull();
}
