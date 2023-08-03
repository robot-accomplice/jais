package jais.messages.enums;

/**
 * Distinguishes between class A and class B vessels in a type safe way
 */
public enum VesselClass {
    UNSPECIFIED("Class Unspecified"),
    A("Class A"),
    B("Class B");

    private final String nameStr;

    VesselClass(String nameStr) {
        this.nameStr = nameStr;
    }

    public String toString() {
        return this.nameStr;
    }
}
