package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.JSONSerializer;
import com.gmail.takenokoii78.json.JSONValueTypes;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONString;
import com.gmail.takenokoii78.json.values.TypedJSONArray;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final JSONObject object = new JSONObject();
        object.set("replace", false);
        object.set("values", new TypedJSONArray<>(JSONValueTypes.STRING, List.of(
            JSONString.valueOf("foo:bar"),
            JSONString.valueOf("baz:piyo")
        )));

        System.out.println(JSONSerializer.serialize(object));
    }
}
