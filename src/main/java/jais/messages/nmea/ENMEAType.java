package jais.messages.nmea;

import lombok.Getter;

/**
 * 
 */
@Getter
public enum ENMEAType {
    GGA_TIMING("$GPGGA", 14, 11),
    GGA_NAVIGATIONAL("$GPGGA", 14, 9),
    GLL("$GPGGA", 8, -1),
    RMC_TIMING("$GPRMC", 13, 7),
    RMC_NAVIGATIONAL("$GPRMC", 13, 10),
    THS("$GPTHS", 4, -1),
    ZDA("$GPZDA", 6, -1);

    private String preamble;
    private int fieldCount;
    private int firstEmptyIndex;

    /**
     * @param preamble String
     */
    private ENMEAType(String preamble, int fieldCount, int firstEmptyIndex) {
        this.preamble = preamble;
    }

    /**
     * 
     * @return
     */
    public boolean hasEmptyIndex() {
        return firstEmptyIndex != -1;
    }
}
