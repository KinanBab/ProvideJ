package edu.brown.providej.modules.types;

import edu.brown.providej.modules.JsonSchema;

public class ObjectType extends AbstractType {
    private static String Case(String name) {
        String upper = name.substring(0, 1).toUpperCase();
        return upper + name.substring(1);
    }

    private String name;
    private JsonSchema schema;

    public ObjectType(String name, JsonSchema schema) {
        super(AbstractType.Kind.OBJECT);
        this.name = Case(name);
        this.schema = schema;
    }

    public String getName() { return this.name; }
    public JsonSchema getSchema() { return this.schema; }

    @Override
    public String javaType() {
        return this.name;
    }

    @Override
    public String javaValue() { return this.schema.generateJavaValue(this.name); }
}
