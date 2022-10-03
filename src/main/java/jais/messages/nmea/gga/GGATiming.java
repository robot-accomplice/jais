package jais.messages.nmea.gga;

import jais.ByteArrayUtils;
import jais.Sentence;
import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GGATiming implements Sentence {

    private final SentenceType sentenceType = SentenceType.NMEA_GGA_TIMING;

    private String rawSentence;
    private float fixTaken;
    private float lat;
    private char latDir;
    private float lon;
    private char lonDir;
    private EFixQuality fixQuality;
    private int satellitesTracked;
    private float horizontalDilution;
    private float altitude;
    private String altitudeUnits;
    private float height;
    private String heightUnits;
    private char notUsed;
    private String checksum;

    /**
     * 
     * @param s
     * @throws ParseException
     */
    public GGATiming(String s) throws ParseException {
        this.rawSentence = s;
        this.parse();
    }

    /**
     * 
     */
    @Override
    public void parse() throws ParseException {
        String[] parts = ByteArrayUtils.fastSplit(this.rawSentence);

        if (parts.length != sentenceType.getFieldCount()) {
            throw new ParseException("Unable to parse NMEASentence. Insufficient fields.");
        } else if (parts[0].equals(sentenceType.getPreamble())) {
            throw new ParseException("Invalid preamble!");
        } else if (sentenceType.hasEmptyIndex() && !parts[sentenceType.getFirstEmptyIndex()].equals("")) {
            throw new ParseException(String.format("Field %d should be empty!", sentenceType.getFirstEmptyIndex()));
        } else {
            fixTaken = Float.parseFloat(parts[1]);
            lat = Float.parseFloat(parts[2]);
            latDir = parts[3].charAt(0);
            lon = Float.parseFloat(parts[4]);
            lonDir = parts[4].charAt(0);
            fixQuality = EFixQuality.values()[Integer.parseInt(parts[5])];
            satellitesTracked = Integer.parseInt(parts[6]);
            horizontalDilution = Float.parseFloat(parts[7]);
            altitude = Float.parseFloat(parts[8]);
            altitudeUnits = parts[9];
            height = Float.parseFloat(parts[10]);
            heightUnits = parts[11];
            checksum = parts[14];
        }
    }
}
