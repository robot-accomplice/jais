package jais.messages.nmea.gga;

import jais.ByteArrayUtils;
import jais.exceptions.ParseException;
import jais.messages.nmea.ENMEAType;
import jais.messages.nmea.NMEASentence;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GGANavigational implements NMEASentence {

    private final ENMEAType type = ENMEAType.GGA_NAVIGATIONAL;

    private String rawSentence;
    private float fixTaken;
    private float lat;
    private char latDir;
    private float lon;
    private char lonDir;
    private EFixQuality fixQuality;
    private int satellitesTracked;
    private float altitude;
    private String altitudeUnits;
    private char notUsed;
    private String checksum;

    /**
     * 
     * @param s
     * @throws ParseException
     */
    public GGANavigational(String s) throws ParseException {
        this.rawSentence = s;
        this.parse();
    }

    /**
     * 
     */
    @Override
    public void parse() throws ParseException {
        String[] parts = ByteArrayUtils.fastSplit(this.rawSentence);

        if (parts.length == type.getFieldCount() && parts[0].equals(type.getPreamble()) &&
                (!type.hasEmptyIndex() && !parts[type.getFirstEmptyIndex()].equals(""))) {
            fixTaken = Float.parseFloat(parts[1]);
            lat = Float.parseFloat(parts[2]);
            latDir = parts[3].charAt(0);
            lon = Float.parseFloat(parts[4]);
            lonDir = parts[4].charAt(0);
            fixQuality = EFixQuality.values()[Integer.parseInt(parts[5])];
            satellitesTracked = Integer.parseInt(parts[6]);
            altitude = Float.parseFloat(parts[8]);
            altitudeUnits = parts[9];
            checksum = parts[14];
        } else {
            throw new ParseException("Unable to parse NMEASentence. Insufficient fields.");
        }
    }
}