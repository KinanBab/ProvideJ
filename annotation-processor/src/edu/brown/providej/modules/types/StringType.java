package edu.brown.providej.modules.types;

public class StringType extends AbstractType {
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

    public StringType(String value) {
        super(AbstractType.Kind.STRING);
        this.value = StringType.sanitize(value);
    }

    @Override
    public String javaType() { return "String"; }

    @Override
    public String javaValue() { return "\"" + this.value + "\""; }
}
