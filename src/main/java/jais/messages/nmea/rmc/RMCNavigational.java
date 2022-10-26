package jais.messages.nmea.rmc;

import jais.ByteArrayUtils;
import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;
import jais.messages.nmea.NMEASentenceBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMCNavigational extends NMEASentenceBase {

    float fixTaken;
    EStatus status;
    String lat;
    char latDir;
    String lon;
    char lonDir;
    float speed;
    String track;
    long date;
    EModeIndicator mode;
    ENavigationStatus navStatus;
    String checksum;

    /**
     * 
     * @param sentence String representation of a NMEA sentence
     */
    public RMCNavigational(String sentence) {
        super.sentenceType = SentenceType.NMEA_RMC_NAVIGATIONAL;
        super.sentence = sentence.getBytes();
    }

    /**
     *
     * @param sentence
     */
    public RMCNavigational(byte[] sentence) {
        super.sentenceType = SentenceType.NMEA_RMC_NAVIGATIONAL;
        super.sentence = sentence;
    }

    /**
     *
     * @throws ParseException
     */
    @Override
    public void parse() throws ParseException {
        byte[][] fields = ByteArrayUtils.fastSplit(this.sentence, ',');

        // $GPRMC,123519,A,4807.038,N,01131.000,E,0.022,269.131,230394,,,A,C*6A
        String preamble = ByteArrayUtils.bArray2Str(fields[0]);

        if (!super.isValid()) {
            throw new ParseException(
                    String.format("Invalid RMCNavigational message, %d fields, preamble: '%s'",
                            fields.length, preamble)
            );
        }

        this.fixTaken = Float.parseFloat(ByteArrayUtils.bArray2Str(fields[1]));
        this.status = EStatus.valueOf(ByteArrayUtils.bArray2Str(fields[2]));
        this.lat = ByteArrayUtils.bArray2Str(fields[3]);
        this.latDir = ByteArrayUtils.bArray2cArray(fields[4])[0];
        this.lon = ByteArrayUtils.bArray2Str(fields[5]);
        this.lonDir = ByteArrayUtils.bArray2cArray(fields[6])[0];
        this.speed = Float.parseFloat(ByteArrayUtils.bArray2Str(fields[7]));
        this.track = ByteArrayUtils.bArray2Str(fields[8]);
        this.date = Long.parseLong(ByteArrayUtils.bArray2Str(fields[9]));
        this.mode = EModeIndicator.valueOf(ByteArrayUtils.bArray2Str(fields[11]));
        this.navStatus = ENavigationStatus.valueOf(ByteArrayUtils.bArray2Str(fields[12]));
        this.checksum = ByteArrayUtils.bArray2Str(fields[13]);
    }
}
