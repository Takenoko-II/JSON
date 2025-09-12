package com.gmail.takenokoii78.json;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final var object = new JSONFile("src/test/resources/test.json").readAsObject();

        final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 0 }]}.bar[-1].d");

        System.out.println(object);

        path.access(object, reference -> {
            reference.set(10);
            return null;
        });

        System.out.println(object);

        // MojangsonParser似のやつ -> 最後にやる
    }
}
