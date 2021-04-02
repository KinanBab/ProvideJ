package edu.brown.providej.modules.types;

public class DoubleType extends AbstractType {
    public DoubleType() {
        super(AbstractType.Kind.DOUBLE);
    }

    @Override
    public String javaType() {
        return "double";
    }
}
