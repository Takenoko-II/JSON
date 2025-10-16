package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

@NullMarked
public abstract class JSONPathNode<S extends JSONStructure, T> {
    protected final T parameter;

    @Nullable
    protected JSONPathNode<?, ?> child;

    protected JSONPathNode(T parameter, @Nullable JSONPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    public abstract @Nullable JSONValue<?> get(S structure);

    public abstract <U> @Nullable U access(S structure, BiFunction<S, Object, U> function);

    public abstract JSONPathNode<S, T> copy();

    public abstract String toString();

    public static final class ObjectKeyNode extends JSONPathNode<JSONObject, String> {
        ObjectKeyNode(String name, @Nullable JSONPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public @Nullable JSONValue<?> get(JSONObject structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeOf(parameter));
        }

        @Override
        public <U> U access(JSONObject structure, BiFunction<JSONObject, Object, U> function) {
            return function.apply(structure, parameter);
        }

        @Override
        public JSONPathNode<JSONObject, String> copy() {
            return new ObjectKeyNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key<" + parameter + ">";
        }
    }

    public static final class ArrayIndexNode extends JSONPathNode<JSONArray, Integer> {
        ArrayIndexNode(Integer index, @Nullable JSONPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public @Nullable JSONValue<?> get(JSONArray structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeAt(parameter));
        }

        @Override
        public <U> @Nullable U access(JSONArray structure, BiFunction<JSONArray, Object, U> function) {
            return function.apply(structure, parameter);
        }

        @Override
        public JSONPathNode<JSONArray, Integer> copy() {
            return new ArrayIndexNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index<" + parameter + ">";
        }
    }

    public static final class ObjectKeyCheckerNode extends JSONPathNode<JSONObject, Pair<String, JSONObject>> {
        ObjectKeyCheckerNode(String name, JSONObject jsonObject, @Nullable JSONPathNode<?, ?> child) {
            super(new Pair<>(name, jsonObject), child);
        }

        @Override
        public @Nullable JSONObject get(JSONObject structure) {
            if (!structure.has(parameter.a())) return null;
            else {
                final JSONObject value = structure.get(parameter.a(), JSONValueTypes.OBJECT);

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

        @Override
        public <U> @Nullable U access(JSONObject structure, BiFunction<JSONObject, Object, U> function) {
            if (!structure.has(parameter.a())) return null;
            else {
                final JSONObject value = structure.get(parameter.a(), JSONValueTypes.OBJECT);

                if (value instanceof JSONObject target) {
                    final JSONObject condition = parameter.b();
                    if (target.isSuperOf(condition)) {
                        // return value;
                        return function.apply(structure, parameter.a());
                    }
                    else return null;
                }
                else return null;
            }
        }

        @Override
        public JSONPathNode<JSONObject, Pair<String, JSONObject>> copy() {
            return new ObjectKeyCheckerNode(parameter.a(), parameter.b(), child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "key_checker<" + parameter.a() + ", " + parameter.b() + ">";
        }
    }

    public static final class ArrayIndexFinderNode extends JSONPathNode<JSONArray, JSONObject> {
        ArrayIndexFinderNode(JSONObject parameter, @Nullable JSONPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public @Nullable JSONObject get(JSONArray structure) {
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

        @Override
        public <U> @Nullable U access(JSONArray structure, BiFunction<JSONArray, Object, U> function) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != JSONValueTypes.OBJECT) {
                    continue;
                }

                final JSONObject element = structure.get(i, JSONValueTypes.OBJECT);

                if (element instanceof JSONObject object) {
                    if (object.isSuperOf(parameter)) {
                        // return element;
                        return function.apply(structure, i);
                    }
                    else return null;
                }
                else return null;
            }

            return null;
        }

        @Override
        public JSONPathNode<JSONArray, JSONObject> copy() {
            return new ArrayIndexFinderNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public String toString() {
            return "index_finder<" + parameter + ">";
        }
    }

    public record Pair<A, B>(A a, B b) {}
}
