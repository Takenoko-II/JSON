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

    private <T, U> @Nullable U accessNextValue(@NotNull JSONPathNode<?, ?> node, @Nullable JSONValue<?> p, @NotNull Class<T> clazz, BiFunction<T, Object, U> function) {
        switch (node) {
            case JSONPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.access(object, (a, b) -> function.apply(clazz.cast(a), b));
            }
            case JSONPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexNode.access(array, (a, b) -> function.apply(clazz.cast(a), b));
            }
            case JSONPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof JSONObject object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.access(object, (a, b) -> function.apply(clazz.cast(a), b));
            }
            case JSONPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof JSONArray array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.access(array, (a, b) -> function.apply(clazz.cast(a), b));
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <T, U> @Nullable U accessStructParam(@NotNull JSONObject jsonObject, @NotNull Class<T> clazz, BiFunction<T, Object, U> function) {
        JSONPathNode<?, ?> node = root;
        JSONValue<?> p = jsonObject;

        while (node.child != null) {
            p = getNextValue(node, p);

            if (p == null) {
                throw new IllegalArgumentException(node.parameter + " p == null");
            }

            node = node.child;
        }

        return accessNextValue(node, p, clazz, function);
    }

    public <T> T access(@NotNull JSONObject jsonObject, @NotNull Function<JSONPathAccess<?, ?>, T> function) {
        return accessStructParam(jsonObject, JSONStructure.class, (a, b) -> {
            final JSONPathAccess<?, ?> access = switch (a) {
                case JSONObject object -> new JSONPathAccess.JSONObjectPathAccess(object, (String) b);
                case JSONArray array -> new JSONPathAccess.JSONArrayPathAccess(array, (Integer) b);
                default -> throw new IllegalArgumentException("TODO");
            };

            return function.apply(access);
        });
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
            throw new IllegalArgumentException("TODO");
        }

        JSONPathNode<?, ?> beginNode = root;
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("TODO");
            }

            beginNode = beginNode.child;
        }

        JSONPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("TODO");
            }

            node = node.child;
        }

        node.child = null;

        return new JSONPath(beginNode);
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

    public static abstract class JSONPathAccess<S extends JSONStructure, T> {
        protected final S structure;

        protected final T parameter;

        protected JSONPathAccess(@NotNull S structure, @NotNull T parameter) {
            this.structure = structure;
            this.parameter = parameter;
        }

        public abstract boolean has();

        public abstract @NotNull JSONValueType<?> getType();

        public abstract <U extends JSONValue<?>> @NotNull U get(@NotNull JSONValueType<U> type);

        public abstract void set(@NotNull Object value);

        public abstract void delete();

        private static final class JSONObjectPathAccess extends JSONPathAccess<JSONObject, String> {
            private JSONObjectPathAccess(@NotNull JSONObject structure, @NotNull String parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.hasKey(parameter);
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
            public void delete() {
                structure.delete(parameter);
            }
        }

        private static final class JSONArrayPathAccess extends JSONPathAccess<JSONArray, Integer> {
            private JSONArrayPathAccess(@NotNull JSONArray structure, @NotNull Integer parameter) {
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
            public void delete() {
                structure.delete(parameter);
            }
        }
    }
}
