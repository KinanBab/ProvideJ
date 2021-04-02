package edu.brown.providej.modules.types;

public class StringType extends AbstractType {
    public StringType() {
        super(AbstractType.Kind.STRING);
    }

    @Override
    public String javaType() {
        return "String";
    }
}
