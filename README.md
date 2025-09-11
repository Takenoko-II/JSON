# JSON

自分用
<br>途中

```java
void main() {
    final var object = JSONParser.object("""
        {
            "foo": {
                "bar": ["a", "b", { "c": 1 }]
            }
        }
        """);

    final JSONPath path = JSONPath.of("foo{\"bar\":[{ \"c\": 1 }]}.bar[-1].c");

    System.out.println("1. " + object);

    path.access(object, access -> {
        access.set(5);
        return null;
    });

    System.out.println("2. " + object);
}
```

```
> 1. {foo={bar=[a, b, {c=1}]}}
> 2. {foo={bar=[a, b, {c=5}]}}
```
