package com.gmail.takenokoii78;

import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.mojangson.*;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final MojangsonCompound compound = MojangsonParser.compound("""
            {
                id: 'minecraft:carrot_on_a_stick',
                count: 1,
                components: {
                    'minecraft:custom_data': {
                        foo: bar
                    }
                }
            }
            """);

        final MojangsonPath path = MojangsonPath.of("components.minecraft:custom_data{foo:bar}.foo");

        System.out.println(compound.get(path, MojangsonValueTypes.STRING));
        System.out.println(compound);

        System.out.println(path.parent());

        // TODO: TypedMojangsonList のみ
    }
}
