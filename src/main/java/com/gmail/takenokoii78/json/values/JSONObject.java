package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class JSONObject extends JSONValue<Map<String, JSONValue<?>>> implements JSONStructure {
    public JSONObject() {
        super(new HashMap<>());
    }

    public JSONObject(@NotNull Map<String, JSONValue<?>> map) {
        super(map);
    }

    public boolean has(@NotNull String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull JSONValueType<?> getTypeOf(@NotNull String key) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JSONValueType.of(value.get(key));
    }

    public @NotNull <T extends JSONValue<?>> T get(@NotNull String key, JSONValueType<T> type) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません");
        }

        return type.cast(value.get(key));
    }

    public void set(@NotNull String key, Object value) {
        this.value.put(key, JSONValueType.of(value).cast(value));
    }

    public boolean delete(@NotNull String key) {
        if (has(key)) {
            value.remove(key);
            return true;
        }
        else return false;
    }

    public boolean clear() {
        if (isEmpty()) return false;
        else {
            value.clear();
            return true;
        }
    }

    public Set<String> keys() {
        return value.keySet();
    }

    public void merge(@NotNull JSONObject jsonObject) {
        for (String key : jsonObject.keys()) {
            set(key, jsonObject.value.get(key));
        }
    }

    public Map<String, Object> asMap() {
        final Map<String, Object> map = new HashMap<>();

        for (String key : keys()) {
            final JSONValueType<?> type = getTypeOf(key);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = get(key, JSONValueTypes.OBJECT);
                map.put(key, object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = get(key, JSONValueTypes.ARRAY);
                map.put(key, array.asList());
            }
            else if (value.get(key) instanceof JSONPrimitive<?> primitive) {
                map.put(key, primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(key).getClass().getName());
            }
        }

        return map;
    }

    @Override
    public @NotNull JSONObject copy() {
        return JSONValueTypes.OBJECT.cast(asMap());
    }

    public boolean isSuperOf(@NotNull JSONObject other) {
        for (final String key : other.keys()) {
            if (has(key)) {
                final JSONValue<?> conditionValue = other.get(key, other.getTypeOf(key));

                switch (conditionValue) {
                    case JSONObject jsonObject -> {
                        if (!get(key, JSONValueTypes.OBJECT).isSuperOf(jsonObject)) {
                            return false;
                        }
                    }
                    case JSONArray jsonArray -> {
                        if (!get(key, JSONValueTypes.ARRAY).isSuperOf(jsonArray)) {
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

    public boolean has(@NotNull JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::has, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public @NotNull JSONValueType<?> getTypeOf(@NotNull JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::getType, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T extends JSONValue<?>> @NotNull T get(@NotNull JSONPath path, @NotNull JSONValueType<T> type) {
        try {
            return path.access(this, reference -> reference.get(type), false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean delete(@NotNull JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::delete, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public void set(@NotNull JSONPath path, Object value) {
        try {
            path.access(this, reference -> {
                reference.set(value);
                return null;
            }, true);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }
}
