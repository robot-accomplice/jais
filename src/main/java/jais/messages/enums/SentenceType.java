package jais.messages.enums;

import lombok.Getter;

/**
 * 
 */
@Getter
public enum SentenceType {
    NMEA_AIS("!", 7, -1),
    NMEA_GGA_TIMING("$GPGGA", 14, 11),
    NMEA_GGA_NAVIGATIONAL("$GPGGA", 14, 9),
    NMEA_GLL("$GPGGA", 8, -1),
    NMEA_RMC_TIMING("$GPRMC", 13, 7),
    NMEA_RMC_NAVIGATIONAL("$GPRMC", 13, 10),
    NMEA_THS("$GPTHS", 4, -1),
    NMEA_ZDA("$GPZDA", 6, -1),
    UNKNOWN("", -1, -1);

    private String preamble;
    private int fieldCount;
    private int firstEmptyIndex;

    /**
     * @param preamble String
     */
    private SentenceType(String preamble, int fieldCount, int firstEmptyIndex) {
        this.preamble = preamble;
    }

    /**
     * 
     * @return
     */
    public boolean hasEmptyIndex() {
        return firstEmptyIndex != -1;
    }

    /**
     * 
     * @param preamble
     * @return
     */
    public SentenceType fromPreamble(String preamble) {
        // is preamble empty?
        // if preamble starts with a ! and exists in AISMessageType return NMEA_AIS
        // otherwise, loop through remaining types and return the one that matches
        // otherwise return UNKNOWN
        return UNKNOWN;
    }
}
