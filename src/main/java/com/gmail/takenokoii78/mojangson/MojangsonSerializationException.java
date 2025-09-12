package com.gmail.takenokoii78.mojangson;

import org.jetbrains.annotations.NotNull;

public class MojangsonSerializationException extends RuntimeException {
    protected MojangsonSerializationException(@NotNull String message) {
        super(message);
    }

    protected MojangsonSerializationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
