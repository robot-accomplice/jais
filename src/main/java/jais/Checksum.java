package jais;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Checksum {

    private final static Logger LOG = LogManager.getLogger(Checksum.class);
    private final static char CHECKSUM_DELIMITER = '*';

    int crc;

    /**
     * 
     * @param crc
     */
    private Checksum(int crc) {
        this.crc = crc;
    }

    /**
     * 
     * @param c
     * @return
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
        if (LOG.isDebugEnabled())
            LOG.debug("Generating checksum for String \"{}\"", new String(source));

        int crc = 0;
        for (char aSource : source)
            crc ^= aSource;

        if (LOG.isDebugEnabled())
            LOG.debug("Generated CRC = {}(int)/{}(hex)", crc, Integer.toHexString(crc));

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
        if (LOG.isDebugEnabled())
            LOG.debug("Index: {}", index);
        if (index < 0) {
            index = data.length() - 1;
        }

        int crc = Integer.parseInt(data.substring(index), 16);

        return new Checksum(crc);
    }

    /**
     * 
     * @param data
     * @return
     */
    public static Checksum parse(byte[] data) {
        int index = ByteArrayUtils.indexOf(data, CHECKSUM_DELIMITER);
        if (LOG.isDebugEnabled())
            LOG.debug("Index: {}", index);
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
            LOG.debug("Generated checksum {}", calcChecksum.toHexString());
        } catch (NumberFormatException nfe) {
            if (LOG.isDebugEnabled())
                LOG.debug("Cannot produce a checksum from  \"{}\"", ByteArrayUtils.bArray2Str(trimmed));
            return false;
        }

        try {
            pktChecksum = new Checksum(Integer.parseUnsignedInt(ByteArrayUtils.bArray2Str(packetChecksum), 16));
        } catch (NumberFormatException nfe) {
            if (LOG.isInfoEnabled())
                LOG.info("Cannot parse \"{}\" into a valid int", ByteArrayUtils.bArray2Str(packetChecksum));
            return false;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Comparing: \"{}/{}\" to \"{}/{}\"", pktChecksum, ByteArrayUtils
                    .bArray2Str(packetChecksum).toUpperCase(),
                    calcChecksum, calcChecksum.toHexString());
            LOG.debug("\"{}\" is {} equal to \"{}\"", calcChecksum, (calcChecksum.equals(pktChecksum) ? "" : "not"),
                    pktChecksum);
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
