package jais;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ByteArrayUtils {

    public final static Logger LOG = LogManager.getLogger(ByteArrayUtils.class);
    public final static Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    /**
     * Uses the DEFAULT_CHARSET of StandardCharsets.US_ASCII to decode a byte []
     * into a String
     *
     * @param bytes the byte [] to decode into a String
     * @return the String decoded from the provided byte array
     */
    public final static String bArray2Str(byte[] bytes) {
        return bArray2Str(bytes, DEFAULT_CHARSET);
    }

    /**
     * Decodes a byte [] into a String using the provided Charset
     *
     * @param bytes the byte[] to decode into a String
     * @param cs    the Charset that should be used to perform the decode operation
     * @return the String decoded from the provided byte []
     */
    public final static String bArray2Str(byte[] bytes, Charset cs) {
        return new String(bArray2cArray(bytes, cs));
    }

    /**
     * Encodes a String into a byte [] using the default Charset (US_ASCII)
     *
     * @param string the String to encode into a byte [] using the DEFAULT_CHARSET
     *               of StandardCharsets.US_ASCII
     * @return the byte [] encoded from the provided String
     */
    public final static byte[] str2bArray(String string) {
        return str2bArray(string, DEFAULT_CHARSET);
    }

    /**
     * Encodes a String into a byte array using the provided Charset
     *
     * @param s  the String to encode into a byte []
     * @param cs the Charset that should be used to perform the encode operation
     * @return the byte [] encoded from the provided String, using the provided
     *         Charset
     */
    public final static byte[] str2bArray(String s, Charset cs) {
        return cs.encode(s).array();
    }

    /**
     * Decodes a byte [] into a char [] using the default character set (US_ASCII)
     *
     * @param bytes the byte [] to decode into a char [] using the DEFAULT_CHARSET
     *              of StandardCharsets.US_ASCII
     * @return the char [] decoded from the provided byte []
     */
    public final static char[] bArray2cArray(byte[] bytes) {
        return bArray2cArray(bytes, DEFAULT_CHARSET);
    }

    /**
     * Decodes a byte [] into a char [] using the provided Charset
     *
     * @param bytes the byte [] to decode into a char []
     * @param cs    the Charset that should be used to perform the decode operation
     * @return the char [] decoded from the provided byte [] using the provided
     *         Charset
     */
    public final static char[] bArray2cArray(byte[] bytes, Charset cs) {
        return cs.decode(ByteBuffer.wrap(bytes)).array();
    }

    /**
     * Encodes a char [] into a byte [] using the provided Charset
     *
     * @param ca the char[] to encode into a byte[]
     * @param cs the Charset that should be used to perform the encode operation
     * @return the encoded byte [] from the provide char [] using the provided
     *         Charset
     */
    public final static byte[] cArray2bArray(char[] ca, Charset cs) {
        return cs.encode(CharBuffer.wrap(ca)).array();
    }

    /**
     * Encodes a char [] into a byte [] using the default Charset (US_ASCII)
     *
     * @param ca the char[] to encode into a byte[] using the DEFAULT_CHARSET of
     *           StandardCharsets.US_ASCII
     * @return the encoded byte [] from the provide char [] using the provided
     *         Charset
     */
    public final static byte[] cArray2bArray(char[] ca) {
        return cArray2bArray(ca, DEFAULT_CHARSET);
    }

    /**
     * Performs a String.trim()-like operation on a byte[] using the
     * StandardCharsets.US_ASCII Charset
     *
     * @param bytes the byte[] to be trimmed
     * @return the trimmed byte []
     */
    public final static byte[] trimByteArray(byte[] bytes) {
        return trimByteArray(bytes, DEFAULT_CHARSET);
    }

    /**
     * Performs a String.trim()-like operation on a byte[] using the specified
     * Charset
     *
     * @param bytes the byte[] to be trimmed
     * @param cs    the Charset to use in the conversion of the byte[] into a char[]
     * @return the trimmed byte []
     */
    public final static byte[] trimByteArray(byte[] bytes, Charset cs) {
        char[] chars = bArray2cArray(bytes, cs);

        for (int i = chars.length - 1; i > -1; i--) {
            if (LOG.isTraceEnabled())
                LOG.trace("Character at position {} is {}", i, chars[i]);

            switch (chars[i]) {
                case '\n':
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found newline");
                    break;
                case '\r':
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found carriage return");
                    break;
                case '\t':
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found tab");
                    break;
                case ' ':
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found space");
                    break;
                default:
                    if (LOG.isTraceEnabled())
                        LOG.trace("Found non-whitespace character {} at position {}", chars[i], i);
                    return Arrays.copyOfRange(bytes, 0, i + 1); // because the "to" value in Arrays.copyOfRange is
                                                                // EXclusive
            }
        }

        return bytes;
    }

    /**
     * Constructs a String object from a subset of a byte []
     *
     * @param bytes the byte array from which we want to extract a substring
     * @param start the starting index of our substring
     * @param end   the ending index of our substring
     * @return the substring decoded from the byte [] contained within the provided
     *         start index and end index of the provided byte array
     */
    public final static String substring(byte[] bytes, int start, int end) {
        return bArray2Str(Arrays.copyOfRange(bytes, start, end));
    }

    /**
     * Constructs a String object from a subset of a byte []
     *
     * @param bytes the byte array from which we want to extract a substring
     * @param start the starting index of our substring
     * @return the substring decoded from the byte [] contained within the provided
     *         start index and end index of the provided byte array
     */
    public final static String substring(byte[] bytes, int start) {
        return bArray2Str(Arrays.copyOfRange(bytes, start, bytes.length - 1));
    }

    /**
     * Attempts to convert a byte [] into an int
     *
     * @param bytes the byte array we want to convert to an int
     * @return an int representation of the provided byte []
     * @throws InvalidParameterException if the byte array is too short
     */
    public final static int getInt(byte[] bytes) throws InvalidParameterException {
        if (bytes.length < 4)
            throw new InvalidParameterException("The byte array is too short to represent an int");
        return ByteBuffer.wrap(bytes).getInt();
    }

    /**
     * Returns the index of the first occurrence of char c in byte [] ba or -1
     * the char is not present
     *
     * @param ba the byte array we want to search
     * @param c  the character for which we want the index
     * @return the first index of the provided char in the provided byte [] or -1 if
     *         the char does not exist within this byte []
     */
    public final static int indexOf(byte[] ba, char c) {
        char[] chars = DEFAULT_CHARSET.decode(ByteBuffer.wrap(ba)).array();
        for (int i = 0; i < chars.length; i++)
            if (chars[i] == c)
                return i;

        return -1;
    }

    /**
     * Splits the provided String by commas
     *
     * @param toSplit The String we wish to split
     * @return a String [] containing the split elements of the source String
     */
    public final static String[] fastSplit(String toSplit) {
        return fastSplit(toSplit, ',');
    }

    /**
     * An alternative to String.split() (which is a memory hog and performance
     * donkey at scale)
     *
     * @param toSplit   the byte [] you wish you to subdivide
     * @param delimiter the char that will be the basis for splitting
     * @return a byte [][] containing the segmentation of the provided byte [] based
     *         on the provided delimiter
     */
    public final static byte[][] fastSplit(byte[] toSplit, char delimiter) {
        if (toSplit == null)
            return null;

        int count = 1;
        for (byte value : toSplit)
            if (value == delimiter)
                count++;

        byte[][] array = new byte[count][];

        int a = -1;
        int b = 0;

        for (int i = 0; i < count; i++) {
            while (b < toSplit.length && toSplit[b] != delimiter)
                b++;

            array[i] = Arrays.copyOfRange(toSplit, a + 1, b);
            a = b;
            b++;
        }

        return array;
    }

    /**
     * An alternative to String.split() (which is a memory hog and performance
     * donkey at scale)
     *
     * @param toSplit   the String you wish to subdivide
     * @param delimiter the char that will be the basis of the subdivision
     * @return a String [] containing the segmented version of the provided String
     *         based on the provided char delimiter
     */
    public final static String[] fastSplit(String toSplit, char delimiter) {
        if (toSplit == null)
            return null;

        int count = 1;

        for (int i = 0; i < toSplit.length(); i++)
            if (toSplit.charAt(i) == delimiter)
                count++;

        String[] array = new String[count];

        int a = -1;
        int b = 0;

        for (int i = 0; i < count; i++) {
            while (b < toSplit.length() && toSplit.charAt(b) != delimiter)
                b++;

            array[i] = toSplit.substring(a + 1, b);
            a = b;
            b++;
        }

        return array;
    }

}
