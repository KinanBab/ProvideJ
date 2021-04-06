package edu.brown.providej.codegen;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ObjectType;
import edu.brown.providej.modules.types.OrType;
import edu.brown.providej.modules.types.RuntimeType;

public class OrTypeGenerator {
    private static String optionEnum(AbstractType type) {
        switch (type.getKind()) {
            case ARRAY:
                return "ARRAY";
            case DOUBLE:
                return "DOUBLE";
            case BOOLEAN:
                return "BOOLEAN";
            case OR:
                throw new IllegalArgumentException("encountered nested Or in OrType");
            case INT:
                return "INT";
            case LONG:
                return "LONG";
            case OBJECT:
                ObjectType ot = (ObjectType) type;
                String[] words = ot.getBaseName().split("(?<=.)(?=\\p{Lu})");
                return String.join("_", words).toUpperCase();
            case STRING:
                return "STRING";
            case NULLABLE:
                throw new IllegalArgumentException("encountered Nullable inside OrType");
            case RUNTIME_TYPE:
                RuntimeType rt = (RuntimeType) type;
                switch (rt.getRuntimeType()) {
                    case NULL:
                        throw new IllegalArgumentException("encountered NULL type in OrType");
                    case EMPTY_ARRAY:
                        return "EMPTY_ARRAY";
                    case EMPTY_OBJECT:
                        return "EMPTY_OBJECT";
                }
            default:
                throw new IllegalStateException("Unreachable option enum code");
        }
    }

    private static String optionName(AbstractType type) {
        switch (type.getKind()) {
            case OBJECT:
                ObjectType ot = (ObjectType) type;
                return ot.getBaseName();
            case RUNTIME_TYPE:
                RuntimeType rt = (RuntimeType) type;
                switch (rt.getRuntimeType()) {
                    case NULL:
                        throw new IllegalArgumentException("encountered NULL type in OrType");
                    case EMPTY_ARRAY:
                        return "EmptyArray";
                    case EMPTY_OBJECT:
                        return "EmptyObject";
                }
            default:
                String enumName = OrTypeGenerator.optionEnum(type);
                return enumName.charAt(0) + enumName.substring(1).toLowerCase();
        }
    }

    private OrType orType;

    public OrTypeGenerator(OrType orType) {
        this.orType = orType;
    }

    private String generateTypeEnum() {
        StringBuilder builder = new StringBuilder();
        builder.append("public enum Type {\n");
        for (AbstractType option : orType.getOptions()) {
            builder.append(OrTypeGenerator.optionEnum(option));
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append("\n}\n");
        return builder.toString();
    }

    private String generateMembers() {
        StringBuilder builder = new StringBuilder();
        builder.append("private Type type;\n");
        builder.append("private Object value;\n");
        return builder.toString();
    }

    private String generateConstructors() {
        StringBuilder builder = new StringBuilder();
        for (AbstractType option : orType.getOptions()) {
            builder.append("public ");
            builder.append(this.orType.getQualifiedName());
            builder.append("(");
            builder.append(option.javaType());
            builder.append(" value) {\n");
            builder.append("this.type = Type.");
            builder.append(OrTypeGenerator.optionEnum(option));
            builder.append(";\n");
            builder.append("this.value = value;\n");
            builder.append("}\n");
        }
        return builder.toString();
    }

    private String generateAccessors() {
        StringBuilder builder = new StringBuilder();
        builder.append("public Type getType() {\n");
        builder.append("return this.type;\n");
        builder.append("}\n");
        for (AbstractType option : orType.getOptions()) {
            builder.append("public ");
            builder.append(option.javaType());
            builder.append(" as");
            builder.append(OrTypeGenerator.optionName(option));
            builder.append("() {\n");
            builder.append("if (this.type != Type." + OrTypeGenerator.optionEnum(option) + ") {\n");
            builder.append("throw new ClassCastException(\"OrType accessed with wrong type!\");\n");
            builder.append("}\n");
            builder.append("return (");
            builder.append(option.javaType());
            builder.append(") this.value;\n");
            builder.append("}\n");
        }
        return builder.toString();
    }

    private String generateToString() {
        StringBuilder builder = new StringBuilder();
        builder.append("@Override\n");
        builder.append("public String toString() {\n");
        builder.append("return ProvideJUtil.toJSON(this.value);\n");
        builder.append("}\n");
        return builder.toString();
    }

    public String generateOrClass() {
        StringBuilder builder = new StringBuilder();
        builder.append("public static class ");
        builder.append(this.orType.getQualifiedName());
        builder.append(" {\n");

        builder.append(this.generateTypeEnum());
        builder.append("\n");

        builder.append(this.generateMembers());
        builder.append("\n");

        builder.append(this.generateConstructors());
        builder.append("\n");

        builder.append(this.generateAccessors());
        builder.append("\n");

        builder.append(this.generateToString());
        builder.append("\n");

        builder.append("}\n");
        return builder.toString();
    }
}
