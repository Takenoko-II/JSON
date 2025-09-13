package com.gmail.takenokoii78;

import com.gmail.takenokoii78.mojangson.*;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonIntArray;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final MojangsonCompound compound = MojangsonParser.compound("""
            {
                id: 'minecraft:carrot_on_a_stick',
                count: 1,
                components: {
                    'minecraft:custom_data': {
                        foo: bar,
                        ints: [I; 1, 2, 3, 4]
                    }
                }
            }
            """);

        final MojangsonPath path = MojangsonPath.of("components.minecraft:custom_data{foo:bar}.foo");

        System.out.println(compound.get(path, MojangsonValueTypes.STRING));

        final MojangsonIntArray intArray = compound.get(
            MojangsonPath.of("components.'minecraft:custom_data'.ints"),
            MojangsonValueTypes.INT_ARRAY
        );

        System.out.println(intArray.listView().typed(MojangsonValueTypes.INT));

        System.out.println(intArray);

        Test test = new Test(1, 3);
        MojangsonModel.MojangsonCompoundModel.write(MojangsonParser.compound("{p: 3, q: 0}"), test);

        System.out.println(test.p);
        System.out.println(test.q);
    }

    public static final class Test implements MojangsonModel.MojangsonCompoundModel {
        int p;

        int q;

        public Test(int p, int q) {
            this.p = p;
            this.q = q;
        }
    }
}
