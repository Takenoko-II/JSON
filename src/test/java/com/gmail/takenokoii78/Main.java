package com.gmail.takenokoii78;

import com.gmail.takenokoii78.mojangson.*;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        final MojangsonFile file = new MojangsonFile("src/test/resources/test.json");
        final MojangsonCompound compound = file.readAsCompound();
        System.out.println(compound);
        compound.getReference(MojangsonPath.of("foo{bar:[{c: 0}]}.bar[-1].d")).set(10);
        System.out.println(compound);

        // TODO: TypedMojangsonList, ちょっと欲しい
    }
}
