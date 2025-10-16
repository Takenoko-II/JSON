package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TypedMojangsonList<T extends MojangsonValue<?>> extends MojangsonValue<List<T>> implements MojangsonIterable<T> {
    private final MojangsonValueType<T> type;

    public TypedMojangsonList(@NotNull MojangsonValueType<T> type, @NotNull List<T> list) {
        super(list);
        this.type = type;
    }

    public TypedMojangsonList(@NotNull MojangsonValueType<T> type) {
        this(type, new ArrayList<>());
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else if (value.size() + index >= 0) return has(value.size() + index);
        else return false;
    }

    protected boolean checkTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return MojangsonValueType.get(value.get(index)).equals(type);
        else return MojangsonValueType.get(value.get(value.size() + index)).equals(type);
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
    public @NotNull TypedMojangsonList<T> copy() {
        return untyped().copy().typed(type);
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        final List<T> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i));
        }

        return list.iterator();
    }

    public @NotNull MojangsonList untyped() {
        final MojangsonList list = new MojangsonList();
        for (int i = 0; i < length(); i++) {
            list.add(get(i));
        }
        return list;
    }

    @NotNull
    @Override
    public String toString() {
        return type + super.toString();
    }
}
