package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class MojangsonArray<T, U extends MojangsonValue<?>> extends MojangsonPrimitive<T> implements MojangsonIterable<U> {
    protected MojangsonArray(@NotNull T value) {
        super(value);

        if (!value.getClass().isArray()) {
            throw new IllegalArgumentException("配列型でない値はMojangsonArrayに変換できません");
        }
    }

    public abstract @NotNull T toArray();

    protected @NotNull MojangsonList getView(@NotNull TriConsumer<T, Integer, Object> setter) {
        final T array = value;

        final List<MojangsonValue<?>> values = new ArrayList<>();
        forEach(values::add);

        return new MojangsonList(values) {
            @Override
            public void set(int index, Object value1) {
                super.set(index, value1);

                setter.accept(array, (index >= 0) ? index : super.length() + index, value1);
            }

            @Override
            public void add(int index, Object value1) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public void add(Object value1) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }

            @Override
            public void delete(int index) {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています (set(0)を使用してください)");
            }

            @Override
            public void clear() {
                throw new IllegalStateException("MojangsonArrayから作成されたMojangsonListにおいてこの操作は禁じられています");
            }
        };
    }

    public abstract @NotNull MojangsonList listView();

    @FunctionalInterface
    public interface TriConsumer<S, T, U> {
        void accept(S s, T t, U u);
    }
}
