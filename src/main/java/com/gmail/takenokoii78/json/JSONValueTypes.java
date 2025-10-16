package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public final class JSONValueTypes {
    private JSONValueTypes() {}

    public static final JSONValueType<JSONBoolean> BOOLEAN = new JSONValueType<>(JSONBoolean.class) {
        @Override
        public JSONBoolean toJSON(@Nullable Object value) {
            if (value instanceof JSONBoolean v) return v;
            else if (value instanceof Boolean v) return JSONBoolean.valueOf(v);
            else throw new IllegalArgumentException("value is not a boolean value");
        }
    };

    public static final JSONValueType<JSONNumber> NUMBER = new JSONValueType<>(JSONNumber.class) {
        @Override
        public JSONNumber toJSON(@Nullable Object value) {
            if (value instanceof JSONNumber v) return v;
            else if (value instanceof Number v) return JSONNumber.valueOf(v);
            else throw new IllegalArgumentException("value is not a number value");
        }
    };

    public static final JSONValueType<JSONString> STRING = new JSONValueType<>(JSONString.class) {
        @Override
        public JSONString toJSON(@Nullable Object value) {
            return switch (value) {
                case JSONString v -> v;
                case String v -> JSONString.valueOf(v);
                case Character v -> JSONString.valueOf(String.valueOf(v));
                case null, default -> throw new IllegalArgumentException("value is not a string value");
            };
        }
    };

    public static final JSONValueType<JSONObject> OBJECT = new JSONValueType<>(JSONObject.class) {
        @Override
        public JSONObject toJSON(@Nullable Object value) {
            if (value instanceof JSONObject jsonObject) return jsonObject;

            if (value instanceof Map<?, ?> map) {
                final Map<String, JSONValue<?>> object = new HashMap<>();

                for (final Object key : map.keySet()) {
                    if (key instanceof String string) {
                        final Object val = map.get(string);
                        object.put(string, JSONValueType.get(val).toJSON(val));
                    }
                    else {
                        throw new IllegalArgumentException("A key of Map is not a string");
                    }
                }

                return new JSONObject(object);
            }
            else throw new IllegalArgumentException("value is not a json object value: " + value.getClass().getName());
        }
    };

    public static final JSONValueType<JSONArray> ARRAY = new JSONValueType<>(JSONArray.class) {
        @Override
        public JSONArray toJSON(@Nullable Object value) {
            switch (value) {
                case JSONArray jsonArray -> {
                    return jsonArray;
                }
                case TypedJSONArray<?> typedJsonArray -> {
                    return typedJsonArray.untyped();
                }
                case Iterable<?> iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case Object[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case boolean[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case byte[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case short[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case int[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case long[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case char[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case float[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case double[] iterable -> {
                    final List<JSONValue<?>> listOfJSONValue = new ArrayList<>();

                    for (final Object element : iterable) {
                        listOfJSONValue.add(JSONValueType.get(element).toJSON(element));
                    }

                    return new JSONArray(listOfJSONValue);
                }
                case null -> {
                    throw new IllegalArgumentException("value is not a json array value: null");
                }
                default -> throw new IllegalArgumentException("value is not a json array value: " + value.getClass().getName());
            }
        }
    };

    public static final JSONValueType<JSONNull> NULL = new JSONValueType<>(JSONNull.class) {
        @Override
        public JSONNull toJSON(@Nullable Object value) {
            if (value instanceof JSONNull jsonNull) return jsonNull;
            else if (value == null) return JSONNull.NULL;
            else throw new IllegalArgumentException("value is not a null value");
        }
    };
}
