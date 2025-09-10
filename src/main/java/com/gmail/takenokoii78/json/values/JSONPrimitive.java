package com.gmail.takenokoii78.json.values;

import com.gmail.takenokoii78.json.JSONValue;

public abstract class JSONPrimitive<T> extends JSONValue<T> {
    protected JSONPrimitive(T value) {
        super(value);
    }

    public T getValue() {
        return value;
    }
}
