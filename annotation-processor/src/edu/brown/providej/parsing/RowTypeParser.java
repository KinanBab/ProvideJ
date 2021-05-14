package edu.brown.providej.parsing;

import edu.brown.providej.modules.rowtypes.RowType;
import edu.brown.providej.modules.types.*;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class RowTypeParser {
    private Messager messager;
    private String className;

    public RowTypeParser(String className, Messager messager) {
        this.messager = messager;
        this.className = className;
    }

    public RowType parseRowType(String data) throws ParseException {
        return this.parseRowType(data, this.className, "");
    }

    private RowType parseRowType(String data, String name, String nameContext) throws ParseException {
        if (nameContext.length() > 0) {
            nameContext += "." + name;
        } else {
            nameContext = name;
        }
        RowType rowType = new RowType(name, nameContext);

        HashMap<String, String> extracted = this.extractKeys(data);
        for (Map.Entry<String, String> e : extracted.entrySet()) {
            this.parseField(rowType, e.getKey(), e.getValue(), name, nameContext);
        }

        return rowType;
    }

    private void parseField(RowType dest, String fieldName, String fieldValue, String name, String nameContext) throws ParseException {
        if (fieldValue.endsWith("[]")) {
            // TODO(babman): Array Type.
            return;
        }
        if (fieldValue.startsWith("(")) {
            // TODO(babman): Or Row Type.
            return;
        }
        if (fieldValue.startsWith("{")) {
            // Nested Row Type.
            String nestedName = name + "__" + fieldName;
            dest.addField(fieldName, this.parseRowType(fieldValue, nestedName, nameContext));
        }

        // Direct type.
        if (fieldValue.equals("string")) {
            dest.addField(fieldName, new StringType());
        } else if (fieldValue.equals("int")) {
            dest.addField(fieldName, new IntType());
        } else if (fieldValue.equals("long")) {
            dest.addField(fieldName, new LongType());
        } else if (fieldValue.equals("double")) {
            dest.addField(fieldName, new DoubleType());
        } else if (fieldValue.equals("boolean")) {
            dest.addField(fieldName, new BooleanType());
        }
    }

    private HashMap<String, String> extractKeys(String data) throws ParseException {
        HashMap<String, String> result = new HashMap<>();

        data = data.trim();
        assert data.charAt(0) == '{';
        assert data.charAt(data.length() - 1) == '}';

        for (int i = 1; i < data.length() - 1; ) {
            Map.Entry<Integer, String> keyPair = this.readKey(data, i);
            i = keyPair.getKey();
            String key = keyPair.getValue().trim();

            Map.Entry<Integer, String> valuePair = this.readValue(data, i);
            i = valuePair.getKey();
            String value = valuePair.getValue().trim();

            result.put(key, value);
        }

        return result;
    }

    private Map.Entry<Integer, String> readKey(String data, int start) throws ParseException {
        String key = "";
        for (int i = start; i < data.length() - 1; i++) {
            char c = data.charAt(i);
            if (c == ' ') {
                continue;
            }
            if (c == ':') {
                return new AbstractMap.SimpleEntry<>(i + 1, key);
            }
            key += c;
        }
        throw new ParseException("Cannot read key from " + data, start);
    }

    private Map.Entry<Integer, String> readValue(String data, int start) throws ParseException {
        String value = "";
        int i ;
        int openBraceCount = 0;
        int openParenCount = 0;
        for (i = start; i < data.length() - 1; i++) {
            char c = data.charAt(i);
            if (c == ' ') {
                if (value.length() > 0 && openBraceCount == 0 && openParenCount == 0) {
                    i++;
                    break;
                }
                continue;
            }

            if (openParenCount == 0 && openBraceCount == 0 && c == ',') {
                return new AbstractMap.SimpleEntry<>(i + 1, value);
            }

            value += c;
            if (c == '(') {
                openParenCount++;
            } else if (c == '{') {
                openBraceCount++;
            } else if (c == ')') {
                openParenCount--;
                if (openParenCount == 0 && openBraceCount == 0) {
                    i++;
                    break;
                }
            } else if (c == '}') {
                openBraceCount--;
                if (openParenCount == 0 && openBraceCount == 0) {
                    i++;
                    break;
                }
            }
        }

        if (openParenCount != 0 || openBraceCount != 0) {
            throw new ParseException("Cannot read value from " + data, start);
        }

        // Consume any trailing comma.
        for (; i < data.length() - 1; i++) {
            char c = data.charAt(i);
            if (c == ' ') {
                continue;
            }
            if (c == ',') {
                return new AbstractMap.SimpleEntry<>(i + 1, value);
            }
            break;
        }
        return new AbstractMap.SimpleEntry<>(i, value);
    }
}
