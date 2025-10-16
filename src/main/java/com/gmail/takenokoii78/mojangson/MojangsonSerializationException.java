package com.gmail.takenokoii78.mojangson;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class MojangsonSerializationException extends RuntimeException {
    protected MojangsonSerializationException(String message) {
        super(message);
    }

    protected MojangsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
