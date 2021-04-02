package edu.brown.providej.modules.values;

import edu.brown.providej.modules.types.ObjectType;
import edu.brown.providej.modules.types.RuntimeType;

public class RuntimeValue extends AbstractValue {
    public RuntimeValue(RuntimeType type) {
        super(type);
    }

    @Override
    public String javaValue() {
        return "new " + this.type.javaType() + "()";
    }
}
