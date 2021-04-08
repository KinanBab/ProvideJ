package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.OptionalType;

public class OptionalValue extends AbstractValue {
    private AbstractValue value;

    public OptionalValue(AbstractType dataType) {
        super(new OptionalType(dataType));
        this.value = null;
    }

    public OptionalValue(AbstractValue value) {
        super(new OptionalType(value.getType()));
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