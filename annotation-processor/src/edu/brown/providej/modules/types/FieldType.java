package edu.brown.providej.modules.types;

public abstract class FieldType {
    // Use this (instead of instanceof) for specialization.
    private final Kind kind;

    protected FieldType(Kind kind) {
        this.kind = kind;
    }

    // Getters / setters.
    public Kind getKind() {
        return this.kind;
    }

    // Types must be translated to java types with string names.
    public abstract String toString();

    // Supported types.
    public enum Kind {
        INT,
        STRING
    }
}
