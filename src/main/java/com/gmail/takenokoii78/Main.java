package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.*;
import com.gmail.takenokoii78.json.values.JSONObject;

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
        final var v = path.access(object, reference -> {
            reference.set(10);
            return reference.get(reference.getType());
        });

        final JSONPath.JSONPathReference<?, ?> reference = path.access(object, r -> r);
        reference.get(JSONValueTypes.NUMBER);

        System.out.println(object);

        // MojangsonParser似のやつ -> 最後にやる
        // TODO: Way Creation Option - 該当のパスが存在しなければ自動生成しつつ参照を作成するオプション
    }
}
