package edu.brown.providej.modules;

import edu.brown.providej.modules.types.FieldType;
import edu.brown.providej.modules.types.IntType;
import edu.brown.providej.modules.types.StringType;

import java.util.Hashtable;
import java.util.Map;

public class JsonSchema {
    private final Hashtable<String, FieldType> fields;

    // Private constructor, can only construct using static .parseSchema()
    private JsonSchema() {
        this.fields = new Hashtable<String, FieldType>();
    }

    // Hardcoded schema for now.
    public static JsonSchema parseSchema() {
        JsonSchema schema = new JsonSchema();
        schema.addField("id", new IntType());
        schema.addField("name", new StringType());
        return schema;
    }

    // Getters and setters..
    private void addField(String field, FieldType type) {
        this.fields.put(field, type);
    }

    public boolean hasField(String field) {
        return this.fields.containsKey(field);
    }

    public FieldType getField(String field) {
        return this.fields.get(field);
    }

    // Java Class representation of the schema.
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // Define fields.
        for (Map.Entry<String, FieldType> e : this.fields.entrySet()) {
            builder.append("private ");
            builder.append(e.getValue().toString());
            builder.append(" ");
            builder.append(e.getKey());
            builder.append(";");
            builder.append("\n");
        }
        // Define getters and setters.
        for (Map.Entry<String, FieldType> e : this.fields.entrySet()) {
            String cased = e.getKey();
            cased = cased.substring(0, 1).toUpperCase() + cased.substring(1);
            builder.append("\n");
            builder.append("public ");
            builder.append(e.getValue().toString());
            builder.append(" get");
            builder.append(cased);
            builder.append("() {\n");
            builder.append("  return this.");
            builder.append(e.getKey());
            builder.append(";\n}\n");
            builder.append("public void set");
            builder.append(cased);
            builder.append("(");
            builder.append(e.getValue().toString());
            builder.append(" v) {\n");
            builder.append("  this.");
            builder.append(e.getKey());
            builder.append(" = v;\n}\n");
        }
        return builder.toString();
    }
}
