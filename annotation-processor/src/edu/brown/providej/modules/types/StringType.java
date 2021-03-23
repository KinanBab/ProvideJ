package edu.brown.providej.modules.types;

public class StringType extends FieldType {
    public StringType() {
        super(FieldType.Kind.STRING);
    }

    @Override
    public String toString() {
        return "String";
    }
}
