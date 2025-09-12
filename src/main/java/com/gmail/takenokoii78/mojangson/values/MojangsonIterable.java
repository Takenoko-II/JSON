package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

public interface MojangsonIterable<T extends MojangsonValue<?>> extends Iterable<T> {
    boolean isEmpty();

    @NotNull MojangsonIterable<T> copy();
}
