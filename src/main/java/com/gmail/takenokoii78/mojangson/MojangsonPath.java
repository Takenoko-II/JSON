package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.MojangsonArray;
import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonList;
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MojangsonPath {
    private final MojangsonPathNode<?, ?> root;

    MojangsonPath(@NotNull MojangsonPathNode<?, ?> root) {
        this.root = root;
    }

    private @Nullable MojangsonValue<?> getNextValue(@NotNull MojangsonPathNode<?, ?> node, @Nullable MojangsonValue<?> p) {
        switch (node) {
            case MojangsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.get(object);
            }
            case MojangsonPathNode.ArrayIndexNode arrayIndexNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexNode.get(array);
            }
            case MojangsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.get(object);
            }
            case MojangsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.get(array);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U useNextValue(@NotNull MojangsonPathNode<?, ?> node, @Nullable MojangsonValue<?> p, BiFunction<MojangsonStructure, Object, U> function) {
        switch (node) {
            case MojangsonPathNode.ObjectKeyNode objectKeyNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException(String.valueOf(p));
                }
                return objectKeyNode.access(object, function::apply);
            }
            case MojangsonPathNode.ArrayIndexNode arrayIndexNode -> {
                return switch (p) {
                    case MojangsonList list -> arrayIndexNode.access(list, function::apply);
                    case MojangsonArray<?, ?> array -> arrayIndexNode.access(array.listView(), function::apply);
                    default -> throw new IllegalArgumentException();
                };
            }
            case MojangsonPathNode.ObjectKeyCheckerNode objectKeyCheckerNode -> {
                if (!(p instanceof MojangsonCompound object)) {
                    throw new IllegalArgumentException();
                }
                return objectKeyCheckerNode.access(object, function::apply);
            }
            case MojangsonPathNode.ArrayIndexFinderNode arrayIndexFinderNode -> {
                if (!(p instanceof MojangsonList array)) {
                    throw new IllegalArgumentException();
                }
                return arrayIndexFinderNode.access(array, function::apply);
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private <U> @Nullable U onLastNode(@NotNull MojangsonCompound MojangsonCompound, @NotNull TriFunction<MojangsonStructure, Object, Runnable, U> function) {
        MojangsonPathNode<?, ?> node = root;
        MojangsonValue<?> p = MojangsonCompound;

        final List<Runnable> list = new ArrayList<>();

        while (node.child != null) {
            var q = getNextValue(node, p);

            if (q == null) {
                if (node instanceof MojangsonPathNode.ObjectKeyNode n) {
                    q = new MojangsonCompound();
                    MojangsonCompound t = (MojangsonCompound) p;
                    MojangsonCompound s = (MojangsonCompound) q;
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

    public <T> T access(@NotNull MojangsonCompound MojangsonCompound, @NotNull Function<MojangsonPathReference<?, ?>, T> function) {
        return onLastNode(MojangsonCompound, (lastStructure, nodeParameter, creator) -> {
            final MojangsonPathReference<?, ?> reference = switch (lastStructure) {
                case MojangsonCompound object -> new MojangsonPathReference.MojangsonCompoundPathReference(object, (String) nodeParameter, creator);
                case MojangsonList array -> new MojangsonPathReference.MojangsonListPathReference(array, (Integer) nodeParameter, creator);
                default -> throw new IllegalArgumentException("NEVER HAPPENS");
            };

            return function.apply(reference);
        });
    }

    public int length() {
        MojangsonPathNode<?, ?> node = root;

        int i = 0;
        while (node != null) {
            i++;
            node = node.child;
        }

        return i;
    }

    public @NotNull MojangsonPath slice(int begin, int end) {
        if (begin < 0 || end > length() || begin > end) {
            throw new IllegalArgumentException("インデックスが範囲外です");
        }

        MojangsonPathNode<?, ?> beginNode = root;
        for (int i = 0; i < begin; i++) {
            if (beginNode == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            beginNode = beginNode.child;
        }

        MojangsonPathNode<?, ?> node = beginNode;
        for (int i = begin; i < end; i++) {
            if (node == null) {
                throw new IllegalStateException("NEVER HAPPENS");
            }

            node = node.child;
        }

        node.child = null;

        return new MojangsonPath(beginNode);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MojangsonPath { ");
        MojangsonPathNode<?, ?> node = root;

        while (node != null) {
            sb.append(node);
            node = node.child;

            if (node != null) {
                sb.append(".");
            }
        }

        return sb.append(" }").toString();
    }

    public static @NotNull MojangsonPath of(@NotNull String path) {
        return MojangsonPathParser.parse(path);
    }

    public static abstract class MojangsonPathReference<S extends MojangsonStructure, T> {
        protected final S structure;

        protected final T parameter;

        protected final Runnable creator;

        protected MojangsonPathReference(@NotNull S structure, @NotNull T parameter, @NotNull Runnable creator) {
            this.structure = structure;
            this.parameter = parameter;
            this.creator = creator;
        }

        public abstract boolean has();

        public abstract @NotNull MojangsonValueType<?> getType();

        public abstract <U extends MojangsonValue<?>> @NotNull U get(@NotNull MojangsonValueType<U> type);

        public abstract void set(@NotNull Object value);

        public abstract void delete();

        private static final class MojangsonCompoundPathReference extends MojangsonPathReference<MojangsonCompound, String> {
            private MojangsonCompoundPathReference(@NotNull MojangsonCompound structure, @NotNull String parameter, @NotNull Runnable creator) {
                super(structure, parameter, creator);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @NotNull
            @Override
            public MojangsonValueType<?> getType() {
                return structure.getTypeOf(parameter);
            }

            @Override
            public <U extends MojangsonValue<?>> @NotNull U get(@NotNull MojangsonValueType<U> type) {
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

        private static final class MojangsonListPathReference extends MojangsonPathReference<MojangsonList, Integer> {
            private MojangsonListPathReference(@NotNull MojangsonList structure, @NotNull Integer parameter, @NotNull Runnable creator) {
                super(structure, parameter, creator);
            }

            @Override
            public boolean has() {
                return structure.has(parameter);
            }

            @NotNull
            @Override
            public MojangsonValueType<?> getType() {
                return structure.getTypeAt(parameter);
            }

            @Override
            public <U extends MojangsonValue<?>> @NotNull U get(@NotNull MojangsonValueType<U> type) {
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
