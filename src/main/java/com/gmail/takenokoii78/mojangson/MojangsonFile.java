package com.gmail.takenokoii78.mojangson;

import com.gmail.takenokoii78.mojangson.values.MojangsonCompound;
import com.gmail.takenokoii78.mojangson.values.MojangsonList;
import com.gmail.takenokoii78.mojangson.values.MojangsonStructure;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.function.Function;

public class MojangsonFile {
    private final Path path;

    public MojangsonFile(@NotNull Path path) {
        this.path = path;

        if (!path.toFile().isFile()) {
            throw new IllegalArgumentException("そのパスはファイルパスとして無効です");
        }
    }

    public MojangsonFile(@NotNull String path) {
        this(Path.of(path));
    }

    public MojangsonFile(@NotNull File file) {
        this(file.toPath());
    }

    public @NotNull Path getPath() {
        return path;
    }

    protected @NotNull String readAsString() throws IllegalStateException {
        if (exists()) try {
            return String.join("\n", Files.readAllLines(path));
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの読み取りに失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    protected void writeAsString(@NotNull String json) throws IllegalStateException {
        if (exists()) try {
            Files.write(
                path,
                Arrays.asList(json.split("\\n")),
                StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING
            );
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの書き込みに失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public boolean exists() {
        return path.toFile().exists();
    }

    public void create() throws IllegalStateException {
        if (exists()) throw new IllegalStateException("既にファイルは存在します");
        else try {
            Files.createFile(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの作成に失敗しました", e);
        }
    }

    public void delete() throws IllegalStateException {
        if (exists()) try {
            Files.delete(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルの削除に失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public long getSize() throws IllegalStateException {
        if (exists()) try {
            return Files.size(path);
        }
        catch (IOException e) {
            throw new IllegalStateException("ファイルサイズの取得に失敗しました", e);
        }
        else throw new IllegalStateException("ファイルが存在しません");
    }

    public @NotNull MojangsonStructure read() throws MojangsonParseException, IllegalStateException {
        final MojangsonValue<?> value = MojangsonParser.object(readAsString());
        if (value instanceof MojangsonStructure structure) {
            return structure;
        }
        else {
            throw new IllegalStateException("非構造体が記述されたファイルはMojangsonファイルとして無効です");
        }
    }

    public void write(@NotNull MojangsonStructure structure) throws MojangsonSerializationException, IllegalStateException {
        writeAsString(MojangsonSerializer.toJson(structure));
    }

    public @NotNull MojangsonCompound readAsCompound() throws MojangsonParseException, IllegalStateException {
        return MojangsonParser.compound(readAsString());
    }

    public @NotNull MojangsonList readAsList() throws MojangsonParseException, IllegalStateException {
        return MojangsonParser.list(readAsString());
    }

    public void edit(@NotNull Function<MojangsonStructure, MojangsonStructure> function) throws MojangsonParseException, MojangsonSerializationException, IllegalStateException {
        write(function.apply(read()));
    }
}
