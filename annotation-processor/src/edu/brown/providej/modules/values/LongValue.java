package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.LongType;

public class LongValue extends AbstractValue {
    long value;

    public LongValue(long value) {
        super(new LongType());
        this.value = value;
    }

    @Override
    public String javaValue() {
        return String.valueOf(this.value);
    }
}
