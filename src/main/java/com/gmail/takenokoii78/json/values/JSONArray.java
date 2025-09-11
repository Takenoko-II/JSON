package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JSONArray extends JSONValue<List<JSONValue<?>>> implements JSONIterable<JSONValue<?>> {
    public JSONArray() {
        super(new ArrayList<>());
    }

    public JSONArray(@NotNull List<JSONValue<?>> list) {
        super(new ArrayList<>(list));
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else if (value.size() + index >= 0) return has(value.size() + index);
        else return false;
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    public JSONValueType<?> getTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return JSONValueType.of(value.get(index));
        else return JSONValueType.of(value.get(value.size() + index));
    }

    public <T extends JSONValue<?>> T get(int index, @NotNull JSONValueType<T> type) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return type.cast(value.get(index));
        else return type.cast(value.get(value.size() + index));
    }

    public void add(int index, Object value) {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.add(index, JSONValueType.of(value).cast(value));
        else this.value.add(this.value.size() + index, JSONValueType.of(value).cast(value));
    }

    public void add(Object value) {
        this.value.add(JSONValueType.of(value).cast(value));
    }

    public void set(int index, Object value) {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, JSONValueType.of(value).cast(value));
        else this.value.set(this.value.size() + index, JSONValueType.of(value).cast(value));
    }

    @Override
    public void delete(int index) {
        if (has(index)) {
            if (index >= 0) value.remove(index);
            else value.remove(value.size() + index);
        }
    }

    @Override
    public void clear() {
        value.clear();
    }

    @Override
    public int length() {
        return value.size();
    }

    @Override
    public @NotNull Iterator<JSONValue<?>> iterator() {
        final List<JSONValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    public @NotNull List<Object> asList() {
        final List<Object> list = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final JSONValueType<?> type = getTypeAt(i);

            if (type.equals(JSONValueTypes.OBJECT)) {
                final JSONObject object = get(i, JSONValueTypes.OBJECT);
                list.add(object.asMap());
            }
            else if (type.equals(JSONValueTypes.ARRAY)) {
                final JSONArray array = get(i, JSONValueTypes.ARRAY);
                list.add(array.asList());
            }
            else if (value.get(i) instanceof JSONPrimitive<?> primitive) {
                list.add(primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(i).getClass().getName());
            }
        }

        return list;
    }

    @Override
    public @NotNull JSONArray copy() {
        return JSONValueTypes.ARRAY.cast(asList());
    }

    public boolean isSuperOf(@NotNull JSONArray other) {
        if (other.length() == 0) return true;

        for (final JSONValue<?> conditionValue : other) {
            if (value.stream().anyMatch(targetValue -> {
                if (targetValue instanceof JSONObject superVal && conditionValue instanceof JSONObject subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else if (targetValue instanceof JSONArray superVal && conditionValue instanceof JSONArray subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else {
                    return targetValue.equals(conditionValue);
                }
            })) {
                return true;
            }
        }

        return false;
    }

    public boolean isArrayOf(@NotNull JSONValueType<?> type) {
        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                return false;
            }
        }

        return true;
    }

    public <T extends JSONValue<?>> TypedJSONArray<T> typed(@NotNull JSONValueType<T> type) {
        final TypedJSONArray<T> array = new TypedJSONArray<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("その型の値でない要素が見つかりました: " + getTypeAt(i).toString());
            }

            final T element = get(i, type);
            array.add(element);
        }

        return array;
    }
}
