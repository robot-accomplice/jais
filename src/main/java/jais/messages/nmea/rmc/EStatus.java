package jais.messages.nmea.rmc;

public enum EStatus {

    A("Active"),
    V("Void");

    private String description;

    private EStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
