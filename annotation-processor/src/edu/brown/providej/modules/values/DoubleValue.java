package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.DoubleType;

public class DoubleValue extends AbstractValue {
    double value;

    public DoubleValue(double value) {
        super(new DoubleType());
        this.value = value;
    }

    @Override
    public String javaValue() {
        return String.valueOf(this.value);
    }
}
