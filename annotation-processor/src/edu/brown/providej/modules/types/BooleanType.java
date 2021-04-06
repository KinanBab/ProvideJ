package edu.brown.providej.modules.types;

public class BooleanType extends AbstractType {
    public BooleanType() {
        super(AbstractType.Kind.BOOLEAN);
    }

    @Override
    public String javaType() {
        return "boolean";
    }

    @Override
    public String javaRefType() {
        return "Boolean";
    }
}
