package com.gmail.takenokoii78.mojangson;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MojangsonParseException extends RuntimeException {
    protected MojangsonParseException(String message, String json, int location) {
        super(
            String.format(
                message + "; pos: %s >> %s << %s",
               json.substring(Math.max(0, location - 8), Math.max(0, location)),
                location >= json.length() ? "" : json.charAt(location),
                json.substring(Math.min(location + 1, json.length()), Math.min(location + 8, json.length()))
            )
        );
    }
}
