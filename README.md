# JSON

JSONにMCJEのNbtPathみたいな感じでアクセスしたかっただけ
<br>Mojangsonもあるよ

```java
import com.gmail.takenokoii78.json.JSONParser;
import com.gmail.takenokoii78.json.JSONPath;
import com.gmail.takenokoii78.json.values.JSONObject;

public final class Main {
    public static void main(String[] args) {
        final JSONObject object = JSONParser.object("""
            {
                "foo": {
                    "bar": [
                        "a",
                        "b",
                        { "c": 1 }
                    ]
                }
            }
            """);

        final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 1 }]}.bar[-1].d");

        System.out.println(object);

        path.access(object, reference -> {
            reference.set(2);
            return null;
        });

        System.out.println(object);
    }
}
```

```
> {foo={bar=[a, b, {c=1}]}}
> {foo={bar=[a, b, {c=1, d=2}]}}
```
