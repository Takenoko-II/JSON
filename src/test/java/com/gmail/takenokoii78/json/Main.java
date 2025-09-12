package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
import com.gmail.takenokoii78.mojangson.values.MojangsonIntArray;

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

        var o = MojangsonParser.compound("{foo:bar,a:[{b:c,d:e}],arr:[I;0,1,2,3]}");
        var p = MojangsonPath.of("arr[-1]");

        p.access(o, r -> {
            r.set(10);
            return null;
        });

        final MojangsonIntArray intArray = MojangsonPath.of("arr").access(o, r -> r).get(MojangsonValueTypes.INT_ARRAY);
        intArray.toMojangsonList().set(2, 6);
        System.out.println(intArray.toMojangsonList());

        System.out.println(o);

        // TODO: JSONObject -> MojangsonCompound, JSONArray -> MojangsonList
        // TODO: MojangsonCompound -> JSONObject, MojangsonList -> JSONArray
        // JSONNumber -> MojangsonInt or MojangsonDouble
        // MojangsonInt or MojangsonDouble -> JSONNumber
    }
}
