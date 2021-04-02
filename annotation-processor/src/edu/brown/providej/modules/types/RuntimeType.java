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

    Types type;

    public RuntimeType(Types type) {
        super(AbstractType.Kind.RUNTIME_TYPE);
        this.type = type;
    }

    public Types getRuntimeType() {
        return type;
    }

    @Override
    public String javaType() {
        return this.type.toString();
    }
}
