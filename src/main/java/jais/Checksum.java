package jais;

public final class Checksum {

    private final static char CHECKSUM_DELIMITER = '*';

    int crc;

    /**
     * 
     * @param crc the integer CRC value to use in the creation of the object
     */
    private Checksum(int crc) {
        this.crc = crc;
    }

    /**
     * 
     * @param c the target Checksum object for the comparison
     * @return boolean indicating whether or not the passed in object is equal to the current Crc object
     */
    public boolean equals(Checksum c) {
        return c.getCRC() == this.crc;
    }

    /**
     * Generates a valid checksum based on the provided char []
     *
     * @param source the source char [] for which you wish to generate a checksum
     * @return a generated int checksum for the provided char []
     */
    public static Checksum generateChecksum(char[] source) {

        int crc = 0;
        for (char aSource : source)
            crc ^= aSource;

        return new Checksum(crc);
    }

    /**
     * Generates a valid checksum based on the provided String
     *
     * @param sourceString the source String from which you wish to generate a
     *                     checksum
     * @return a generated int checksum for the provided String
     */
    public static Checksum generateChecksum(String sourceString) {
        return generateChecksum(sourceString.toCharArray());
    }

    /**
     * Attempts to parse a checksum from the provided String and generates a new one
     * if the parsing operation is unsuccessful
     *
     * @param data the AIS packet string for which you wish to parse the checksum
     * @return the int checksum for the provided string
     */
    public static Checksum parse(String data) {
        int index = data.indexOf(String.valueOf(CHECKSUM_DELIMITER));
        if (index < 0) {
            index = data.length() - 1;
        }

        int crc = Integer.parseInt(data.substring(index), 16);

        return new Checksum(crc);
    }

    /**
     * 
     * @param data the data from which the checksum value must be parsed
     * @return the Checksum object that results from the successful parsing of the passed in data
     */
    public static Checksum parse(byte[] data) {
        int index = ByteArrayUtils.indexOf(data, CHECKSUM_DELIMITER);
        if (index < 0) {
            index = data.length - 1;
        }

        int crc = Integer.parseInt(ByteArrayUtils.substring(data, index), 16);

        return new Checksum(crc);
    }

    /**
     * Generates a checksum for the substring (based on int startFrom and int endAt
     * indices) of String genString
     *
     * @param genString the String for which you wish to generate a checksum
     * @param startFrom the int start index of the substring
     * @param endAt     the int end index of the substring
     * @return the int form of the checksum
     */
    public static Checksum generateChecksum(String genString, int startFrom, int endAt) {
        if (endAt <= startFrom || endAt > genString.length())
            return null;

        return generateChecksum(genString.substring(startFrom, endAt).toCharArray());
    }

    /**
     * Validates the provided checksum (byte [] packetChecksum) by generating a new
     * checksum for byte [] data and comparing them
     *
     * @param data           the byte [] to which the provided packetChecksum should
     *                       apply
     * @param packetChecksum a byte [] representation of the checksum to be
     *                       validated
     * @return a boolean representing the validity of the checksum
     */
    public static boolean validateChecksum(byte[] data, byte[] packetChecksum) {
        Checksum calcChecksum;
        Checksum pktChecksum;

        byte[] trimmed = ByteArrayUtils.trimByteArray(data);

        try {
            calcChecksum = parse(trimmed);
        } catch (NumberFormatException nfe) {
            // can't parse checksum
            return false;
        }

        try {
            pktChecksum = new Checksum(Integer.parseUnsignedInt(ByteArrayUtils.bArray2Str(packetChecksum), 16));
        } catch (NumberFormatException nfe) {
            // cannot generate checksum
            return false;
        }


        return pktChecksum.equals(calcChecksum);
    }

    /*
     * returns the crc value for the current Checksum object
     */
    public int getCRC() {
        return this.crc;
    }

    /**
     * @return hex String representation of the crc value
     */
    public String toHexString() {
        String hexString = Integer.toHexString(this.crc);
        hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
        return hexString;
    }
}
