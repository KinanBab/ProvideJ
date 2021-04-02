package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.BooleanType;

public class BooleanValue extends AbstractValue {
    private boolean value;

    public BooleanValue(boolean value) {
        super(new BooleanType());
        this.value = value;
    }

    @Override
    public String javaValue() {
        return String.valueOf(this.value);
    }
}
