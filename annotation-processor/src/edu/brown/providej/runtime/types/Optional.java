package edu.brown.providej.runtime.types;

import edu.brown.providej.runtime.ProvideJUtil;

public class Optional<T> {
    private T value;

    public Optional() {
        this.value = null;
    }

    public Optional(T value) {
        this.value = value;
    }

    public boolean isDefined() {
        return this.value != null;
    }

    public T getValue() {
        if (!this.isDefined()) {
            throw new NullPointerException("Optional value accessed while undefined!");
        }
        return this.value;
    }

    @Override
    public String toString() {
        if (!this.isDefined()) {
            throw new NullPointerException("attempting to stringify undefined optional");
        }
        return ProvideJUtil.toJSON(this.value);
    }
}
