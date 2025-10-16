package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.JSONValue;
import com.gmail.takenokoii78.json.JSONValueType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NullMarked
public class TypedJSONArray<T extends JSONValue<?>> extends JSONValue<List<T>> implements JSONIterable<T> {
    private final JSONValueType<T> type;

    public TypedJSONArray(JSONValueType<T> type) {
        super(new ArrayList<>());
        this.type = type;
    }

    public TypedJSONArray(JSONValueType<T> type, @NotNull List<T> list) {
        super(new ArrayList<>(list));
        this.type = type;
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

    protected boolean checkTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return JSONValueType.get(value.get(index)).equals(type);
        else return JSONValueType.get(value.get(value.size() + index)).equals(type);
    }

    public T get(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!checkTypeAt(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return value.get(index);
        else return value.get(value.size() + index);
    }

    public void add(int index, T value) {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.add(index, value);
        else this.value.add(this.value.size() + index, value);
    }

    public void add(T value) {
        this.value.add(value);
    }

    public void set(int index, T value) {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, value);
        else this.value.set(this.value.size() + index, value);
    }

    @Override
    public boolean delete(int index) {
        if (has(index)) {
            if (index >= 0) value.remove(index);
            else value.remove(this.value.size() + index);
            return true;
        }
        else return false;
    }

    @Override
    public boolean clear() {
        if (isEmpty()) return false;
        else {
            value.clear();
            return true;
        }
    }

    @Override
    public int length() {
        return value.size();
    }

    @Override
    public TypedJSONArray<T> copy() {
        return untyped().copy().typed(type);
    }

    @Override
    public Iterator<T> iterator() {
        final List<T> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i));
        }

        return list.iterator();
    }

    public JSONArray untyped() {
        final JSONArray array = new JSONArray();
        for (int i = 0; i < length(); i++) {
            array.add(get(i));
        }
        return array;
    }

    @Override
    public String toString() {
        return type + super.toString();
    }
}
