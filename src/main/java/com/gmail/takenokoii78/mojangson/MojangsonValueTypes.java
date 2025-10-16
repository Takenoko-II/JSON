package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.*;

@NullMarked
public final class MojangsonValueTypes {
    private MojangsonValueTypes() {}

    public static final MojangsonValueType<MojangsonByte> BYTE = new MojangsonValueType<>(MojangsonByte.class) {
        @Override
        public MojangsonByte toMojangson(@Nullable Object value) {
            return switch (value) {
                case MojangsonByte mojangsonByte -> mojangsonByte;
                case Byte byteValue -> MojangsonByte.valueOf(byteValue);
                case Boolean booleanValue -> MojangsonByte.valueOf(booleanValue ? (byte) 1 : (byte) 0);
                case null, default -> throw new IllegalArgumentException("byte型でない値はMojangsonByteに変換できません");
            };
        }
    };

    public static final MojangsonValueType<MojangsonShort> SHORT = new MojangsonValueType<>(MojangsonShort.class) {
        @Override
        public MojangsonShort toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonShort mojangsonShort) return mojangsonShort;
            else if (value instanceof Short shortValue) return MojangsonShort.valueOf(shortValue);
            else throw new IllegalArgumentException("short型でない値はMojangsonShortに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonInt> INT = new MojangsonValueType<>(MojangsonInt.class) {
        @Override
        public MojangsonInt toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonInt mojangsonInt) return mojangsonInt;
            else if (value instanceof Integer intValue) return MojangsonInt.valueOf(intValue);
            else throw new IllegalArgumentException("int型でない値はMojangsonIntに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonLong> LONG = new MojangsonValueType<>(MojangsonLong.class) {
        @Override
        public MojangsonLong toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonLong mojangsonLong) return mojangsonLong;
            else if (value instanceof Long longValue) return MojangsonLong.valueOf(longValue);
            else throw new IllegalArgumentException("long型でない値はMojangsonLongに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonFloat> FLOAT = new MojangsonValueType<>(MojangsonFloat.class) {
        @Override
        public MojangsonFloat toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonFloat mojangsonFloat) return mojangsonFloat;
            else if (value instanceof Float floatValue) return MojangsonFloat.valueOf(floatValue);
            else throw new IllegalArgumentException("float型でない値はMojangsonFloatに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonDouble> DOUBLE = new MojangsonValueType<>(MojangsonDouble.class) {
        @Override
        public MojangsonDouble toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonDouble mojangsonDouble) return mojangsonDouble;
            else if (value instanceof Double doubleValue) return MojangsonDouble.valueOf(doubleValue);
            else throw new IllegalArgumentException("double型でない値はMojangsonDoubleに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonString> STRING = new MojangsonValueType<>(MojangsonString.class) {
        @Override
        public MojangsonString toMojangson(@Nullable Object value) {
            return switch (value) {
                case MojangsonString mojangsonString -> mojangsonString;
                case String stringValue -> MojangsonString.valueOf(stringValue);
                case Character characterValue -> MojangsonString.valueOf(characterValue);
                case null, default ->
                    throw new IllegalArgumentException("String型でない値はMojangsonStringに変換できません");
            };
        }
    };

    public static final MojangsonValueType<MojangsonByteArray> BYTE_ARRAY = new MojangsonValueType<>(MojangsonByteArray.class) {
        @Override
        public MojangsonByteArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonByteArray mojangsonByteArray) return mojangsonByteArray;
            else if (value instanceof byte[] bytes) return new MojangsonByteArray(bytes);
            else throw new IllegalArgumentException("byte[]型でない値はMojangsonByteArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonIntArray> INT_ARRAY = new MojangsonValueType<>(MojangsonIntArray.class) {
        @Override
        public MojangsonIntArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonIntArray mojangsonIntArray) return mojangsonIntArray;
            else if (value instanceof int[] ints) return new MojangsonIntArray(ints);
            else throw new IllegalArgumentException("int[]型でない値はMojangsonIntArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonLongArray> LONG_ARRAY = new MojangsonValueType<>(MojangsonLongArray.class) {
        @Override
        public MojangsonLongArray toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonLongArray mojangsonLongArray) return mojangsonLongArray;
            else if (value instanceof long[] longs) return new MojangsonLongArray(longs);
            else throw new IllegalArgumentException("long[]型でない値はMojangsonLongArrayに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonCompound> COMPOUND = new MojangsonValueType<>(MojangsonCompound.class) {
        @Override
        public MojangsonCompound toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonCompound mojangsonCompound) return mojangsonCompound;
            else if (value instanceof Map<?,?> map) {
                final Map<String, MojangsonValue<?>> compound = new HashMap<>();

                for (final Object k : map.keySet()) {
                    if (k instanceof String strKey) {
                        final Object val = map.get(strKey);
                        compound.put(strKey, get(val).toMojangson(val));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not a string");
                    }
                }

                return new MojangsonCompound(compound);
            }
            else throw new IllegalArgumentException("Map<String, ?>型でない値はMojangsonCompoundに変換できません");
        }
    };

    public static final MojangsonValueType<MojangsonList> LIST = new MojangsonValueType<>(MojangsonList.class) {
        @Override
        public MojangsonList toMojangson(@Nullable Object value) {
            switch (value) {
                case MojangsonList mojangsonList -> {
                    return mojangsonList;
                }
                case TypedMojangsonList<?> typedMojangsonList -> {
                    return typedMojangsonList.untyped();
                }
                case Collection<?> iterable -> {
                    final List<MojangsonValue<?>> listOfMojangson = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfMojangson.add(get(element).toMojangson(element));
                    }

                    return new MojangsonList(listOfMojangson);
                }
                case null, default ->
                    throw new IllegalArgumentException("List<?>型でない値はMojangsonListに変換できません");
            }
        }
    };

    public static final MojangsonValueType<MojangsonNull> NULL = new MojangsonValueType<>(MojangsonNull.class) {
        @Override
        public MojangsonNull toMojangson(@Nullable Object value) {
            if (value instanceof MojangsonNull mojangsonNull) return mojangsonNull;
            else if (value == null) return MojangsonNull.NULL;
            else throw new IllegalArgumentException("nullでない値はMojangsonNullに変換できません");
        }
    };
}
