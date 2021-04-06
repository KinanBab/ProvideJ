package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.NullableType;
import edu.brown.providej.modules.types.OrType;

public class OrValue extends AbstractValue {
    private AbstractValue value;

    public OrValue(OrType orType, AbstractValue value) {
        super(orType);
        this.value = value;
        if (!orType.accepts(value.getType())) {
            throw new IllegalArgumentException("OrValue is not accepted by OrType");
        }
    }

    public AbstractValue getValue() {
        return value;
    }

    @Override
    public String javaValue() {
        return "new " + this.type.javaType() + "(" + this.value.javaValue() + ")";
    }
}
