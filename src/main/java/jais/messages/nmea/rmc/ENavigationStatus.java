package jais.messages.nmea.rmc;

public enum ENavigationStatus {

    S("Safe"),
    C("Caution"),
    U("Unsafe"),
    V("Void");

    private String description;

    private ENavigationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
