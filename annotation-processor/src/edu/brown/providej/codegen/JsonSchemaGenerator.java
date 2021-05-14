package edu.brown.providej.codegen;

import edu.brown.providej.annotations.enums.Visibility;
import edu.brown.providej.parsing.JsonSchema;
import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ArrayType;
import edu.brown.providej.modules.types.ObjectType;
import edu.brown.providej.modules.types.OrType;

public class JsonSchemaGenerator {
    private final JsonSchema schema;

    public JsonSchemaGenerator(JsonSchema schema) {
        this.schema = schema;
    }

    // Generate java code.
    private String generateNestedTypes() {
        StringBuilder builder = new StringBuilder();
        // Create all required nested types.
        for (ObjectType objectType : this.schema.getNestedTypes()) {
            ObjectTypeGenerator objectTypeGenerator = new ObjectTypeGenerator(objectType);
            builder.append(objectTypeGenerator.generateEntireClass());
            builder.append("\n");
        }
        for (OrType orType : this.schema.getNestedOrTypes()) {
            OrTypeGenerator orTypeGenerator = new OrTypeGenerator(orType);
            builder.append(orTypeGenerator.generateOrClass());
            builder.append("\n");
        }
        return builder.toString();
    }

    private String generateRootData() {
        StringBuilder builder = new StringBuilder();
        builder.append("public static final ");
        builder.append(this.schema.getRootValue().getType().javaType());
        builder.append(" DATA = ");
        builder.append(this.schema.getRootValue().javaValue());
        builder.append(";\n");
        return builder.toString();
    }

    private String generateClassContent(AbstractType classType) {
        switch (classType.getKind()) {
            case OBJECT:
                ObjectTypeGenerator objectTypeGenerator = new ObjectTypeGenerator((ObjectType) classType);
                return objectTypeGenerator.generateClassContent();
            case ARRAY:
                ArrayType type = (ArrayType) classType;
                return this.generateClassContent(type.getDataType());
            default:
                return "";
        }
    }

    public String generateJavaClass(String packageName, Visibility visibility) {
        StringBuilder builder = new StringBuilder();
        // Package and imports.
        builder.append("package " + packageName + ";\n\n");
        builder.append("import edu.brown.providej.runtime.*;\n\n");
        builder.append("import edu.brown.providej.runtime.types.*;\n\n");
        // <visibility> class <name> {
        builder.append(visibility.toString());
        builder.append(" class ");
        builder.append(this.schema.getClassName());
        builder.append(this.schema.getRootValue().getType().javaInterfacesImplemented());
        builder.append(" {\n");

        // Class content (data members, constructor, etc) if exists.
        AbstractType rootType = this.schema.getRootValue().getType();
        builder.append(this.generateClassContent(rootType));
        builder.append("\n");

        // static root data.
        builder.append(this.generateRootData());
        builder.append("\n");

        // Generate nested classes.
        builder.append(this.generateNestedTypes());
        builder.append("\n");

        builder.append("}");
        return builder.toString();
    }
}
