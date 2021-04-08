package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;
import edu.brown.providej.modules.values.ArrayValue;
import edu.brown.providej.runtime.types.Nullable;

public class ArrayType extends AbstractType {
    private AbstractType dataType;

    public ArrayType() {
        super(AbstractType.Kind.ARRAY);
        this.dataType = null;
    }

    public void addType(AbstractType type) {
        if (this.dataType == null) {
            this.dataType = type;
        } else {
            this.dataType = AbstractType.unify(this.dataType, type);
        }
    }

    public AbstractType getDataType() {
        return this.dataType;
    }

    @Override
    public String toString() {
        return "[" + this.dataType.toString() + "]";
    }

    @Override
    public String javaType() {
        return this.dataType.javaType() + "[]";
    }

    public String javaTypeConstructor() {
        if (this.dataType.getKind() == AbstractType.Kind.NULLABLE) {
            return "Nullable[]";
        } else if (this.dataType.getKind() == AbstractType.Kind.OPTIONAL) {
            return "edu.brown.providej.runtime.types.Optional[]";
        } else {
            return this.javaType();
        }
    }

    @Override
    public boolean equals(AbstractType other) {
        if (other.getKind() == AbstractType.Kind.ARRAY) {
            return this.dataType.equals(((ArrayType) other).dataType);
        }
        return false;
    }

    public static AbstractType unify(ArrayType t1, ArrayType t2) {
        ArrayType unified = new ArrayType();
        unified.addType(AbstractType.unify(t1, t2));
        return unified;
    }

    @Override
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().getKind() != AbstractType.Kind.ARRAY) {
            throw new IllegalArgumentException("Cannot transform " + value.getClass().getName() + " to " + this);
        }

        ArrayValue arrayValue = (ArrayValue) value;
        if (arrayValue.getType().equals(this)) {
            return value;
        }
        return arrayValue.conformToUnifiedType(this);
    }
}
