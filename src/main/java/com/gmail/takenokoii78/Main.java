package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.*;
import com.gmail.takenokoii78.json.values.JSONObject;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final var object = JSONParser.object("""
            {
                "foo": {
                    "bar": ["a", "b", { "c": 2 }]
                }
            }
            """);

        final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 2 }]}.bar[-1].d");

        System.out.println(object);

        path.access(object, reference -> {
            reference.set(10);
            return null;
        });

        System.out.println(object);

        // MojangsonParser似のやつ -> 最後にやる
    }
}
