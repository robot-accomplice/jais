package jais.messages.nmea.rmc;

import jais.ByteArrayUtils;
import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;
import jais.messages.nmea.NMEASentenceBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMCTiming extends NMEASentenceBase {

    float fixTaken;
    EStatus status;
    String lat;
    char latDir;
    String lon;
    char lonDir;
    // String null1;
    // String null2;
    long date;
    // String null3;
    // String null4;
    EModeIndicator mode;
    String checksum;

    /**
     *
     * @param sentence String representation of a NMEA sentence
     */
    public RMCTiming(String sentence) {
        super.sentenceType = SentenceType.NMEA_RMC_TIMING;
        super.sentence = sentence.getBytes();
    }

    /**
     *
     * @param sentence byte[] representation of a NMEA sentence
     */
    public RMCTiming(byte[] sentence) {
        super.sentenceType = SentenceType.NMEA_RMC_TIMING;
        super.sentence = sentence;
    }

    /**
     *
     * @throws ParseException
     */
    @Override
    public void parse() throws ParseException {
        byte[][] fields = ByteArrayUtils.fastSplit(this.sentence, ',');

        // $GPRMC,123519.00,A,4807.038,N,01131.000,E,,,230394,,,A*6A
        String preamble = ByteArrayUtils.bArray2Str(fields[0]);

        if (!super.isValid()) {
            throw new ParseException(
                    String.format("Invalid RMCTiming message, %d fields, preamble: '%s'",
                            fields.length, preamble)
            );
        }

        this.fixTaken = Float.parseFloat(ByteArrayUtils.bArray2Str(fields[1]));
        this.status = EStatus.valueOf(ByteArrayUtils.bArray2Str(fields[2]));
        this.lat = ByteArrayUtils.bArray2Str(fields[3]);
        this.latDir = ByteArrayUtils.bArray2Str(fields[4]).toCharArray()[0];
        this.lon = ByteArrayUtils.bArray2Str(fields[5]);
        this.lonDir = ByteArrayUtils.bArray2Str(fields[6]).toCharArray()[0];
        this.date = Long.parseLong(ByteArrayUtils.bArray2Str(fields[9]));
        String tempStr = ByteArrayUtils.bArray2Str(fields[12]);
        String[] tempParts = ByteArrayUtils.fastSplit(tempStr, '*');
        this.mode = EModeIndicator.valueOf(tempParts[0]);
        this.checksum = tempParts[1];
    }
}
