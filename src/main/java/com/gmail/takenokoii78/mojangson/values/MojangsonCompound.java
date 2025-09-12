package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MojangsonCompound extends MojangsonValue<Map<String, MojangsonValue<?>>> implements MojangsonStructure {
    public MojangsonCompound(@NotNull Map<String, MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonCompound() {
        this(new HashMap<>());
    }

    public boolean has(@NotNull String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull MojangsonValueType<?> getTypeOf(@NotNull String key) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return MojangsonValueType.of(value.get(key));
    }

    public @NotNull <T extends MojangsonValue<?>> T get(@NotNull String key, MojangsonValueType<T> type) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(key));
    }

    public void set(@NotNull String key, Object value) {
        this.value.put(key, MojangsonValueType.of(value).cast(value));
    }

    public void delete(@NotNull String key) {
        if (has(key)) value.remove(key);
    }

    public void clear() {
        value.clear();
    }

    public @NotNull Set<String> keys() {
        return Set.copyOf(value.keySet());
    }

    public @NotNull Map<String, Object> toMap() {
        final Map<String, Object> map = new HashMap<>();

        for (final String key : keys()) {
            final MojangsonValueType<?> type = getTypeOf(key);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = get(key, MojangsonValueTypes.COMPOUND);
                map.put(key, compound.toMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = get(key, MojangsonValueTypes.LIST);
                map.put(key, list.toList());
            }
            else if (value.get(key) instanceof MojangsonArray<?, ?> array) {
                map.put(key, array.toArray());
            }
            else if (value.get(key) instanceof MojangsonPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public @NotNull MojangsonStructure copy() {
        return MojangsonValueTypes.COMPOUND.cast(toMap());
    }

    public boolean isSuperOf(@NotNull MojangsonCompound other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final MojangsonValue<?> conditionValue = other.get(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case MojangsonCompound jsonObject -> {
                        if (!get(key, MojangsonValueTypes.COMPOUND).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case MojangsonList jsonArray -> {
                        if (!get(key, MojangsonValueTypes.LIST).isSuperOf(jsonArray)) {
                            return false;
                        }
                    }
                    default -> {
                        if (!get(key, getTypeOf(key)).equals(conditionValue)) {
                            return false;
                        }
                    }
                }
            }
            else return false;
        }

        return true;
    }
}
