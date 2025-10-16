package com.gmail.takenokoii78.json;

import com.gmail.takenokoii78.json.values.JSONArray;
import com.gmail.takenokoii78.json.values.JSONNull;
import com.gmail.takenokoii78.json.values.JSONObject;
import com.gmail.takenokoii78.json.values.JSONStructure;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

@NullMarked
public class JSONParser {
    private static final Set<Character> WHITESPACE = Set.of(' ', '\n');

    private static final char COMMA = ',';

    private static final char COLON = ':';

    private static final char SEMICOLON = ';';

    private static final char ESCAPE = '\\';

    private static final Set<Character> QUOTES = Set.of('"');

    private static final char[] OBJECT_BRACES = {'{', '}'};

    private static final char[] ARRAY_BRACES = {'[', ']'};

    private static final Set<Character> SIGNS = Set.of('+', '-');

    private static final char DECIMAL_POINT = '.';

    private static final Set<Character> NUMBERS = Set.of('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');

    private static final String[] BOOLEANS = {"false", "true"};

    private static final Function<String, Long> INT_PARSER = Long::parseLong;

    private static final Function<String, Double> DECIMAL_PARSER = Double::parseDouble;

    private static final Set<Character> SYMBOLS_ON_STRING = new HashSet<>();

    private static final String NULL = "null";

    static {
        SYMBOLS_ON_STRING.addAll(WHITESPACE);
        SYMBOLS_ON_STRING.add(COMMA);
        SYMBOLS_ON_STRING.add(COLON);
        SYMBOLS_ON_STRING.add(SEMICOLON);
        SYMBOLS_ON_STRING.add(ESCAPE);
        SYMBOLS_ON_STRING.addAll(QUOTES);
        SYMBOLS_ON_STRING.add(OBJECT_BRACES[0]);
        SYMBOLS_ON_STRING.add(OBJECT_BRACES[1]);
        SYMBOLS_ON_STRING.add(ARRAY_BRACES[0]);
        SYMBOLS_ON_STRING.add(ARRAY_BRACES[1]);
        SYMBOLS_ON_STRING.addAll(SIGNS);
        SYMBOLS_ON_STRING.add(DECIMAL_POINT);
    }

    private final String text;

    private int location = 0;

    private JSONParser(String text) {
        this.text = text;
    }

    private JSONParseException exception(String message) {
        return new JSONParseException(message, text, location);
    }

    private boolean isOver() {
        return location >= text.length();
    }

    private char next(boolean ignorable) {
        if (isOver()) {
            throw exception("文字列の長さが期待より不足しています");
        }

        final char next = text.charAt(location++);

        if (ignorable) {
            return WHITESPACE.contains(next) ? next(true) : next;
        }
        return next;
    }

