package jais.messages.nmea.rmc;

public class RMCTiming {

    private static String preamble = "$GPRMC";

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

    // Timing we only want the constructor to be invoked as part of a factory
    private RMCTiming() {
    }

    public static RMCTiming parse(String message) {
        String[] fields = message.split(",");

        if (message.length() != 14) {
            // uh oh
        }

        RMCTiming timingObj = new RMCTiming();
        timingObj.fixTaken = Float.parseFloat(fields[1]);
        timingObj.status = EStatus.valueOf(fields[2]);
        timingObj.lat = fields[3];
        timingObj.lat_dir = fields[4].toCharArray()[0];
        timingObj.lon = fields[5];

        return timingObj;
    }

}
