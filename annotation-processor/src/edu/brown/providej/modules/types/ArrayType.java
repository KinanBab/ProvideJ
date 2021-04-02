package edu.brown.providej.modules.types;

import edu.brown.providej.modules.JsonSchema;

public class ArrayType extends AbstractType {
    private AbstractType dataType;
    private AbstractType[] concreteTypes;
    private int index;

    public ArrayType(int size) {
        super(AbstractType.Kind.ARRAY);
        this.index = 0;
        this.concreteTypes = new AbstractType[size];
        this.dataType = null;
    }

    public void addType(AbstractType type) {
        if (this.index == 0) {
            this.dataType = type;
        } else {
            // TODO(babman): unify types.
        }
        this.concreteTypes[this.index] = type;
        this.index++;
    }

    public AbstractType getDataType() {
        return this.dataType;
    }

    public int size() {
        return this.concreteTypes.length;
    }

    @Override
    public String javaType() {
        return this.dataType.javaType() + "[]";
    }
}
