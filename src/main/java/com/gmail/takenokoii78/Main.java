package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.values.JSONNull;

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

        final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 2 }]}.bar[2].c");

        System.out.println(object);
        System.out.println();
        final var v = path.access(object, access -> {
            access.set(10);
            return access.get(access.getType());
        });

        System.out.println(v);
        System.out.println(object);

        // 負の添え字 -> JSONArray側の問題
        // MojangsonParser似のやつ -> 最後にやる
    }
}
