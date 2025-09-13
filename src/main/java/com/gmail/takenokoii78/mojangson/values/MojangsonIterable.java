package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

public interface MojangsonIterable<T extends MojangsonValue<?>> extends MojangsonStructure, Iterable<T> {
    boolean isEmpty();

    boolean has(int index);

    int length();

    boolean clear();

    @NotNull MojangsonIterable<T> copy();
}
