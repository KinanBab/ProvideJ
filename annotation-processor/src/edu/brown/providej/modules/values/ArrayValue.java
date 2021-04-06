package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;
import edu.brown.providej.modules.types.ArrayType;

import java.util.Arrays;
import java.util.Iterator;

public class ArrayValue extends AbstractValue implements Iterable<AbstractValue> {
    private final AbstractValue[] values;
    private int index;

    public ArrayValue(ArrayType type, int size) {
        super(type);
        this.values = new AbstractValue[size];
        this.index = 0;
    }

    // Value manipulation.
    public void addValue(AbstractValue value) {
        this.values[this.index] = value;
        this.index++;
    }

    @Override
    public Iterator<AbstractValue> iterator() {
        return Arrays.stream(values).iterator();
    }

    @Override
    public String javaValue() {
        ArrayType arrayType = (ArrayType) this.type;

        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(arrayType.javaTypeConstructor());
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

    // Change the inner values so that they are all of the same unified type as the corresponding array.
    public ArrayValue conformToUnifiedType(ArrayType type) {
        ArrayValue value = new ArrayValue(type, this.values.length);
        AbstractType dataType = type.getDataType();
        for (int i = 0; i < values.length; i++) {
            value.addValue(dataType.transform(this.values[i]));
        }
        return value;
    }
}
