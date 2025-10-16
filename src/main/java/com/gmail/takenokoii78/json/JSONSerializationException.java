package com.gmail.takenokoii78.json;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class JSONSerializationException extends RuntimeException {
    protected JSONSerializationException(String message) {
        super(message);
    }

    protected JSONSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
