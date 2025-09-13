package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonList;
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public abstract class MojangsonPathNode<S extends MojangsonStructure, T> {
    protected final T parameter;

    protected MojangsonPathNode<?, ?> child;

    protected MojangsonPathNode(@NotNull T parameter, @Nullable MojangsonPathNode<?, ?> child) {
        this.parameter = parameter;
        this.child = child;
    }

    public abstract @Nullable MojangsonValue<?> get(@NotNull S structure);

    public abstract <U> @Nullable U access(@NotNull S structure, @NotNull BiFunction<S, Object, U> function);

    public abstract @NotNull MojangsonPathNode<S, T> copy();

    public abstract @NotNull String toString();

    public static final class ObjectKeyNode extends MojangsonPathNode<MojangsonCompound, String> {
        ObjectKeyNode(@NotNull String name, @Nullable MojangsonPathNode<?, ?> child) {
            super(name, child);
        }

        @Override
        public @Nullable MojangsonValue<?> get(@NotNull MojangsonCompound structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeOf(parameter));
        }

        @Override
        public <U> @Nullable U access(@NotNull MojangsonCompound structure, @NotNull BiFunction<MojangsonCompound, Object, U> function) {
            return function.apply(structure, parameter);
        }

        @Override
        public @NotNull MojangsonPathNode<MojangsonCompound, String> copy() {
            return new ObjectKeyNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public @NotNull String toString() {
            return "key<" + parameter + ">";
        }
    }

    public static final class ArrayIndexNode extends MojangsonPathNode<MojangsonList, Integer> {
        ArrayIndexNode(@NotNull Integer index, @Nullable MojangsonPathNode<?, ?> child) {
            super(index, child);
        }

        @Override
        public @Nullable MojangsonValue<?> get(@NotNull MojangsonList structure) {
            if (!structure.has(parameter)) return null;
            else return structure.get(parameter, structure.getTypeAt(parameter));
        }

        @Override
        public <U> @Nullable U access(@NotNull MojangsonList structure, @NotNull BiFunction<MojangsonList, Object, U> function) {
            return function.apply(structure, parameter);
        }

        @Override
        public @NotNull MojangsonPathNode<MojangsonList, Integer> copy() {
            return new ArrayIndexNode(parameter, child == null ? null : child.copy());
        }

        @Override
        public @NotNull String toString() {
            return "index<" + parameter + ">";
        }
    }

    public static final class ObjectKeyCheckerNode extends MojangsonPathNode<MojangsonCompound, Pair<String, MojangsonCompound>> {
        ObjectKeyCheckerNode(@NotNull String name, @NotNull MojangsonCompound jsonObject, @Nullable MojangsonPathNode<?, ?> child) {
            super(new Pair<>(name, jsonObject), child);
        }

        @Override
        public @Nullable MojangsonCompound get(@NotNull MojangsonCompound structure) {
            if (!structure.has(parameter.a())) return null;
            else {
                final MojangsonCompound value = structure.get(parameter.a(), MojangsonValueTypes.COMPOUND);

                if (value instanceof MojangsonCompound target) {
                    final MojangsonCompound condition = parameter.b();
                    if (target.isSuperOf(condition)) {
                        return value;
                    }
                    else return null;
                }
                else return null;
            }
        }

        @Override
        public <U> @Nullable U access(@NotNull MojangsonCompound structure, @NotNull BiFunction<MojangsonCompound, Object, U> function) {
            if (!structure.has(parameter.a())) return null;
            else {
                final MojangsonCompound value = structure.get(parameter.a(), MojangsonValueTypes.COMPOUND);

                if (value instanceof MojangsonCompound target) {
                    final MojangsonCompound condition = parameter.b();
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
        public @NotNull MojangsonPathNode<MojangsonCompound, Pair<String, MojangsonCompound>> copy() {
            return new ObjectKeyCheckerNode(parameter.a(), parameter.b(), child == null ? null : child.copy());
        }

        @Override
        public @NotNull String toString() {
            return "key_checker<" + parameter.a() + ", " + parameter.b() + ">";
        }
    }

    public static final class ArrayIndexFinderNode extends MojangsonPathNode<MojangsonList, MojangsonCompound> {
        ArrayIndexFinderNode(@NotNull MojangsonCompound parameter, @Nullable MojangsonPathNode<?, ?> child) {
            super(parameter, child);
        }

        @Override
        public @Nullable MojangsonCompound get(@NotNull MojangsonList structure) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != MojangsonValueTypes.COMPOUND) {
                    continue;
                }

                final MojangsonCompound element = structure.get(i, MojangsonValueTypes.COMPOUND);

                if (element instanceof MojangsonCompound object) {
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
        public <U> @Nullable U access(@NotNull MojangsonList structure, @NotNull BiFunction<MojangsonList, Object, U> function) {
            for (int i = 0; i < structure.length(); i++) {
                if (structure.getTypeAt(i) != MojangsonValueTypes.LIST) {
                    continue;
                }

                final MojangsonCompound element = structure.get(i, MojangsonValueTypes.COMPOUND);

                if (element instanceof MojangsonCompound object) {
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
        public @NotNull MojangsonPathNode<MojangsonList, MojangsonCompound> copy() {
            return new ArrayIndexFinderNode(parameter, child == null ? null : child.copy());
        }

        @NotNull
        @Override
        public String toString() {
            return "index_finder<" + parameter + ">";
        }

        public static final class MojangsonArrayIndexNotFoundException extends Exception {
            private MojangsonArrayIndexNotFoundException(@NotNull String message) {
                super(message);
            }
        }
    }

    public record Pair<A, B>(A a, B b) {}
}
