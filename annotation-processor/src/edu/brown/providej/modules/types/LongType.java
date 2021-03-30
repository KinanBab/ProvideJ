package edu.brown.providej.modules.types;

public class LongType extends AbstractType {
    long value;

    public LongType(long value) {
        super(AbstractType.Kind.INT);
        this.value = value;
    }

    @Override
    public String javaType() {
        return "long";
    }

    @Override
    public String javaValue() { return String.valueOf(this.value); }
}
