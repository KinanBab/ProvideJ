package edu.brown.providej.modules.types;

public class IntType extends FieldType {
    public IntType() {
        super(FieldType.Kind.INT);
    }

    @Override
    public String toString() {
        return "int";
    }
}
