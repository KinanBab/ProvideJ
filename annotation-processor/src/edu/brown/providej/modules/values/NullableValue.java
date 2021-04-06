package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.NullableType;

public class NullableValue extends AbstractValue {
    private AbstractValue value;

    public NullableValue(AbstractType dataType) {
        super(new NullableType(dataType));
        this.value = null;
    }

    public NullableValue(AbstractValue value) {
        super(new NullableType(value.getType()));
        this.value = value;
    }

    public boolean hasValue() {
        return this.value != null;
    }

    public AbstractValue getValue() {
        return this.value;
    }

    @Override
    public String javaValue() {
        if (this.value == null) {
            return "new " + this.type.javaType() + "()";
        } else {
            return "new " + this.type.javaType() + "(" + this.value.javaValue() + ")";
        }
    }
}