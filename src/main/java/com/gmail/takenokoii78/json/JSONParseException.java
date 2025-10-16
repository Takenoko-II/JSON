package com.gmail.takenokoii78.json;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class JSONParseException extends RuntimeException {
    protected JSONParseException(String message, String json, int location) {
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
