package edu.brown.providej.modules.types;

public class IntType extends AbstractType {

    public IntType() {
        super(AbstractType.Kind.INT);
    }

    @Override
    public String javaType() {
        return "int";
    }
}
