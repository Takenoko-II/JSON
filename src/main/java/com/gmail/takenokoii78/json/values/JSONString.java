package com.gmail.takenokoii78.json.values;

import org.jspecify.annotations.NullMarked;

@NullMarked
public final class JSONString extends JSONPrimitive<String> {
    private JSONString(String value) {
        super(value);
    }

    public static JSONString valueOf(String value) {
        return new JSONString(value);
    }
}
