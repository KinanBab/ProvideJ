package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.IntType;

public class IntValue extends AbstractValue {
    int value;

    public IntValue(int value) {
        super(new IntType());
        this.value = value;
    }

    @Override
    public String javaValue() {
        return String.valueOf(this.value);
    }
}
