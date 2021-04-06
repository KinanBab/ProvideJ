package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;
import edu.brown.providej.modules.values.NullableValue;

public class NullableType extends AbstractType {
    private AbstractType dataType;

    public NullableType(AbstractType dataType) {
        super(AbstractType.Kind.NULLABLE);
        this.dataType = dataType;
        if (dataType.getKind() == AbstractType.Kind.NULLABLE) {
            this.dataType = ((NullableType) dataType).dataType;
        }
    }

    public AbstractType getDataType() {
        return this.dataType;
    }

    @Override
    public String toString() {
        return "Nullable<" + this.dataType.toString() + ">";
    }

    @Override
    public String javaType() {
        return "Nullable<" + this.dataType.javaRefType() + ">";
    }

    @Override
    public boolean equals(AbstractType other) {
        if (other.getKind() == AbstractType.Kind.NULLABLE) {
            return this.dataType.equals(((NullableType) other).dataType);
        }
        return false;
    }

    public static AbstractType unify(NullableType t1, AbstractType t2) {
        // Nullable<T1> + Nullable<T2> ==> Nullable<T1 + T2>
        if (t2.getKind() == AbstractType.Kind.NULLABLE) {
            AbstractType dataType = AbstractType.unify(t1.dataType, ((NullableType) t2).dataType);
            return new NullableType(dataType);
        }
        // Nullable<T1> + Null ==> Nullable<T1>
        if (t2.getKind() == AbstractType.Kind.RUNTIME_TYPE) {
            RuntimeType rt = (RuntimeType) t2;
            if (rt.getRuntimeType() == RuntimeType.Types.NULL) {
                return t1;
            }
        }
        // Nullable<T1> + T2 ==> Nullable<T1 + T2>
        return new NullableType(AbstractType.unify(t1.dataType, t2));
    }

    @Override
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().equals(this)) {
            return value;
        }
        if (value.getType().equals(new RuntimeType(RuntimeType.Types.NULL))) {
            return new NullableValue(this);
        }
        return new NullableValue(this.dataType.transform(value));
    }
}
