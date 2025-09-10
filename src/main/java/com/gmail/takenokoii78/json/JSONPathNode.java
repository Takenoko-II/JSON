package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.generic.Pair;
import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class JSONPathNode<S extends JSONStructure, T> {
    protected final T parameter;

    protected final JSONPathNode<?, ?> child;

    protected JSONPathNode(@NotNull T parameter, @Nullable JSONPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    public abstract @Nullable JSONValue<?> get(@NotNull S structure);

    public static final class ObjectKeyNode extends JSONPathNode<JSONObject, String> {
        public ObjectKeyNode(@NotNull String name, @Nullable JSONPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public @Nullable JSONValue<?> get(@NotNull JSONObject structure) {
            if (!structure.hasKey(parameter)) return null;
            else return structure.getKey(parameter, structure.getTypeOfKey(parameter));
        }
    }

    public static final class ArrayIndexNode extends JSONPathNode<JSONArray, Integer> {
        public ArrayIndexNode(@NotNull Integer index, @Nullable JSONPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public @Nullable JSONValue<?> get(@NotNull JSONArray structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeAt(parameter));
        }
    }

    public static final class ObjectKeyCheckerNode extends JSONPathNode<JSONObject, Pair<String, JSONObject>> {
        public ObjectKeyCheckerNode(@NotNull String name, @NotNull JSONObject jsonObject, @Nullable JSONPathNode<?, ?> child) {
            super(new Pair<>(name, jsonObject), child);
        }

        @Override
        public @Nullable JSONObject get(@NotNull JSONObject structure) {
            if (!structure.hasKey(parameter.a())) return null;
            else {
                final JSONObject value = structure.getKey(parameter.a(), JSONValueTypes.OBJECT);

                if (value instanceof JSONObject target) {
                    final JSONObject condition = parameter.b();
                    if (target.isSuperOf(condition)) {
                        return value;
                    }
                    else return null;
                }
                else return null;
            }
        }
    }

    public static final class ArrayIndexFinderNode extends JSONPathNode<JSONArray, JSONObject> {
        public ArrayIndexFinderNode(@NotNull JSONObject parameter, @Nullable JSONPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public @Nullable JSONObject get(@NotNull JSONArray structure) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != JSONValueTypes.OBJECT) {
                    continue;
                }

                final JSONObject element = structure.get(i, JSONValueTypes.OBJECT);

                if (element instanceof JSONObject object) {
                    if (object.isSuperOf(parameter)) {
                        return element;
                    }
                    else return null;
                }
                else return null;
            }

            return null;
        }
    }
}
