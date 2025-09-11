package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.*;
import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONIterable;
import com.gmail.takenokoii78.json.values.JSONString;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final var object = JSONParser.object("""
            {
                "foo": {
                    "bar": ["a", "b", { "c":2 }]
                }
            }
            """);

        final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 2 }]}.bar[-1].c");

        System.out.println(object);
        System.out.println();
        final var v = path.access(object, access -> {
            access.set(10);
            return access.get(access.getType());
        });

        System.out.println(v);
        System.out.println(object);

        JSONIterable<JSONString> ss = new JSONArray().typed(JSONValueTypes.STRING);

        // MojangsonParser似のやつ -> 最後にやる
    }
}
