package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

@NullMarked
public final class JSONPath {
    private final JSONPathNode<?, ?> root;

    JSONPath(JSONPathNode<?, ?> root) {
        this.root = root;
    }

    private @Nullable JSONValue<?> getNextValue(JSONPathNode<?, ?> node, @Nullable JSONValue<?> p) {
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

    private <U> @Nullable U useNextValue(JSONPathNode<?, ?> node, @Nullable JSONValue<?> p, BiFunction<JSONStructure, Object, U> function) {
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

    private <U> @Nullable U onLastNode(JSONObject jsonObject, BiFunction<JSONStructure, Object, U> function, boolean isForcedAccess) throws JSONInaccessiblePathException {
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

    public <T> T access(JSONObject jsonObject, Function<JSONPathReference<?, ?>, T> function, boolean isForcedAccess) throws JSONInaccessiblePathException {
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

    public JSONPath slice(int begin, int end) {
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

    public JSONPath parent() {
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

    @Override
    public int hashCode() {
        return Objects.hash(toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        else if (obj == this) return true;
        else if (obj.getClass() != getClass()) return false;
        else return toString().equals(obj.toString());
    }

    public static JSONPath of(String path) throws JSONParseException {
        return JSONPathParser.parse(path);
    }

    public static abstract class JSONPathReference<S extends JSONStructure, T> {
        protected final S structure;

        protected final T parameter;

        protected JSONPathReference(S structure, T parameter) {
            this.structure = structure;
            this.parameter = parameter;
        }

        public abstract boolean has();

        public abstract JSONValueType<?> getType();

        public abstract <U extends JSONValue<?>> U get(JSONValueType<U> type);

        public abstract void set(Object value);

        public abstract boolean delete();

        private static final class JSONObjectPathReference extends JSONPathReference<JSONObject, String> {
            private JSONObjectPathReference(JSONObject structure, String parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public JSONValueType<?> getType() {
                return structure.getTypeOf(parameter);
            }

            @Override
            public <U extends JSONValue<?>> U get(JSONValueType<U> type) {
                return structure.get(parameter, type);
            }

            @Override
            public void set(Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }

        private static final class JSONArrayPathReference extends JSONPathReference<JSONArray, Integer> {
            private JSONArrayPathReference(JSONArray structure, Integer parameter) {
                super(structure, parameter);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @Override
            public JSONValueType<?> getType() {
                return structure.getTypeAt(parameter);
            }

            @Override
            public <U extends JSONValue<?>> U get(JSONValueType<U> type) {
                return structure.get(parameter, type);
            }

            @Override
            public void set(Object value) {
                structure.set(parameter, value);
            }

            @Override
            public boolean delete() {
                return structure.delete(parameter);
            }
        }
    }

    public static final class JSONInaccessiblePathException extends Exception {
        public JSONInaccessiblePathException(Object nodeParameter) {
            super("パスに対応する値へのアクセスに失敗しました: 条件 " + nodeParameter + " を満たすキーは存在しません");
        }
    }
}
