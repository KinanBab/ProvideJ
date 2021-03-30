package edu.brown.providej.modules.types;

public class IntType extends AbstractType {
    int value;

    public IntType(int value) {
        super(AbstractType.Kind.INT);
        this.value = value;
    }

    @Override
    public String javaType() {
        return "int";
    }

    @Override
    public String javaValue() { return String.valueOf(this.value); }
}
