package edu.brown.providej.codegen;

import edu.brown.providej.annotations.enums.Visibility;
import edu.brown.providej.modules.rowtypes.RowType;
import edu.brown.providej.modules.types.ObjectType;

public class RowTypeGenerator {
    private final RowType rowType;
    private final Visibility visibility;

    public RowTypeGenerator(RowType rowType) {
        this(rowType, null);
    }

    public RowTypeGenerator(RowType rowType, Visibility visibility) {
        this.rowType = rowType;
        this.visibility = visibility;
    }

    // Generate public getters and setters for every private data member.
    private String generateMethods() {
        StringBuilder builder = new StringBuilder();
        // Define getters and setters.
        for (String key : this.rowType) {
            String cased = ObjectType.Case(key);
            String typeName;
            if (this.rowType.isDirect(key)) {
                typeName = this.rowType.getDirectType(key).javaType();
            } else {
                typeName = this.rowType.getNestedType(key).getName();
            }

            builder.append(typeName);
            builder.append(" get");
            builder.append(cased);
            builder.append("();\n\n");
        }
        return builder.toString();
    }

    private String generateNested() {
        StringBuilder builder = new StringBuilder();
        for (String key : this.rowType) {
            if (!this.rowType.isDirect(key)) {
                RowTypeGenerator generator = new RowTypeGenerator(this.rowType.getNestedType(key));
                builder.append(generator.generateEntireClass());
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public String generateInterfaceContent() {
        StringBuilder builder = new StringBuilder();
        // Public getters/setters for each member.
        builder.append(this.generateMethods());
        builder.append("\n");
        // Nested row types.
        builder.append(this.generateNested());
        builder.append("\n");
        return builder.toString();
    }

    public String generateEntireClass() {
        String visibility = "";
        if (this.visibility != null) {
            visibility = this.visibility.toString() + " ";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(visibility + "interface ");
        builder.append(this.rowType.getName());
        builder.append(" {\n");
        builder.append(this.generateInterfaceContent());
        builder.append("}\n");
        return builder.toString();
    }
}
