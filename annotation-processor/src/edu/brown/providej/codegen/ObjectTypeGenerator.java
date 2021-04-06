package edu.brown.providej.codegen;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ObjectType;
import edu.brown.providej.modules.types.RuntimeType;

import java.util.Map;

public class ObjectTypeGenerator {
    private final ObjectType objectType;

    public ObjectTypeGenerator(ObjectType objectType) {
        this.objectType = objectType;
    }

    // Generate constructor.
    private String generateConstructor() {
        StringBuilder builder = new StringBuilder();
        builder.append("public ");
        builder.append(this.objectType.javaType());
        builder.append("(");
        // Constructor arguments.
        for (Map.Entry<String, AbstractType> e : this.objectType) {
            builder.append(e.getValue().javaType());
            builder.append(" ");
            builder.append(e.getKey());
            builder.append(", ");
        }
        if (this.objectType.size() > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append(") {\n");
        // Constructor body.
        for (Map.Entry<String, AbstractType> e : this.objectType) {
            builder.append("this.");
            builder.append(e.getKey());
            builder.append(" = ");
            builder.append(e.getKey());
            builder.append(";\n");
        }
        builder.append("}\n");
        return builder.toString();
    }

    // Generate data members (private).
    private String generateMembers() {
        StringBuilder builder = new StringBuilder();
        // Define fields.
        for (Map.Entry<String, AbstractType> e : this.objectType) {
            builder.append("private ");
            builder.append(e.getValue().javaType());
            builder.append(" ");
            builder.append(e.getKey());
            builder.append(";");
            builder.append("\n");
        }
        return builder.toString();
    }

    // Generate public getters and setters for every private data member.
    private String generateMethods() {
        StringBuilder builder = new StringBuilder();
        // Define getters and setters.
        for (Map.Entry<String, AbstractType> e : this.objectType) {
            String cased = ObjectType.Case(e.getKey());
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

    // Generate the toJson Function.
    private String generateToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@Override\n");
        builder.append("public String toString() {\n");
        builder.append("StringBuilder builder = new StringBuilder();\n");
        builder.append("builder.append(\"{\");");

        // Define getters and setters.
        for (Map.Entry<String, AbstractType> e : this.objectType) {
            builder.append("builder.append(\"\\\"" + e.getKey() + "\\\": \");\n");
            switch (e.getValue().getKind()) {
                case OBJECT:
                    builder.append("builder.append(this." + e.getKey() + ".toString());\n");
                    break;
                case ARRAY:
                case OR:
                    builder.append("builder.append(ProvideJUtil.toJSON(this." + e.getKey() + "));\n");
                    break;
                case RUNTIME_TYPE:
                    RuntimeType rtype = (RuntimeType) e.getValue();
                    switch (rtype.getRuntimeType()) {
                        case NULL:
                            builder.append("builder.append(\"null\");\n");
                            break;
                        case EMPTY_OBJECT:
                            builder.append("builder.append(\"{}\");\n");
                            break;
                        case EMPTY_ARRAY:
                            builder.append("builder.append(\"[]\");\n");
                            break;
                    }
                    break;
                case STRING:
                    builder.append("builder.append('\"' + this." + e.getKey() + " + '\"');\n");
                    break;
                case DOUBLE:
                case LONG:
                case INT:
                case BOOLEAN:
                case NULLABLE:
                    builder.append("builder.append(this." + e.getKey() + " + \"\");\n");
                    break;
            }
            builder.append("builder.append(\", \");\n");
        }
        if (this.objectType.size() > 0) {
            builder.append("builder.setLength(builder.length() - 2);\n");
        }
        builder.append("builder.append(\"}\");\n");
        builder.append("return builder.toString();\n");
        builder.append("}\n");
        return builder.toString();
    }

    public String generateClassContent() {
        StringBuilder builder = new StringBuilder();

        // Members (all private).
        builder.append(this.generateMembers());
        builder.append("\n");

        // The one constructor (which receives values for all members in order).
        builder.append(this.generateConstructor());
        builder.append("\n");

        // Public getters/setters for each member.
        builder.append(this.generateMethods());
        builder.append("\n");

        builder.append(this.generateToString());
        builder.append("\n");

        return builder.toString();
    }

    public String generateEntireClass() {
        StringBuilder builder = new StringBuilder();
        builder.append("public static class ");
        builder.append(this.objectType.javaType());
        builder.append(" {\n");
        builder.append(this.generateClassContent());
        builder.append("}\n");
        return builder.toString();
    }
}
