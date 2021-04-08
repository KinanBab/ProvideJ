package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;
import edu.brown.providej.modules.values.OptionalValue;

public class OptionalType extends AbstractType {
    private AbstractType dataType;

    public OptionalType(AbstractType dataType) {
        super(AbstractType.Kind.OPTIONAL);
        this.dataType = dataType;
        if (dataType.getKind() == AbstractType.Kind.OPTIONAL) {
            this.dataType = ((OptionalType) dataType).dataType;
        }
    }

    public AbstractType getDataType() {
        return this.dataType;
    }

    @Override
    public String toString() {
        return "Optional<" + this.dataType.toString() + ">";
    }

    @Override
    public String javaType() {
        return "edu.brown.providej.runtime.types.Optional<" + this.dataType.javaRefType() + ">";
    }

    @Override
    public boolean equals(AbstractType other) {
        if (other.getKind() == AbstractType.Kind.OPTIONAL) {
            return this.dataType.equals(((OptionalType) other).dataType);
        }
        return false;
    }

    public static AbstractType unify(OptionalType t1, AbstractType t2) {
        // Optional<T1> + Optional<T2> ==> Optional<Or<T1 + T2>>
        if (t2.getKind() == AbstractType.Kind.OPTIONAL) {
            AbstractType dataType = AbstractType.unify(t1.dataType, ((OptionalType) t2).dataType);
            return new OptionalType(dataType);
        }
        // Optional<T1> + T2 ==> Optional<T1 + T2>
        return new OptionalType(AbstractType.unify(t1.dataType, t2));
    }

    @Override
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().equals(this)) {
            return value;
        }
        return new OptionalValue(this.dataType.transform(value));
    }
}
