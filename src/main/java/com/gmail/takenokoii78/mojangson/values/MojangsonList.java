package com.gmail.takenokoii78.mojangson.values;

import com.gmail.takenokoii78.mojangson.MojangsonValue;
import com.gmail.takenokoii78.mojangson.MojangsonValueType;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MojangsonList extends MojangsonValue<List<MojangsonValue<?>>> implements MojangsonIterable<MojangsonValue<?>> {
    public MojangsonList(@NotNull List<MojangsonValue<?>> value) {
        super(value);
    }

    public MojangsonList() {
        this(new ArrayList<>());
    }

    @Override
    public boolean has(int index) {
        if (index >= 0) return index < value.size();
        else return has(value.size() + index);
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    public @NotNull MojangsonValueType<?> getTypeAt(int index) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (index >= 0) return MojangsonValueType.get(value.get(index));
        else return MojangsonValueType.get(value.get(value.size() + index));
    }

    public <T extends MojangsonValue<?>> T get(int index, @NotNull MojangsonValueType<T> type) {
        if (!has(index)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は存在しません");
        }

        if (!getTypeAt(index).equals(type)) {
            throw new IllegalArgumentException("インデックス '" + index + "' は期待される型の値と紐づけられていません");
        }

        if (index >= 0) return type.toMojangson(value.get(index));
        else return type.toMojangson(value.get(value.size() + index));
    }

    public void add(int index, Object value) {
        if (index > this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.add(index, MojangsonValueType.get(value).toMojangson(value));
        else this.value.add(this.value.size() + index, MojangsonValueType.get(value).toMojangson(value));
    }

    public void add(Object value) {
        this.value.add(MojangsonValueType.get(value).toMojangson(value));
    }

    public void set(int index, Object value) {
        if (index >= this.value.size()) {
            throw new IllegalArgumentException("そのインデックスは使用できません");
        }

        if (index >= 0) this.value.set(index, MojangsonValueType.get(value).toMojangson(value));
        else this.value.set(this.value.size() + index, MojangsonValueType.get(value).toMojangson(value));
    }

    public boolean delete(int index) {
        if (has(index)) {
            if (index >= 0) value.remove(index);
            else value.remove(value.size() + index);
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
    public @NotNull Iterator<MojangsonValue<?>> iterator() {
        final List<MojangsonValue<?>> list = new ArrayList<>();

        for (int i = 0; i < this.value.size(); i++) {
            list.add(get(i, getTypeAt(i)));
        }

        return list.iterator();
    }

    public @NotNull List<Object> toList() {
        final List<Object> arrayList = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            final MojangsonValueType<?> type = getTypeAt(i);

            if (type.equals(MojangsonValueTypes.COMPOUND)) {
                final MojangsonCompound compound = get(i, MojangsonValueTypes.COMPOUND);
                arrayList.add(compound.toMap());
            }
            else if (type.equals(MojangsonValueTypes.LIST)) {
                final MojangsonList list = get(i, MojangsonValueTypes.LIST);
                arrayList.add(list.toList());
            }
            else if (value.get(i) instanceof MojangsonArray<?, ?> array) {
                arrayList.add(array.toArray());
            }
            else if (value.get(i) instanceof MojangsonPrimitive<?> primitive) {
                arrayList.add(primitive.getValue());
            }
            else {
                throw new IllegalStateException("無効な型を検出しました: " + value.get(i).getClass().getName());
            }
        }

        return arrayList;
    }

    @Override
    public @NotNull MojangsonList copy() {
        return MojangsonValueTypes.LIST.toMojangson(toList());
    }

    public boolean isSuperOf(@NotNull MojangsonList other) {
        if (other.length() == 0) return true;

        for (final MojangsonValue<?> conditionValue : other) {
            if (value.stream().anyMatch(targetValue -> {
                if (targetValue instanceof MojangsonCompound superVal && conditionValue instanceof MojangsonCompound subVal) {
                    return superVal.isSuperOf(subVal);
                }
                else if (targetValue instanceof MojangsonList superVal && conditionValue instanceof MojangsonList subVal) {
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

    public boolean isListOf(@NotNull MojangsonValueType<?> type) {
        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                return false;
            }
        }

        return true;
    }

    public <T extends MojangsonValue<?>> TypedMojangsonList<T> typed(@NotNull  MojangsonValueType<T> type) {
        final TypedMojangsonList<T> array = new TypedMojangsonList<>(type);

        for (int i = 0; i < length(); i++) {
            if (!getTypeAt(i).equals(type)) {
                throw new IllegalStateException("その型の値でない要素が見つかりました: " + getTypeAt(i));
            }

            final T element = get(i, type);
            array.add(element);
        }

        return array;
    }
}
