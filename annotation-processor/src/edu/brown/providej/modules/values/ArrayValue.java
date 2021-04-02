package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ArrayType;
import edu.brown.providej.modules.types.ObjectType;

public class ArrayValue extends AbstractValue {
    private final AbstractValue[] values;
    private int index;

    public ArrayValue(ArrayType type) {
        super(type);
        this.values = new AbstractValue[type.size()];
        this.index = 0;
    }

    // Value manipulaton.
    public void addValue(AbstractValue value) {
        this.values[this.index] = value;
        this.index++;
    }

    @Override
    public String javaValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(this.type.javaType());
        builder.append("{");
        for (AbstractValue value : this.values) {
            builder.append(value.javaValue());
            builder.append(", ");
        }
        if (this.values.length > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("}");
        return builder.toString();
    }
}
