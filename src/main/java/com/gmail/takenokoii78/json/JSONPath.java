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

    private <U> @Nullable U onLastNode(@NotNull JSONObject jsonObject, @NotNull TriFunction<JSONStructure, Object, Runnable, U> function) {
        JSONPathNode<?, ?> node = root;
        JSONValue<?> p = jsonObject;

        final List<Runnable> list = new ArrayList<>();

        while (node.child != null) {
            var q = getNextValue(node, p);

            if (q == null) {
                if (node instanceof JSONPathNode.ObjectKeyNode n) {
                    q = new JSONObject();
                    JSONObject t = (JSONObject) p;
                    JSONObject s = (JSONObject) q;
                    list.add(() -> t.set(n.parameter, s));
                }
                else {
                    throw new IllegalArgumentException(node.parameter + " p == null");
                }
            }

            p = q;
            node = node.child;
        }

        return useNextValue(node, p, (a, b) -> {
            final boolean[] created = {false};
            return function.apply(a, b, () -> {
                if (!created[0]) {
                    list.forEach(Runnable::run);
                    created[0] = true;
                }
            });
        });
    }

    public <T> T access(@NotNull JSONObject jsonObject, @NotNull Function<JSONPathReference<?, ?>, T> function) {
        return onLastNode(jsonObject, (lastStructure, nodeParameter, creator) -> {
            final JSONPathReference<?, ?> reference = switch (lastStructure) {
                case JSONObject object -> new JSONPathReference.JSONObjectPathReference(object, (String) nodeParameter, creator);
                case JSONArray array -> new JSONPathReference.JSONArrayPathReference(array, (Integer) nodeParameter, creator);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        });
    }

    public @NotNull JSONPathReference<?, ?> refer(@NotNull JSONObject jsonObject) {
        return access(jsonObject, reference -> reference);
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

        protected final Runnable creator;

        protected JSONPathReference(@NotNull S structure, @NotNull T parameter, @NotNull Runnable creator) {
            this.structure = structure;
            this.parameter = parameter;
            this.creator = creator;
        }

        public abstract boolean has();

        public abstract @NotNull JSONValueType<?> getType();

        public abstract <U extends JSONValue<?>> @NotNull U get(@NotNull JSONValueType<U> type);

        public abstract void set(@NotNull Object value);

        public abstract void delete();

        private static final class JSONObjectPathReference extends JSONPathReference<JSONObject, String> {
            private JSONObjectPathReference(@NotNull JSONObject structure, @NotNull String parameter, @NotNull Runnable creator) {
                super(structure, parameter, creator);
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
                creator.run();
                structure.set(parameter, value);
            }

            @Override
            public void delete() {
                structure.delete(parameter);
            }
        }

        private static final class JSONArrayPathReference extends JSONPathReference<JSONArray, Integer> {
            private JSONArrayPathReference(@NotNull JSONArray structure, @NotNull Integer parameter, @NotNull Runnable creator) {
                super(structure, parameter, creator);
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
                creator.run();
                structure.set(parameter, value);
            }

            @Override
            public void delete() {
                structure.delete(parameter);
            }
        }
    }

    @FunctionalInterface
    private interface TriFunction<S, T, U, R> {
        R apply(S s, T t, U u);
    }
}
