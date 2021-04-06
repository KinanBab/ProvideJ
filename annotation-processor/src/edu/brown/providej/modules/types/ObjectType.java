package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;
import edu.brown.providej.modules.values.NullableValue;
import edu.brown.providej.modules.values.ObjectValue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ObjectType extends AbstractType implements Iterable<Map.Entry<String, AbstractType>> {
    public static String Case(String name) {
        String upper = name.substring(0, 1).toUpperCase();
        return upper + name.substring(1);
    }

    private String[] context;
    private TreeMap<String, AbstractType> fields;

    public ObjectType() {
        super(AbstractType.Kind.OBJECT);
        this.fields = new TreeMap<>();
    }

    // Expose iterating over fields.
    @Override
    public Iterator<Map.Entry<String, AbstractType>> iterator() {
        return this.fields.entrySet().iterator();
    }

    public int size() {
        return this.fields.size();
    }

    // Getters and setters..
    public void setContext(String[] context) {
        this.context = Arrays.copyOf(context, context.length);
    }

    public String getBaseName() {
        return this.context[this.context.length - 1];
    }

    public String getQualifiedName() {
        if (this.context == null) {
            return "null";
        }
        return String.join("__", this.context);
    }

    // Field manipulations.
    public void addField(String field, AbstractType type) {
        this.fields.put(field, type);
    }

    public boolean hasField(String field) {
        return this.fields.containsKey(field);
    }

    public AbstractType getField(String field) {
        return this.fields.get(field);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("--" + getQualifiedName() + "--{");
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            builder.append(e.getKey());
            builder.append(": ");
            builder.append(e.getValue());
            builder.append(", ");
        }
        if (this.fields.size() > 0) {
            builder.setLength(builder.length() - 2);
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public String javaType() {
        return this.getQualifiedName();
    }

    @Override
    public boolean equals(AbstractType other) {
        if (other.getKind() == AbstractType.Kind.OBJECT) {
            ObjectType otherObj = (ObjectType) other;
            for (Map.Entry<String, AbstractType> e : this) {
                if (!otherObj.fields.containsKey(e.getKey()) || !otherObj.fields.get(e.getKey()).equals(e.getValue())) {
                    return false;
                }
            }
            for (Map.Entry<String, AbstractType> e : otherObj) {
                if (!this.fields.containsKey(e.getKey()) || !this.fields.get(e.getKey()).equals(e.getValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static AbstractType unify(ObjectType t1, ObjectType t2) {
        ObjectType unified = new ObjectType();
        for (String field : t1.fields.keySet()) {
            if (t2.fields.containsKey(field)) {
                unified.addField(field, AbstractType.unify(t1.fields.get(field), t2.fields.get(field)));
            } else {
                unified.addField(field, new NullableType(t1.fields.get(field)));
            }
        }
        for (String field : t2.fields.keySet()) {
            if (!t1.fields.containsKey(field)) {
                unified.addField(field, new NullableType(t2.fields.get(field)));
            }
        }
        return unified;
    }

    @Override
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().equals(this)) {
            return value;
        }

        if (value.getType().getKind() != AbstractType.Kind.OBJECT) {
            throw new IllegalArgumentException("Cannot transform " + value.getClass().getName() + " to " + this);
        }

        ObjectValue objValue = (ObjectValue) value;
        ObjectType objType = (ObjectType) value.getType();

        ObjectValue result = new ObjectValue(this);
        for (Map.Entry<String, AbstractValue> e : objValue.getValues().entrySet()) {
            if (!this.fields.containsKey(e.getKey())) {
                throw new IllegalArgumentException("Cannot ignore field " + e.getKey() + " during transformation to " + this);
            }
            result.addValue(e.getKey(), this.fields.get(e.getKey()).transform(e.getValue()));
        }
        for (Map.Entry<String, AbstractType> e : this.fields.entrySet()) {
            if (!objType.hasField(e.getKey())) {
                if (e.getValue().getKind() != AbstractType.Kind.NULLABLE) {
                    throw new IllegalArgumentException("Cannot populate field " + e.getKey() + " during transformation to " + this);
                }

                NullableType nullableType = (NullableType) e.getValue();
                result.addValue(e.getKey(), new NullableValue(nullableType.getDataType()));
            }
        }
        return result;
    }
}
