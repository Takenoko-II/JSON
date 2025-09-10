package com.gmail.takenokoii78.json;

import org.jetbrains.annotations.NotNull;

public class JSONSerializationException extends RuntimeException {
    public JSONSerializationException() {
        super();
    }

    public JSONSerializationException(@NotNull String message) {
        super(message);
    }

    public JSONSerializationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
