package edu.brown.providej.modules.types;

public class BooleanType extends AbstractType {
    private boolean value;

    public BooleanType(boolean value) {
        super(AbstractType.Kind.BOOLEAN);
        this.value = value;
    }

    @Override
    public String javaType() {
        return "boolean";
    }

    @Override
    public String javaValue() { return String.valueOf(this.value); }
}
