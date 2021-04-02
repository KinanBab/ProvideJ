package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.AbstractType;

public abstract class AbstractValue {
    protected final AbstractType type;

    protected AbstractValue(AbstractType type) {
        this.type = type;
    }

    // Getters / setters.
    public AbstractType.Kind getKind() {
        return this.type.getKind();
    }
    public AbstractType getType() {
        return this.type;
    }

    // Values must be translated to java code values (strings that when interpreted as Java code give the value).
    public abstract String javaValue();
}
