/*
 * Copyright 2016-2019 Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.InvalidAISCharacterException;
import jais.exceptions.AISException;
import jais.messages.AISMessage.AISFieldMap;
import jais.messages.enums.AISMessageType;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class AISMessageDecoder {

    private final static Logger LOG = LogManager.getLogger(AISMessageDecoder.class);

    public final static int CHAR_RANGE_A_MIN = 48;
    public final static int CHAR_RANGE_A_MAX = 87;
    public final static int CHAR_RANGE_B_MIN = 96;
    public final static int CHAR_RANGE_B_MAX = 119;

    /**
     *
     * @param rawMessage a byte array containing the raw message we intend to decode
     * @return a BitSet representing the decoded message
     */
    public static BitSet byteArrayToBitSet(byte[] rawMessage) {
        return byteArrayToBitSet(rawMessage, AISPacket.DEFAULT_CHARSET);
    }

    /**
     *
     * @param rawMessage a byte array containing the raw message we intend to decode
     * @param charset    the CharacterSet we want to use to perform the decoding
     * @return a BitSet representing the decoded message
     */
    public static BitSet byteArrayToBitSet(byte[] rawMessage, Charset charset) {

        char[] msgChars = charset.decode(ByteBuffer.wrap(rawMessage)).array();

        if (LOG.isDebugEnabled())
            LOG.debug("8 bit char array is {} bytes long.", rawMessage.length);

        BitSet bits = new BitSet(8 * rawMessage.length / 6);
        // boolean out[] = new boolean[6 * in.length];
        if (LOG.isDebugEnabled())
            LOG.debug("6 bit boolean array is {} bits long.", bits.size());

        int bIndex = 0;
        for (char c : msgChars) {
            try {
                int oc = encodedToSixBitInt(c); // pull the current raw message char
                if (oc == -1) {
                    LOG.info("Invalid character: '{}'", c);
                    bits.clear();
                    break;
                } else {
                    for (int bPos = 5; bPos >= 0; bPos--)
                        bits.set(bIndex++, 0 < (oc & (1 << bPos)));
                }
            } catch (InvalidAISCharacterException iace) {
                LOG.info("Encountered an invalid character (possible message padding?) : {}", iace.getMessage());
            }
        }

        return bits;
    }

    /**
     *
     * @param rawMessage the String we want to convert to a BitSet
     * @return a BitSet representing the contents of the String
     */
    private static BitSet stringToBitSet(String rawMessage) {

        char[] msgChars = rawMessage.toCharArray();
        if (LOG.isDebugEnabled())
            LOG.debug("8 bit char array is {} bytes long.", msgChars.length);

        BitSet bits = new BitSet(8 * msgChars.length / 6);
        if (LOG.isDebugEnabled())
            LOG.debug("6 bit boolean array is {} bits long.", bits.size());

        int bIndex = 0;
        for (char c : msgChars) {
            try {
                int oc = encodedToSixBitInt(c); // pull the current raw message char
                if (oc == -1) {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Invalid character: '{}'", c);
                    bits.clear();
                    break;
                } else {
                    for (int bPos = 5; bPos >= 0; bPos--)
                        bits.set(bIndex++, 0 < (oc & (1 << bPos)));
                }
            } catch (InvalidAISCharacterException iace) {
                if (LOG.isDebugEnabled())
                    LOG.debug("Encountered an invalid character (possible message padding?) : {}", iace.getMessage());
                break;
            }
        }

        return bits;
    }

    /**
     *
     * @param c the character we want to encode to a six bit int
     * @return the six bit int representation of the provided character
     * @throws InvalidAISCharacterException if the character is out of bounds
     */
    private static int encodedToSixBitInt(char c) throws InvalidAISCharacterException {
        if (c <= CHAR_RANGE_A_MAX && c >= CHAR_RANGE_A_MIN)
            return c - CHAR_RANGE_A_MIN;
        else if (c <= CHAR_RANGE_B_MAX && c >= CHAR_RANGE_B_MIN)
            return c - CHAR_RANGE_B_MIN + (CHAR_RANGE_A_MAX - CHAR_RANGE_A_MIN + 1);
        else
            throw new InvalidAISCharacterException(
                    "Character \'" + c + "\' is outside of either of the acceptable ranges.");
    }

    /**
     *
     * @param bits     the bitset to decode
     * @param startBit the starting bit from which we want to begin decoding
     * @param endBit   the last bit to be included in our decoding
     * @return an unsigned int we have decoded from the subset of the BitSet
     */
    public static int decodeUnsignedInt(BitSet bits, int startBit, int endBit) {
        int rval = 0;

        if (endBit > bits.size())
            throw new ArrayIndexOutOfBoundsException(
                    "DecodeInt: position " + endBit + " exceeds input array length " + bits.size());

        int binPosValue = 1;
        for (int i = endBit; i >= startBit; i--) {
            if (bits.get(i))
                rval += binPosValue;
            binPosValue += binPosValue; // double binPosValue to produce valid binary position value as per binary math
        }

        return rval;
    }

    /**
     *
     * @param b        the BitSet we want to use to decode the draught
     * @param startBit The starting bit of the draught data
     * @param endBit   the ending bit of the draught data
     * @return a float representing the draught
     */
    public static float decodeDraught(BitSet b, int startBit, int endBit) {
        int intVal = decodeUnsignedInt(b, startBit, endBit);
        return intVal / 10.f;
    }

    /**
     * decode integers in twos-complement form
     *
     * @param bits     the BitSet containing our signed int
     * @param startBit the starting bit where our int is located
     * @param endBit   the ending bit that marks the end of our int data
     * @return the int
     */
    public static int decodeSignedInt(BitSet bits, int startBit, int endBit) {
        int intValue = 0;

        boolean negative = bits.get(startBit);

        if (negative) {
            int binPosValue = 1;
            for (int i = endBit; i >= startBit; i--) {
                if (!bits.get(i))
                    intValue += binPosValue;

                // double binPosValue to produce valid
                // binary position value as per binary math
                binPosValue += binPosValue;
            }
            intValue = -(intValue + 1); // correct for dropped bit and add negative
        } else
            intValue = decodeUnsignedInt(bits, (startBit + 1), endBit);

        return intValue;
    }

    /**
     *
     * @param c the int representing the six bit character we want to convert to
     *          ASCII
     * @return the ASCII character
     * @throws AISException if the provided int value is out of bounds
     */
    private static char sixBitIntToAscii(int c) throws AISException {
        int rval = c;

        if (c < 0 || c > 63)
            throw new AISException("sixBitIntToAscii: Invalid input for 6-bit conversion: " + c);
        else if (c < 32)
            rval += 64;

        return (char) rval;
    }

    /**
     *
     * @param packets the collection of packets from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     * @throws AISException if we are unable to concatenate the provided packets or
     *                      if the decode operation fails
     */
    public static Optional<AISMessageType> decodeMessageType(AISPacket... packets) throws AISException {
        // concatenate full raw message from all packets
        return decodeMessageType(AISPacket.concatenate(packets));
    }

    /**
     *
     * @param rawMessage the String from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     * @throws AISException if the decode operation fails
     */
    public static Optional<AISMessageType> decodeMessageType(String rawMessage) throws AISException {
        return decodeMessageType(stringToBitSet(rawMessage));
    }

    /**
     *
     * @param rawMessage the byte array from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     * @throws AISException if the decode operation fails
     */
    public static Optional<AISMessageType> decodeMessageType(byte[] rawMessage) throws AISException {
        return decodeMessageType(byteArrayToBitSet(rawMessage));
    }

    /**
     *
     * @param bits the BitSet containing the message we wish to decode
     * @return an Optional which may contain the AISMessageType
     * @throws AISException if we are unable to decode the message
     */
    public static Optional<AISMessageType> decodeMessageType(BitSet bits) throws AISException {

        if (bits.size() < AISFieldMap.TYPE.getEndBit())
            throw new AISException("BitSet is too short: " + bits.size());

        if (LOG.isDebugEnabled())
            LOG.debug("BitSet Size: {}, Start Bit: {}, End Bit: {}", bits.size(),
                    AISFieldMap.TYPE.getStartBit(), AISFieldMap.TYPE.getEndBit());

        int typeId = decodeUnsignedInt(bits, AISFieldMap.TYPE.getStartBit(), AISFieldMap.TYPE.getEndBit());

        if (typeId == 0 || AISMessageType.fetchById(typeId) == null)
            return Optional.empty();

        return Optional.of(AISMessageType.fetchById(typeId));
    }

    /**
     *
     * @param b        the bitset which contains the latitude we want to decode
     * @param startBit the starting bit where the latitude data is located
     * @param endBit   the ending bit where the latitude data is located
     * @return a float representing the latitude
     * @throws AISException if the latitude data is unavailable
     */
    public static float decodeLatitude(BitSet b, int startBit, int endBit) throws AISException {
        int i = decodeSignedInt(b, startBit, endBit);

        switch (i) {
            case 0x3412140:
                throw new AISException("Latitude unavailable.");
            default:
                return (float) (((double) i) / (60f * 10000f));
        }
    }

    /**
     *
     * @param b        the bitset which contains the longitude we want to decode
     * @param startBit the starting bit where the longitude data is located
     * @param endBit   the ending bit where the longitude data is located
     * @return a float representing the longitude
     * @throws AISException if the longitude data is unavailable
     */
    public static float decodeLongitude(BitSet b, int startBit, int endBit) throws AISException {
        int i = decodeSignedInt(b, startBit, endBit);

        switch (i) {
            case 0x6791AC0:
                throw new AISException("Longitude unavailable.");
            default:
                return (float) (((double) i) / (60f * 10000f));
        }
    }

    /**
     *
     * @param bits     the bitset from which we wish to decode the turn data
     * @param startBit the starting bit where the turn data is located
     * @param endBit   the ending bit where the turn data is located
     * @return a float representing the turn value of the messsage
     */
    public static float decodeTurn(BitSet bits, int startBit, int endBit) {
        int i = decodeSignedInt(bits, startBit, endBit);

        return (i / 4.733f) * (i / 4.733f);
    }

    /**
     *
     * @param bits     the BitSet from which we want to decode the speed
     * @param startBit the starting bit where the speed data is located
     * @param endBit   the ending bit where the speed data is located
     * @return a float representing the speed
     * @throws AISException if the value is invalid
     */
    public static float decodeSpeed(BitSet bits, int startBit, int endBit) throws AISException {
        int i = decodeUnsignedInt(bits, startBit, endBit);

        if ((i < 0) || (i > 1023))
            throw new AISException("getSpeedOverGround: invalid value: " + i);

        switch (i) {
            case 1023:
                // speed unavailable
                LOG.debug("getSpeedOverGround: unavailable: {}", i);
                return -1f;
            case 1022:
                return 102.2f;
            default:
                return i / 10.f;
        }
    }

    /**
     *
     * @param bits     the BitSet containing the course information we wish to
     *                 decode
     * @param startBit the starting bit where the course information is located
     * @param endBit   the ending bit where the course information is located
     * @return a float representing the course
     * @throws AISException if the decoded value is invalid or unavailable
     */
    public static float decodeCourse(BitSet bits, int startBit, int endBit) throws AISException {
        int i = decodeUnsignedInt(bits, startBit, endBit);

        if ((i < 0) || (i > 3600))
            throw new AISException("decodeCourse: invalid value: " + i);

        switch (i) {
            case 3600:
                throw new AISException("Course unavailable");
            default:
                return i / 10f;
        }
    }

    /**
     *
     * @param bits     the BitSet containing our String
     * @param startBit the starting bit where our String is located
     * @param endBit   the ending bit where our String is located
     * @return the decoded String
     * @throws AISException if we are unable to convert a six bit character into
     *                      Ascii
     */
    public static String decodeToString(BitSet bits, int startBit, int endBit) throws AISException {
        if (LOG.isTraceEnabled())
            LOG.trace("Decoding bit {} through bit {} of {} element BitSet", startBit, endBit, bits.size());

        CharBuffer cb = CharBuffer.allocate((((endBit - startBit) / 6) + 1));

        int stopBit = (endBit > bits.size()) ? bits.size() : endBit;

        // we need to walk forward through every set of six bits without traveling past
        // the endBit
        for (int sb = startBit; sb <= stopBit; sb += 6) {
            int binPosVal = 1; // binary position value
            int charVal = 0; // current binary position value
            for (int s = (sb + 5); s >= sb && s <= stopBit; s--) {
                if (bits.get(s))
                    charVal += binPosVal; // sum bits to arrive at int char value
                binPosVal += binPosVal; // doubling consistent with binary math
            }

            char c = sixBitIntToAscii(charVal);
            if (c != '@') {
                LOG.trace("Appending character to CharBuffer: {}", c);
                cb.put(c);
            }
        }

        return new String(cb.array()).trim();
    }

    /**
     *
     * @param bits     the BitSet containing our byte array
     * @param startBit the starting bit where our byte array is located
     * @param endBit   the ending bit where our byte array is located
     * @return the decoded byte array
     * @throws AISException if we are unable to convert a six bit character into
     *                      Ascii
     */
    public static byte[] decodeToByteArray(BitSet bits, int startBit, int endBit) throws AISException {
        return decodeToString(bits, startBit, endBit).getBytes();
    }
}
