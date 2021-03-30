package edu.brown.providej.modules.types;

import edu.brown.providej.modules.JsonSchema;

public class ArrayType extends AbstractType {
    private AbstractType unifiedType;
    private AbstractType[] concreteTypes;
    private int index;

    public ArrayType(int size) {
        super(AbstractType.Kind.ARRAY);
        this.index = 0;
        this.concreteTypes = new AbstractType[size];
        this.unifiedType = null;
    }

    public void addType(AbstractType type) {
        if (this.index == 0) {
            this.unifiedType = type;
        } else {
            // TODO(babman): unify types.
        }
        this.concreteTypes[this.index] = type;
        this.index++;
    }

    public AbstractType getUnifiedType() {
        return this.unifiedType;
    }

    @Override
    public String javaType() {
        return this.unifiedType.javaType() + "[]";
    }

    @Override
    public String javaValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(this.javaType());
        builder.append("{");
        for (AbstractType type : this.concreteTypes) {
            builder.append(type.javaValue());
            builder.append(", ");
        }
        if (this.concreteTypes.length > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("}");
        return builder.toString();
    }
}
