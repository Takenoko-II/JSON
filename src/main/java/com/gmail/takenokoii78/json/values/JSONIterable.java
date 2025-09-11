package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.JSONValue;
import org.jetbrains.annotations.NotNull;

public interface JSONIterable<T extends JSONValue<?>> extends JSONStructure, Iterable<T> {
    boolean isEmpty();

    @NotNull JSONIterable<T> copy();
}