    private void back() {
        if (location > 0) location--;
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

    private boolean test(String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

        return str.startsWith(next);
    }

    private boolean test(char next) {
        return test(String.valueOf(next));
    }

    private boolean next(String next) {
        if (isOver()) return false;

        whitespace();

        final String str = text.substring(location);

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

    private void expect(String next) {
        if (!next(next)) {
            throw exception("期待された文字列は" + next + "でしたが、テストが偽を返しました");
        }
    }

    private void expect(char next) {
        expect(String.valueOf(next));
    }

    private String stringValue() {
        final StringBuilder sb = new StringBuilder();
        char current = next(true);

        if (QUOTES.contains(current)) {
            final char quote = current;
            char previous = current;
            current = next(false);

            while (previous == ESCAPE || current != quote) {
                if (previous == ESCAPE && current == quote) {
                    sb.delete(sb.length() - 1, sb.length());
                }

                sb.append(current);

                previous = current;
                current = next(false);
            }

            return sb.toString();
        }
        else throw exception("文字列はクォーテーションで開始される必要があります");
    }

    private Number numberValue() {
        final StringBuilder sb = new StringBuilder();
        char current = next(false);

        if (SIGNS.contains(current) || NUMBERS.contains(current)) {
            sb.append(current);
        }
        else {
            throw exception("数値の解析中に無効な文字を検出しました: " + current);
        }

        char previous;
        boolean decimalPointAppeared = false;

        while (!isOver()) {
            previous = current;
            current = next(false);

            if (NUMBERS.contains(current)) {
                sb.append(current);
            }
            else if (NUMBERS.contains(previous) && current == DECIMAL_POINT && !decimalPointAppeared) {
                sb.append(current);
                decimalPointAppeared = true;
            }
            else if (SYMBOLS_ON_STRING.contains(current)) {
                back();
                break;
            }
            else throw exception("数値の解析中に無効な文字を検出しました: " + current);
        }

        if (!NUMBERS.contains(sb.charAt(sb.length() - 1))) {
            throw exception("数値は数字で終わる必要があります");
        }

        final Function<String, ? extends Number> parser = decimalPointAppeared ? DECIMAL_PARSER : INT_PARSER;

        return parser.apply(sb.toString());
    }

    private Boolean booleanValue() {
        if (next(BOOLEANS[0])) {
            return false;
        }
        else if (next(BOOLEANS[1])) {
            return true;
        }
        else throw exception("真偽値が見つかりませんでした");
    }

    private JSONObject objectValue() {
        expect(OBJECT_BRACES[0]);

        final JSONObject jsonObject = new JSONObject();

        if (next(OBJECT_BRACES[1])) {
            return jsonObject;
        }

        keyValues(jsonObject);

        expect(OBJECT_BRACES[1]);

        return jsonObject;
    }

    private JSONArray arrayValue() {
        expect(ARRAY_BRACES[0]);

        final JSONArray array = new JSONArray();

        if (next(ARRAY_BRACES[1])) {
            return array;
        }

        elements(array);

        expect(ARRAY_BRACES[1]);

        return array;
    }

    private void keyValues(JSONObject jsonObject) {
        final String key = stringValue();
        if (!next(COLON)) throw exception("コロンが必要です");
        jsonObject.set(key, value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) keyValues(jsonObject);
        else if (commaOrBrace == OBJECT_BRACES[1]) back();
        else throw exception("閉じ括弧が見つかりません");
    }

    private void elements(JSONArray array) {
        array.add(value());

        final char commaOrBrace = next(true);

        if (commaOrBrace == COMMA) elements(array);
        else if (commaOrBrace == ARRAY_BRACES[1]) back();
        else throw exception("閉じ括弧が見つかりません");
    }

    private Object value() {
        if (test(OBJECT_BRACES[0])) {
            return objectValue();
        }
        else if (test(ARRAY_BRACES[0])) {
            return arrayValue();
        }
        else if (QUOTES.stream().anyMatch(this::test)) {
            return stringValue();
        }
        else if (Arrays.stream(BOOLEANS).anyMatch(this::test)) {
            return booleanValue();
        }
        else if (SIGNS.stream().anyMatch(this::test) || NUMBERS.stream().anyMatch(this::test)) {
            return numberValue();
        }
        else if (next(NULL)) {
            return JSONNull.NULL;
        }
        else throw exception("値が見つかりませんでした");
    }

    private void extraChars() {
        if (!isOver()) throw exception("解析終了後、末尾に無効な文字列(" + text.substring(location) + ")を検出しました");
    }

    private Object parse() {
        final Object value = value();
        extraChars();
        return value;
    }

    private static <T> T parseAs(String text, Class<T> clazz) {
        final Object value = new JSONParser(text).parse();

        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        else throw new IllegalArgumentException("期待された型(" + clazz.getName() + ")と取得した値(" + value.getClass().getName() + ")が一致しません");
    }

    public static JSONObject object(String text) throws JSONParseException {
        return parseAs(text, JSONObject.class);
    }

    public static JSONArray array(String text) throws JSONParseException {
        return parseAs(text, JSONArray.class);
    }

    public static JSONStructure structure(String text) throws JSONParseException {
        return parseAs(text, JSONStructure.class);
    }
}
