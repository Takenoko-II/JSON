package com.gmail.takenokoii78.mojangson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MojangsonPathParser {
    private static final Set<Character> WHITESPACE = Set.of(' ');

    private static final char DOT = '.';

    private static final Set<Character> QUOTES = Set.of('"', '\'');

    private static final char[] OBJECT_BRACES = {'{', '}'};

    private static final char[] ARRAY_BRACES = {'[', ']'};

    private static final char ESCAPE = '\\';

    private static final String EMPTY_STRING = "";

    private String text;

    private int location = -1;

    private MojangsonPathParser() {

    }

    private @NotNull MojangsonParseException exception(@NotNull String message) {
        return new MojangsonParseException(message, text, location);
    }

    private boolean isOver() {
        return location >= text.length() - 1;
    }

    private char peek(boolean ignorable) {
        if (isOver()) {
            throw exception("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location + 1);

        if (ignorable) {
            return WHITESPACE.contains(next) ? peek(true) : next;
        }
        return next;
    }

    private void next() {
        if (isOver()) {
            throw exception("文字列の長さが期待より不足しています");
        }

        location++;
    }

    private void whitespace() {
        if (isOver()) return;

        final char current = text.charAt(location++);

        if (WHITESPACE.contains(current)) {
            whitespace();
        }
        else {
            location--;
        }
    }

    private boolean test(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean next(@NotNull String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location + 1);

        if (str.startsWith(next)) {
            location += next.length();
            whitespace();
            return true;
        }

        return false;
    }

    private boolean next(char next) {
        return next(String.valueOf(next));
    }

    private void expect(@NotNull String next) {
        if (!next(next)) {
            throw exception("期待された文字列は" + next + "でしたが、テストが偽を返しました(" + (isOver() ? "isOver!" : peek(false)) + ")");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
    }

    private @NotNull String string() {
        char current = peek(false);
        final StringBuilder string = new StringBuilder();

        if (QUOTES.contains(current)) {
            final char quote = current;
            next();
            char previous = current;
            current = peek(false);
            next();

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    string.delete(string.length() - 1, string.length());
                }

                string.append(current);

                previous = current;
                current = peek(false);
                next();
            }
        }
        else {
            throw exception("文字列はクォーテーションで開始される必要があります");
        }

        return string.toString();
    }

    private @NotNull String[] objectKey(boolean isRoot) {
        if (!isRoot) expect(DOT);

        final StringBuilder sb = new StringBuilder();
        final StringBuilder mojangson = new StringBuilder();

        while (!isOver()) {
            final char c = peek(false);

            if (WHITESPACE.contains(c)) {
                throw exception("期待された文字は非記号文字です");
            }
            else if (QUOTES.contains(c)) {
                if (!sb.isEmpty()) {
                    throw exception("クォーテーションはキーの途中に含めることができない文字です");
                }

                sb.append(string());
                continue;
            }
            else if (c == DOT || c == ARRAY_BRACES[0]) {
                break;
            }
            else if (c == OBJECT_BRACES[0]) {
                final StringBuilder sb2 = new StringBuilder();
                sb2.append(c);
                int depth = 1;
                next();

                while (!isOver()) {
                    final char c2 = peek(false);

                    if (QUOTES.contains(c2)) {
                        sb2.append(c2)
                            .append(string())
                            .append(c2);
                        continue;
                    }

                    if (c2 == ARRAY_BRACES[0] || c2 == OBJECT_BRACES[0]) {
                        depth++;
                    }
                    else if (c2 == ARRAY_BRACES[1] || c2 == OBJECT_BRACES[1]) {
                        depth--;
                    }

                    if (depth == 0) {
                        sb2.append(c2);
                        next();
                        break;
                    }

                    sb2.append(c2);
                    next();
                }

                mojangson.append(sb2);

                return new String[]{sb.toString(), mojangson.toString()};
            }

            sb.append(c);
            next();
        }

        return new String[]{sb.toString()};
    }

    private @NotNull String arrayIndex() {
        expect(ARRAY_BRACES[0]);

        if (next(ARRAY_BRACES[1])) {
            return EMPTY_STRING;
        }

        final StringBuilder sb = new StringBuilder();
        int depth = 1;

        while (!isOver()) {
            final char c = peek(false);

            if (QUOTES.contains(c)) {
                sb.append(c)
                    .append(string())
                    .append(c);
                continue;
            }

            if (c == ARRAY_BRACES[0]) {
                depth++;
            }
            else if (c == ARRAY_BRACES[1]) {
                depth--;
            }

            if (depth == 0) {
                break;
            }

            sb.append(c);
            next();
        }

        expect(ARRAY_BRACES[1]);

        return sb.toString();
    }

    private @NotNull MojangsonPathNode<?, ?> root() {
        final List<Object> list = new ArrayList<>();

        list.add(objectKey(true));

        while (!isOver()) {
            if (peek(false) == DOT) {
                list.add(objectKey(false));
            }
            else if (peek(false) == ARRAY_BRACES[0]) {
                list.add(arrayIndex());
            }
            else {
                throw exception("不明な文字です: " + peek(false));
            }
        }

        MojangsonPathNode<?, ?> node = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            final Object value = list.get(i);

            if (value instanceof String[] strings) {
                if (strings.length == 1) {
                    node = new MojangsonPathNode.ObjectKeyNode(strings[0], node);
                }
                else if (strings.length == 2) {
                    node = new MojangsonPathNode.ObjectKeyCheckerNode(strings[0], MojangsonParser.compound(strings[1]), node);
                }
                else throw exception("NEVER HAPPENS");
            }
            else if (value instanceof String string) {
                if (string.matches("^[+-]?[1-9]*\\d+|0$")) {
                    node = new MojangsonPathNode.ArrayIndexNode(Integer.parseInt(string), node);
                }
                else {
                    node = new MojangsonPathNode.ArrayIndexFinderNode(MojangsonParser.compound(string), node);
                }
            }
            else throw exception("NEVER HAPPENS");
        }

        if (node == null) {
            throw exception("空のパスは解析できません");
        }

        return node;
    }

    private void extraChars() {
        if (!isOver()) throw exception("解析終了後、末尾に無効な文字列(" + text.substring(location) + ")を検出しました");
    }

    private @NotNull MojangsonPath parse() {
        if (text == null) {
            throw exception("textがnullです");
        }

        final MojangsonPathNode<?, ?> rootNode = root();
        extraChars();
        return new MojangsonPath(rootNode);
    }

    protected static @NotNull MojangsonPath parse(@NotNull String path) throws MojangsonParseException {
        final MojangsonPathParser parser = new MojangsonPathParser();
        parser.text = path;
        return parser.parse();
    }
}
