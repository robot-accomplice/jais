package jais.messages.nmea.rmc;

import jais.Sentence;
import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;
import lombok.Getter;

@Getter
public class RMCTiming implements Sentence {

    private final SentenceType sentenceType = SentenceType.NMEA_RMC_TIMING;

    float fixTaken;
    EStatus status;
    String lat;
    char lat_dir;
    String lon;
    char lon_dir;
    // String null1;
    // String null2;
    long date;
    // String null3;
    // String null4;
    EModeIndicator mode;
    String checksum;
    String rawMessage;

    // Timing we only want the constructor to be invoked as part of a factory
    private RMCTiming() {
    }

    public void parse() throws ParseException {
        String[] fields = rawMessage.split(",");

        if (fields.length != 14) {
            // uh oh
        }

        RMCTiming timingObj = new RMCTiming();
        timingObj.fixTaken = Float.parseFloat(fields[1]);
        timingObj.status = EStatus.valueOf(fields[2]);
        timingObj.lat = fields[3];
        timingObj.lat_dir = fields[4].toCharArray()[0];
        timingObj.lon = fields[5];
    }
}
