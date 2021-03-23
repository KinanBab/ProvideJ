package edu.brown.providej.annotations.enums;

public enum Visibility {
    PACAKGE, PUBLIC, PRIVATE;

    @Override
    public String toString() {
        switch (this) {
            case PACAKGE:
                return "/* package */";
            case PUBLIC:
                return "public";
            case PRIVATE:
                return "private";
            default:
                throw new UnsupportedOperationException("Unsupported visibility value");
        }
    }
}
