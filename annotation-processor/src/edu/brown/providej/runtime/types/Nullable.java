package edu.brown.providej.runtime.types;

import edu.brown.providej.runtime.ProvideJUtil;

public class Nullable<T> {
    private T value;

    public Nullable() {
        this.value = null;
    }

    public Nullable(T value) {
        this.value = value;
    }

    public boolean isNull() {
        return this.value == null;
    }

    public T getValue() {
        if (this.isNull()) {
            throw new NullPointerException("Nullable value accessed while null!");
        }
        return this.value;
    }

    @Override
    public String toString() {
        if (this.value == null) {
            return "null";
        }
        return ProvideJUtil.toJSON(this.value);
    }
}
