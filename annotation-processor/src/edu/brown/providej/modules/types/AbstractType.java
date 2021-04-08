package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;

import java.util.ArrayList;

public abstract class AbstractType {
    // Use this (instead of instanceof) for specialization.
    private final Kind kind;

    protected AbstractType(Kind kind) {
        this.kind = kind;
    }

    // Getters / setters.
    public Kind getKind() {
        return this.kind;
    }

    // Transform a given value to this type.
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().equals(this)) {
            return value;
        }
        throw new IllegalArgumentException("Cannot generically transform " + value.getClass().getName() + " to " + this.toString());
    }

    // For debugging.
    @Override
    public String toString() {
        return this.javaType();
    }

    // Types must be translated to java types with string names.
    public abstract String javaType();
    public String javaRefType() {
        return this.javaType();
    }

    // Equality with other abstract types.
    @Override
    public boolean equals(Object other) {
        if (other instanceof AbstractType) {
            return this.equals((AbstractType) other);
        }
        return false;
    }
    public boolean equals(AbstractType other) {
        return this.kind == other.getKind();
    }

    // Supported types.
    public enum Kind {
        INT,
        LONG,
        STRING,
        OBJECT,
        BOOLEAN,
        DOUBLE,
        ARRAY,
        RUNTIME_TYPE,
        NULLABLE,
        OR,
        OPTIONAL
    }

    public static AbstractType unify(AbstractType t1, AbstractType t2) {
        // T + T ==> T
        if (t1.equals(t2)) {
            return t1;
        }

        // Unifying with optional is special: unified type remains optional with the inner data type unified.
        // Optional<T1> + T2 ===> Optional<T1>          if T1 == T2,
        //                        Optional<T1 + T2>     otherwise.
        // Optional is flat: cannot have Optional<Optional>.
        if (t1.getKind() == AbstractType.Kind.OPTIONAL) {
            return OptionalType.unify((OptionalType) t1, t2);
        }
        if (t2.getKind() == AbstractType.Kind.OPTIONAL) {
            return OptionalType.unify((OptionalType) t2, t1);
        }

        // Unifying with nullable is special: unified type remains nullable with the inner data type unified.
        // Nullable<T1> + T2 ===> Nullable<T1>          if T1 == T2,,
        //                        Nullable<T1>          if T2 == Null,
        //                        Nullable<T1 + T2>     otherwise.
        if (t1.getKind() == AbstractType.Kind.NULLABLE) {
            return NullableType.unify((NullableType) t1, t2);
        }
        if (t2.getKind() == AbstractType.Kind.NULLABLE) {
            return NullableType.unify((NullableType) t2, t1);
        }

        // Unifying with null makes a nullable!
        // T + Null ==> Nullable<T>
        if (t1.getKind() == AbstractType.Kind.RUNTIME_TYPE) {
            return RuntimeType.unify((RuntimeType) t1, t2);
        }
        if (t2.getKind() == AbstractType.Kind.RUNTIME_TYPE) {
            return RuntimeType.unify((RuntimeType) t2, t1);
        }

        // Unifying things of different kinds: use OrType.
        if (t1.getKind() != t2.getKind()) {
            return new OrType(t1, t2);
        }

        // T1 and t2 are not equal, but they have the same kind!
        // They cannot be booleans, doubles, ints, longs, or strings
        // as being of the same kind is sufficient for these types to be equal.
        // Hence, they can only be Array, Object, Or, Runtime.
        switch (t1.getKind()) {
            case OR:
                return new OrType(t1, t2);
            case ARRAY:
                return ArrayType.unify((ArrayType) t1, (ArrayType) t2);
            case OBJECT:
                return ObjectType.unify((ObjectType) t1, (ObjectType) t2);
            default:
                throw new IllegalStateException("AbstractType.unify: This should not be reachable!");
        }
    }
}
