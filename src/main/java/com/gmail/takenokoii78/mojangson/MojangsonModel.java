package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonList;
import jdk.jfr.Experimental;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Experimental
public interface MojangsonModel {
    interface MojangsonCompoundModel extends MojangsonModel {
        static <T extends MojangsonCompoundModel> void write(@NotNull MojangsonCompound compound, @NotNull T destination) {
            final List<Field> fields = Arrays.stream(destination.getClass().getDeclaredFields()).toList();

            for (final String key : compound.keys()) {
                for (final Field field : fields) {
                    try {
                        field.set(destination, compound.get(key, compound.getTypeOf(key)).value);
                    }
                    catch (IllegalAccessException e) {
                        return;
                    }
                }
            }
        }
    }

    interface MojangsonListModel extends MojangsonModel, Collection<Object> {
        static <T extends MojangsonListModel> void write(@NotNull MojangsonList list, @NotNull T destination) {
            destination.clear();
            destination.addAll(list.toList());
        }
    }
}
