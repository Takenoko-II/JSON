package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class JSONPath {
    private final JSONPathNode<?, ?> root;

    JSONPath(@NotNull JSONPathNode<?, ?> root) {
        this.root = root;
    }

    private @Nullable JSONValue<?> getNextValue(@NotNull JSONPathNode<?, ?> node, @Nullable JSONValue<?> p) {
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

    private <U> @Nullable U useNextValue(@NotNull JSONPathNode<?, ?> node, @Nullable JSONValue<?> p, BiFunction<JSONStructure, Object, U> function) {
        switch (node) {
            case JSONPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.access(object, function::apply);
            }
            case JSONPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexNode.access(array, function::apply);
            }
            case JSONPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.access(object, function::apply);
            }
            case JSONPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.access(array, function::apply);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U onLastNode(@NotNull JSONObject jsonObject, @NotNull BiFunction<JSONStructure, Object, U> function, boolean isForcedAccess) throws JSONInaccessiblePathException {
        JSONPathNode<?, ?> node = root;
        JSONValue<?> p = jsonObject;

        while (node.child != null) {
            var q = getNextValue(node, p);

            if (q == null) {
                if (node instanceof JSONPathNode.ObjectKeyNode n && isForcedAccess) {
                    q = new JSONObject();
                    ((JSONObject) p).set(n.parameter, q);
                }
                else {
                    throw new JSONInaccessiblePathException(node.parameter);
                }
            }

            p = q;
            node = node.child;
        }

        return useNextValue(node, p, function);
    }

    public <T> T access(@NotNull JSONObject jsonObject, @NotNull Function<JSONPathReference<?, ?>, T> function, boolean isForcedAccess) throws JSONInaccessiblePathException {
        return onLastNode(jsonObject, (lastStructure, nodeParameter) -> {
            final JSONPathReference<?, ?> reference = switch (lastStructure) {
                case JSONObject object -> new JSONPathReference.JSONObjectPathReference(object, (String) nodeParameter);
                case JSONArray array -> new JSONPathReference.JSONArrayPathReference(array, (Integer) nodeParameter);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        }, isForcedAccess);
    }

    public int length() {
        JSONPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.child;
        }

        return i;
    }

    public @NotNull JSONPath slice(int begin, int end) {
        if (begin < 0 || end > length() || begin > end) {
            throw new IllegalArgumentException("インデックスが範囲外です");
        }

        JSONPathNode<?, ?> beginNode = root;
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.child;
        }

        JSONPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            node = node.child;
        }

        node.child = null;

        return new JSONPath(beginNode);
    }

    public @NotNull JSONPath parent() {
        return slice(0, length() - 2);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("JSONPath { ");
        JSONPathNode<?, ?> node = root;

        while (node != null) {
            sb.append(node);
            node = node.child;

            if (node != null) {
                sb.append(".");
            }
        }

        return sb.append(" }").toString();
    }

    public static @NotNull JSONPath of(@NotNull String path) {
        return JSONPathParser.parse(path);
    }

    public static abstract class JSONPathReference<S extends JSONStructure, T> {
        protected final S structure;

        protected final T parameter;

        protected JSONPathReference(@NotNull S structure, @NotNull T parameter) {
            this.structure = structure;
            this.parameter = parameter;
        }

        public abstract boolean has();

        public abstract @NotNull JSONValueType<?> getType();

        public abstract <U extends JSONValue<?>> @NotNull U get(@NotNull JSONValueType<U> type);

        public abstract void set(@NotNull Object value);

        public abstract boolean delete();

        private static final class JSONObjectPathReference extends JSONPathReference<JSONObject, String> {
            private JSONObjectPathReference(@NotNull JSONObject structure, @NotNull String parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @NotNull
            @Override
            public JSONValueType<?> getType() {
                return structure.getTypeOf(parameter);
            }

            @Override
            public <U extends JSONValue<?>> @NotNull U get(@NotNull JSONValueType<U> type) {
                return structure.get(parameter, type);
            }

            @Override
            public void set(@NotNull Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }

        private static final class JSONArrayPathReference extends JSONPathReference<JSONArray, Integer> {
            private JSONArrayPathReference(@NotNull JSONArray structure, @NotNull Integer parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @NotNull
            @Override
            public JSONValueType<?> getType() {
                return structure.getTypeAt(parameter);
            }

            @Override
            public <U extends JSONValue<?>> @NotNull U get(@NotNull JSONValueType<U> type) {
                return structure.get(parameter, type);
            }

            @Override
            public void set(@NotNull Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }
    }

    public static final class JSONInaccessiblePathException extends Exception {
        public JSONInaccessiblePathException(@NotNull Object nodeParameter) {
            super("パスに対応する値へのアクセスに失敗しました: 条件 " + nodeParameter + " を満たすキーは存在しません");
        }
    }
}
