package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.ObjectType;

import java.util.Map;
import java.util.TreeMap;

public class ObjectValue extends AbstractValue {
    private final TreeMap<String, AbstractValue> values;

    public ObjectValue(ObjectType type) {
        super(type);
        this.values = new TreeMap<>();
    }

    // Value manipulations.
    public void addValue(String fieldName, AbstractValue Value) {
        this.values.put(fieldName, Value);
    }

    public boolean hasValue(String fieldName) {
        return this.values.containsKey(fieldName);
    }

    public AbstractValue getValue(String fieldName) {
        return this.values.get(fieldName);
    }

    public TreeMap<String, AbstractValue> getValues() {
        return new TreeMap<>(values);
    }

    @Override
    public String javaValue() {
        StringBuilder builder = new StringBuilder();
        builder.append("new ");
        builder.append(this.type.javaType());
        builder.append("(");
        for (Map.Entry<String, AbstractValue> e : this.values.entrySet()) {
            builder.append(e.getValue().javaValue());
            builder.append(", ");
        }
        if (this.values.size() > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append(")");
        return builder.toString();
    }
}
