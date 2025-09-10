package com.gmail.takenokoii78.json.values;

import org.jetbrains.annotations.NotNull;

public final class JSONString extends JSONPrimitive<String> {
    private JSONString(@NotNull String value) {
        super(value);
    }

    public static @NotNull JSONString valueOf(@NotNull String value) {
        return new JSONString(value);
    }
}
