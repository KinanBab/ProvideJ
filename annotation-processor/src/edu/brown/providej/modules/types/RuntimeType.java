package edu.brown.providej.modules.types;

public class RuntimeType extends AbstractType {
    public enum Types {
        EMPTY_ARRAY, EMPTY_OBJECT, NULL;

        @Override
        public String toString() {
            switch (this) {
                case EMPTY_ARRAY:
                    return "EmptyArray";
                case EMPTY_OBJECT:
                    return "EmptyObject";
                case NULL:
                    return "Null";
                default:
                    throw new UnsupportedOperationException("Unknown  runtime type");
            }
        }
    }

    RuntimeType.Types type;

    public RuntimeType(RuntimeType.Types type) {
        super(AbstractType.Kind.RUNTIME_TYPE);
        this.type = type;
    }

    @Override
    public String javaType() {
        return this.type.toString();
    }

    @Override
    public String javaValue() { return "new " + this.type + "()"; }
}
