# JSON

JSONにMCJEのNbtPathみたいな感じでアクセスしたかっただけ
<br>Mojangsonもあるよ

```java
import com.gmail.takenokoii78.mojangson.MojangsonParser;
import com.gmail.takenokoii78.mojangson.MojangsonPath;
import com.gmail.takenokoii78.mojangson.MojangsonValueTypes;
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
    }
}
```

```
> bar
```
