package jais.messages.nmea.rmc;

import jais.ByteArrayUtils;
import jais.exceptions.ParseException;
import jais.messages.nmea.ENMEAType;
import jais.messages.nmea.NMEASentence;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RMCNavigational implements NMEASentence {

    ENMEAType type = ENMEAType.RMC_NAVIGATIONAL;
    float fixTaken;
    EStatus status;
    String lat;
    char lat_dir;
    String lon;
    char lon_dir;
    float speed;
    String track;
    long date;
    // String null3;
    // String null4;
    EModeIndicator mode;
    ENavigationStatus nav_status;
    String checksum;
    byte[] sentence;

    /**
     * 
     * @param sentence String representation of a NMEA sentence
     */
    public RMCNavigational(String sentence) {
        this.sentence = sentence.getBytes();
    }

    public RMCNavigational(byte[] sentence) {
        this.sentence = sentence;
    }

    @Override
    public void parse() throws ParseException {
        byte[][] fields = ByteArrayUtils.fastSplit(this.sentence, ',');

        // this.preamble = "";
    }
}
