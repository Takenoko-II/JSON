package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NullMarked
public final class JSONObject extends JSONValue<Map<String, JSONValue<?>>> implements JSONStructure {
    public JSONObject() {
        super(new HashMap<>());
    }

    public JSONObject(Map<String, JSONValue<?>> map) {
        super(map);
    }

    public boolean has(String key) {
        return value.containsKey(key);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public JSONValueType<?> getTypeOf(String key) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        return JSONValueType.get(value.get(key));
    }

    public <T extends JSONValue<?>> T get(String key, JSONValueType<T> type) {
        if (!has(key)) {
            throw new IllegalArgumentException("キー '" + key + "' は存在しません");
        }

        if (!getTypeOf(key).equals(type)) {
            throw new IllegalArgumentException("キー '" + key + "' は期待される型の値と紐づけられていません: " + getTypeOf(key));
        }

        return type.toJSON(value.get(key));
    }

    public void set(String key, Object value) {
        this.value.put(key, JSONValueType.get(value).toJSON(value));
    }

    public boolean delete(String key) {
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

    public void merge(JSONObject jsonObject) {
        for (String key : jsonObject.keys()) {
            set(key, jsonObject.value.get(key));
        }
    }

    public Map<String, @Nullable Object> asMap() {
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
    public JSONObject copy() {
        return JSONValueTypes.OBJECT.toJSON(asMap());
    }

    public boolean isSuperOf(JSONObject other) {
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

    public boolean has(JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::has, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            return false;
        }
    }

    public JSONValueType<?> getTypeOf(JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::getType, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public <T extends JSONValue<?>> T get(JSONPath path, JSONValueType<T> type) {
        try {
            return path.access(this, reference -> reference.get(type), false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean delete(JSONPath path) {
        try {
            return path.access(this, JSONPath.JSONPathReference::delete, false);
        }
        catch (JSONPath.JSONInaccessiblePathException e) {
            throw new IllegalStateException(e);
        }
    }

    public void set(JSONPath path, Object value) {
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
