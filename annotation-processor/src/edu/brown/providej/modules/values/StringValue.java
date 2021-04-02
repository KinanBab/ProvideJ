package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.StringType;

public class StringValue extends AbstractValue {
    public static String sanitize(String v) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < v.length(); i++) {
            char c = v.charAt(i);
            if (c == '"') {
                if (i == 0 || v.charAt(i - 1) != '\'') {
                    builder.append('\\');
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }

    private String value;

    public StringValue(String value) {
        super(new StringType());
        this.value = StringValue.sanitize(value);
    }

    @Override
    public String javaValue() {
        return "\"" + this.value + "\"";
    }
}
