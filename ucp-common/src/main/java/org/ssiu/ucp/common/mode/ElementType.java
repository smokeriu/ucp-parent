package org.ssiu.ucp.common.mode;

public enum ElementType {

    Operator("operator"),

    Reader("reader"),

    Writer("writer");

    private final String simpleName;

    public String getSimpleName() {
        return simpleName;
    }

    ElementType(String simpleName) {
        this.simpleName = simpleName;
    }

    public static ElementType get(String name) {
        for (ElementType value : values()) {
            if (value.simpleName.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }
}
