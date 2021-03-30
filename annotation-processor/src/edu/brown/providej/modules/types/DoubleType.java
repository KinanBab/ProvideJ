package edu.brown.providej.modules.types;

public class DoubleType extends AbstractType {
    double value;

    public DoubleType(double value) {
        super(AbstractType.Kind.DOUBLE);
        this.value = value;
    }

    @Override
    public String javaType() {
        return "double";
    }

    @Override
    public String javaValue() { return String.valueOf(this.value); }
}
