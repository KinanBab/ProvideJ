package edu.brown.providej.modules.types;

import edu.brown.providej.modules.values.AbstractValue;
import edu.brown.providej.modules.values.OrValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class OrType extends AbstractType {
    private ArrayList<AbstractType> options;

    public OrType(AbstractType t1, AbstractType t2) {
        super(AbstractType.Kind.OR);
        this.options = new ArrayList<>();
        this.addOrUnify(t1);
        this.addOrUnify(t2);
        if (this.options.size() < 2) {
            throw new IllegalArgumentException("OrType must have at least two options");
        }
    }

    // Initialzation: OrType are always flat: they never have OrType direct children.
    private int findIndexOfKind(AbstractType.Kind kind) {
        for (int i = 0; i < this.options.size(); i++) {
            if (this.options.get(i).getKind() == kind) {
                return i;
            }
        }
        return -1;
    }

    private void addOrUnify(AbstractType t) {
        // t already in arr, skip.
        for (AbstractType option : this.options) {
            if (option.equals(t)) {
                return;
            }
        }

        // t is an object or array, if arr contains a similar object or array we unify, otherwise we add.
        if (t.getKind() == AbstractType.Kind.OBJECT || t.getKind() == AbstractType.Kind.ARRAY) {
            int i = this.findIndexOfKind(t.getKind());
            if (i > -1) {
                this.options.set(i, AbstractType.unify(this.options.get(i), t));
            } else {
                this.options.add(t);
            }
            return;
        }
        if (t.getKind() == AbstractType.Kind.NULLABLE) {
            this.addOrUnify(new RuntimeType(RuntimeType.Types.NULL));
            this.addOrUnify(((NullableType) t).getDataType());
            return;
        }
        if (t.getKind() == Kind.OR) {
            for (AbstractType flat : ((OrType) t).options) {
                this.addOrUnify(flat);
            }
            return;
        }
        // t is a simple type: Boolean, Double, Int, Long, Runtime, String.
        // furthermore, an equivalent type does not exist in the options list.
        this.options.add(t);
    }

    // Getters, lookups.
    public AbstractType[] getOptions() {
        return options.toArray(new AbstractType[0]);
    }

    public boolean accepts(AbstractType type) {
        for (AbstractType t : this.options) {
            if (t.equals(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (AbstractType type : this.options) {
            builder.append(type);
            builder.append(" | ");
        }
        if (this.options.size() > 0) {
            builder.setLength(builder.length() - 3);
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public String javaType() {
        StringBuilder builder = new StringBuilder();
        builder.append("Object/*<");
        for (int i = 0; i < this.options.size(); i++) {
            builder.append(this.options.get(i).javaRefType());
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(">*/");
        return builder.toString();
    }

    @Override
    public boolean equals(AbstractType other) {
        if (other.getKind() == AbstractType.Kind.OR) {
            HashSet<AbstractType> u1 = new HashSet<>(this.options);
            HashSet<AbstractType> u2 = new HashSet<>(((OrType) other).options);
            return u1.equals(u2);
        }
        return false;
    }

    @Override
    public AbstractValue transform(AbstractValue value) {
        if (value.getType().equals(this)) {
            return value;
        }
        // OrType and OrValue should be flat.
        if (value.getType().getKind() == AbstractType.Kind.OR) {
            OrValue orValue = (OrValue) value;
            value = orValue.getValue();
        }
        // Can only transform if the type of value is one of the types of this OrType.
        int index = findIndexOfKind(value.getKind());
        if (index == -1) {
            throw new IllegalArgumentException(value.getType() + " is incompatible with OrType " + this);
        }

        AbstractType targetTypeInOr = this.options.get(index);
        return new OrValue(this, targetTypeInOr.transform(value));
    }
}
