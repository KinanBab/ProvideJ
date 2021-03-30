package edu.brown.providej.types;

public class Nullable<T> {
    T value;

    public Nullable() {
        this.value = null;
    }

    public Nullable(T value) {
        this.value = value;
    }

    boolean isNull() {
        return this.value == null;
    }

    T getValue() {
        if (this.isNull()) {
            throw new NullPointerException("Nullable value accessed while null!");
        }
        return this.value;
    }
}
