package com.gmail.takenokoii78.json;

import org.jetbrains.annotations.NotNull;

public class JSONSerializationException extends RuntimeException {
    protected JSONSerializationException(@NotNull String message) {
        super(message);
    }

    protected JSONSerializationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
