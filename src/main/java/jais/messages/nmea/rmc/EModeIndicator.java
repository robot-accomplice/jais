package jais.messages.nmea.rmc;

public enum EModeIndicator {
    A("Autonomous"),
    D("Differential"),
    E("Estimated"),
    F("Float RTK"),
    M("Manual input"),
    N("No fix"),
    P("Precise"),
    R("Real time kinematic"),
    S("Simulator");

    private String description;

    private EModeIndicator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
