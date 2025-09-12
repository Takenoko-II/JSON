package com.gmail.takenokoii78.mojangson;

import org.jetbrains.annotations.NotNull;

public class MojangsonParseException extends RuntimeException {
    protected MojangsonParseException(@NotNull String message, @NotNull String json, int location) {
        super(createMessage(message, json, location));
    }

    protected MojangsonParseException(@NotNull String message, @NotNull String json, int location, @NotNull Throwable cause) {
        super(createMessage(message, json, location), cause);
    }

    protected static @NotNull String createMessage(@NotNull String message, @NotNull String json, int location) {
        return new StringBuilder()
            .append(message)
            .append(": ")
            .append(json, Math.max(0, location - 8), Math.max(0, location))
            .append(" >> ")
            .append(json.charAt(location))
            .append(" << ")
            .append(json, Math.min(location + 1, json.length()), Math.min(location + 8, json.length()))
            .toString();
    }
}
