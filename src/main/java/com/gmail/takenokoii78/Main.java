package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONPath;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final var j = JSONParser.object("""
            {
                "foo": {
                    "bar": ["a", "b", { "c":2 }]
                }
            }
            """);

        final JSONPath path = JSONPath.pathOf("foo{\"bar\":[\"g\"]}.bar");

        System.out.println(j);
        System.out.println(path.get(j));
    }
}
