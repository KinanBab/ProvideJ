package edu.brown.providej.modules.types;

public class LongType extends AbstractType {
    public LongType() {
        super(AbstractType.Kind.LONG);
    }

    @Override
    public String javaType() {
        return "long";
    }
}
