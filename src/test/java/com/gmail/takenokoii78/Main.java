package com.gmail.takenokoii78;

import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonSerializer;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final MojangsonCompound compound = MojangsonParser.compound("""
            {
                foo: {
                    bar: {
                        baz: [
                            {
                                x: 4
                            }
                        ]
                    }
                }
            }
            """);

        compound.set("b", false);

        System.out.println(MojangsonSerializer.serialize(compound));
    }
}
