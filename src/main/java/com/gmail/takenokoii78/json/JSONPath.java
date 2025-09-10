package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JSONPath {
    private final JSONPathNode<?, ?> root;

    JSONPath(@NotNull JSONPathNode<?, ?> root) {
        this.root = root;
    }

    private @Nullable JSONValue<?> value(@NotNull JSONPathNode<?, ?> node, @Nullable JSONValue<?> p) {
        switch (node) {
            case JSONPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.get(object);
            }
            case JSONPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexNode.get(array);
            }
            case JSONPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.get(object);
            }
            case JSONPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.get(array);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    public @Nullable JSONValue<?> get(@NotNull JSONObject jsonObject) {
        JSONPathNode<?, ?> node = root;
        JSONValue<?> p = jsonObject;

        while (node.child != null) {
            p = value(node, p);

            if (p == null) {
                throw new IllegalArgumentException(node.parameter + " p == null");
            }

            node = node.child;
        }

        return value(node, p);
    }

    private void set(@NotNull JSONObject jsonObject, @NotNull JSONValue<?> jsonValue) {

    }

    public static @NotNull JSONPath pathOf(@NotNull String path) {
        return JSONPathParser.parse(path);
    }
}
