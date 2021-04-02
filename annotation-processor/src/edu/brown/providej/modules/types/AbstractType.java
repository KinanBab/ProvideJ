package edu.brown.providej.modules.types;

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

    // Types must be translated to java types with string names.
    public abstract String javaType();

    // Supported types.
    public enum Kind {
        INT,
        LONG,
        STRING,
        OBJECT,
        BOOLEAN,
        DOUBLE,
        ARRAY,
        RUNTIME_TYPE
    }
}
