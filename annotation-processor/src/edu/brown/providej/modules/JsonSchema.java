package edu.brown.providej.modules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.brown.providej.annotations.enums.Visibility;
import edu.brown.providej.modules.types.*;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class JsonSchema {
    private final TreeMap<String, AbstractType> fields;

    // Private constructor, can only construct using static .parseSchema()
    private JsonSchema() {
        this.fields = new TreeMap<String, AbstractType>();
    }

    // Empty schema.
    public static JsonSchema empty() {
        return new JsonSchema();
    }

    // Create schema for the given JSON file.
    public static JsonSchema parseSchema(String jsonFilePath) throws IOException {
        File jsonFile = new File(jsonFilePath);
        return JsonSchema.parseSchema(new ObjectMapper().readTree(jsonFile));
    }
    private static JsonSchema parseSchema(JsonNode root) throws IOException {
        JsonSchema schema = new JsonSchema();

        switch (root.getNodeType()) {
            case OBJECT:
                Iterator<Map.Entry<String, JsonNode>> it = root.fields();
                while (it.hasNext()) {
                    Map.Entry<String, JsonNode> e = it.next();
                    AbstractType type = JsonSchema.parseField(e.getKey(), e.getValue());
                    schema.addField(e.getKey(), type);
                }
                break;
            case ARRAY:
            case BOOLEAN:
            case NUMBER:
            case STRING:
            case NULL:
            case BINARY:
            case MISSING:
            case POJO:
                throw new IOException("Expected container JSON type, found " + root.getNodeType());
        }

        return schema;
    }
    private static AbstractType parseField(String fieldname, JsonNode node) throws IOException {
        switch (node.getNodeType()) {
            case OBJECT:
                if (!node.fields().hasNext()) {
                    return new RuntimeType(RuntimeType.Types.EMPTY_OBJECT);
                } else {
                    JsonSchema nestedSchema = JsonSchema.parseSchema(node);
                    return new ObjectType(fieldname, nestedSchema);
                }
            case ARRAY:
                if (node.size() == 0) {
                    return new RuntimeType(RuntimeType.Types.EMPTY_ARRAY);
                } else {
                    ArrayType type = new ArrayType(node.size());
                    for (int i = 0; i < node.size(); i++) {
                        type.addType(parseField(fieldname, node.get(i)));
                    }
                    return type;
                }

            case BOOLEAN:
                return new BooleanType(node.booleanValue());
            case NUMBER:
                if (node.isDouble() && !node.isLong() && !node.isInt()) {
                    return new DoubleType(node.doubleValue());
                } else if (node.canConvertToInt()) {
                    return new IntType(node.intValue());
                } else {
                    return new LongType(node.longValue());
                }
            case STRING:
                return new StringType(node.textValue());

            case NULL:
                return new RuntimeType(RuntimeType.Types.NULL);

            case BINARY:
            case MISSING:
            case POJO:
            default:
                throw new IOException("Cannot parse JSON!");
        }
    }

    // Getters and setters..
    private void addField(String field, AbstractType type) {
        this.fields.put(field, type);
    }

    public boolean hasField(String field) {
        return this.fields.containsKey(field);
    }

    public AbstractType getField(String field) {
        return this.fields.get(field);
    }

    // Generate a java value (in code) that contains the values in this schema.
    public String generateJavaValue(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(name);
        builder.append("(");
        // Constructor arguments.
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            builder.append(e.getValue().javaValue());
            builder.append(", ");
        }
        if (this.fields.size() > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append(")");
        return builder.toString();
    }

    // Generate java code.
    private String generateNestedTypes() {
        StringBuilder builder = new StringBuilder();
        // Create all required nested types.
        for (AbstractType t : this.fields.values()) {
            if (t.getKind() == AbstractType.Kind.OBJECT) {
                builder.append(this.generateNestedType((ObjectType) t));
            } else if (t.getKind() == AbstractType.Kind.ARRAY) {
                builder.append(this.generateNestedType((ArrayType) t));
            }
        }
        return builder.toString();
    }
    private String generateNestedType(ObjectType objectType) {
        String nestedName = objectType.getName();
        JsonSchema nestedSchema = objectType.getSchema();
        return nestedSchema.generateJavaCode(nestedName, Visibility.PUBLIC, "static");
    }
    private String generateNestedType(ArrayType arrayType) {
        AbstractType innerType = arrayType.getUnifiedType();
        if (innerType.getKind() == AbstractType.Kind.OBJECT) {
            return this.generateNestedType((ObjectType) innerType);
        } else if (innerType.getKind() == AbstractType.Kind.ARRAY) {
            return this.generateNestedType((ArrayType) innerType);
        }
        return "";
    }

    private String generateConstructor(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append("public ");
        builder.append(name);
        builder.append("(");
        // Constructor arguments.
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            builder.append(e.getValue().javaType());
            builder.append(" ");
            builder.append(e.getKey());
            builder.append(", ");
        }
        if (this.fields.size() > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append(") {\n");
        // Constructor body.
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            builder.append("this.");
            builder.append(e.getKey());
            builder.append(" = ");
            builder.append(e.getKey());
            builder.append(";\n");
        }
        builder.append("}\n");
        return builder.toString();
    }
    private String generateMembers() {
        StringBuilder builder = new StringBuilder();
        // Define fields.
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            builder.append("private ");
            builder.append(e.getValue().javaType());
            builder.append(" ");
            builder.append(e.getKey());
            builder.append(";");
            builder.append("\n");
        }
        return builder.toString();
    }
    private String generateMethods() {
        StringBuilder builder = new StringBuilder();
        // Define getters and setters.
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            String cased = e.getKey();
            cased = cased.substring(0, 1).toUpperCase() + cased.substring(1);
            builder.append("public ");
            builder.append(e.getValue().javaType());
            builder.append(" get");
            builder.append(cased);
            builder.append("() {\n");
            builder.append("  return this.");
            builder.append(e.getKey());
            builder.append(";\n}\n");
            builder.append("public void set");
            builder.append(cased);
            builder.append("(");
            builder.append(e.getValue().javaType());
            builder.append(" v) {\n");
            builder.append("  this.");
            builder.append(e.getKey());
            builder.append(" = v;\n}\n");
        }
        return builder.toString();
    }
    public String generateJavaCode(String name, Visibility visibility) {
        return this.generateJavaCode(name, visibility, "");
    }
    private String generateJavaCode(String name, Visibility visibility, String modifiers) {
        StringBuilder builder = new StringBuilder();
        // <visibility> class <classname> {
        builder.append(visibility.toString());
        builder.append(" ");
        builder.append(modifiers);
        builder.append(" class ");
        builder.append(name);
        builder.append(" {\n");

        // If this is a proper top-level class, it corresponds to a given singleton JSON value,
        // put that value in as a static member.
        if (!modifiers.equals("static")) {
            builder.append("public static final ");
            builder.append(name);
            builder.append(" DATA = ");
            builder.append(this.generateJavaValue(name));
            builder.append(";\n\n");
        }

        // Members (all private).
        builder.append(this.generateMembers());
        builder.append("\n");

        // The one constructor (which receives values for all members in order).
        builder.append(this.generateConstructor(name));
        builder.append("\n");

        // Public getters/setters for each member.
        builder.append(this.generateMethods());
        builder.append("\n");

        // Any nested types needed (all public static classes).
        builder.append(this.generateNestedTypes());

        builder.append("}");
        return builder.toString();
    }
}
