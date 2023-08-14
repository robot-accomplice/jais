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

import jais.AISSentence;
import jais.messages.AISMessage.AISFieldMap;
import jais.messages.enums.AISMessageType;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.Optional;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class AISMessageDecoder {

    public final static int CHAR_RANGE_A_MIN = 48;
    public final static int CHAR_RANGE_A_MAX = 87;
    public final static int CHAR_RANGE_B_MIN = 96;
    public final static int CHAR_RANGE_B_MAX = 119;

    public static BitSet sentencesToBitSet(AISSentence ...sentences) {
        return sentencesToBitSet(AISSentence.DEFAULT_CHARSET, sentences);
    }

    public static BitSet sentencesToBitSet(Charset charset, AISSentence ...sentences) {
        int totalPayloadSize = 0;
        for (AISSentence s : sentences) {
            totalPayloadSize += s.getPayload().length;
        }

        BitSet bits = new BitSet(8 * totalPayloadSize);
        int bIndex = 0;

        for (AISSentence s : sentences) {
            char[] payload = charset.decode(ByteBuffer.wrap(s.getPayload())).array();
            for (char c : payload) {
                int oc = encodeToSixBitInt(c);
                if (oc == -1) { // invalid character
                    bits.clear();
                    break;
                } else {
                    for (int bPos = 5; bPos >= 0; bPos--)
                        bits.set(bIndex++, 0 < (oc & (1 << bPos)));
                }
            }
            for (int i = 0; i < s.getFillBits(); i++) {
                bits.set(bIndex++, false);
            }
        }

        return bits;
    }

    /**
     *
     * @param payload a byte array containing the raw message we intend to decode
     * @return a BitSet representing the decoded message
     */
    public static BitSet byteArrayToBitSet(byte[] payload) {
        return byteArrayToBitSet(payload, AISSentence.DEFAULT_CHARSET);
    }

    /**
     *
     * @param payload a byte array containing the raw message we intend to decode
     * @param charset    the CharacterSet we want to use to perform the decoding
     * @return a BitSet representing the decoded message
     */
    public static BitSet byteArrayToBitSet(byte[] payload, Charset charset) {

        char[] msgChars = charset.decode(ByteBuffer.wrap(payload)).array();

        BitSet bits = new BitSet(8 * payload.length);

        int bIndex = 0;
        for (char c : msgChars) {
            int oc = encodeToSixBitInt(c); // pull the current raw message char
            if (oc == -1) {
                // invalid character
                bits.clear();
                break;
            } else {
                for (int bPos = 5; bPos >= 0; bPos--)
                    bits.set(bIndex++, 0 < (oc & (1 << bPos)));
            }
        }

        return bits;
    }

    /**
     *
     * @param payload the String we want to convert to a BitSet
     * @return a BitSet representing the contents of the String
     */
    private static BitSet stringToBitSet(String payload) {

        char[] msgChars = payload.toCharArray();

        BitSet bits = new BitSet(8 * msgChars.length);

        int bIndex = 0;
        for (char c : msgChars) {
            int oc = encodeToSixBitInt(c); // pull the current raw message char
            if (oc == -1) {
                // invalid character
                bits.clear();
                break;
            } else {
                for (int bPos = 5; bPos >= 0; bPos--)
                    bits.set(bIndex++, 0 < (oc & (1 << bPos)));
            }
        }

        return bits;
    }

    /**
     *
     * @param c the character we want to encode to a six bit int
     * @return the six bit int representation of the provided character
     */
    private static int encodeToSixBitInt(char c) {
        if (c <= CHAR_RANGE_A_MAX && c >= CHAR_RANGE_A_MIN)
            return c - CHAR_RANGE_A_MIN;
        else if (c <= CHAR_RANGE_B_MAX && c >= CHAR_RANGE_B_MIN)
            return c - CHAR_RANGE_B_MIN + (CHAR_RANGE_A_MAX - CHAR_RANGE_A_MIN + 1);
        else {
            // character outside of acceptable range
            return '@';
        }
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
            endBit = bits.size();

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
     * decode signed integers in twos-complement form.  If the number is negative
     * first bit must be set to on/true and the rest of the bits are inverted, meaning 
     * that a off/false bit is counted and a true or on bit is not, the bits are
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
     */
    private static char sixBitIntToAscii(int c) {
        int rval = c;

        if (c < 0 || c > 63) {
            // invalid character
            return '@';
        } else if (c < 32)
            rval += 64;

        return (char) rval;
    }

    /**
     *
     * @param packets the collection of packets from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     */
    public static Optional<AISMessageType> decodeMessageType(AISSentence... packets) {
        // concatenate full raw message from all packets
        return decodeMessageType(AISSentence.concatenate(packets));
    }

    /**
     *
     * @param rawMessage the String from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     */
    public static Optional<AISMessageType> decodeMessageType(String rawMessage) {
        return decodeMessageType(stringToBitSet(rawMessage));
    }

    /**
     *
     * @param rawMessage the byte array from which this message was composed
     * @return An optional which may contain the decoded AISMessageType
     */
    public static Optional<AISMessageType> decodeMessageType(byte[] rawMessage) {
        return decodeMessageType(byteArrayToBitSet(rawMessage));
    }

    /**
     *
     * @param bits the BitSet containing the message we wish to decode
     * @return an Optional which may contain the AISMessageType
     */
    public static Optional<AISMessageType> decodeMessageType(BitSet bits) {

        if (bits.size() < AISFieldMap.TYPE.getEndBit()) {
            // bitset too short
            return Optional.empty();
        }

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
     */
    public static float decodeLatitude(BitSet b, int startBit, int endBit) {
        int i = decodeSignedInt(b, startBit, endBit);

        if (i == 0x3412140) {
            // latitude unavailable
            return -91f;
        }
        return (float) (((double) i) / (60f * 10000f));
    }

    /**
     *
     * @param b        the bitset which contains the longitude we want to decode
     * @param startBit the starting bit where the longitude data is located
     * @param endBit   the ending bit where the longitude data is located
     * @return a float representing the longitude
     */
    public static float decodeLongitude(BitSet b, int startBit, int endBit) {
        int i = decodeSignedInt(b, startBit, endBit);

        if (i == 0x6791AC0) {
            // longitude unavailable
            return -181f;
        }
        return (float) (((double) i) / (60f * 10000f));
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
     * @param bits     the bitset from which we wish to decode the heading data
     * @param startBit the starting bit where the heading data is located
     * @param endBit   the ending bit where the heading data is located
     * @return an int representing the heading value of the messsage
     */
    public static int decodeHeading(BitSet bits, int startBit, int endBit) {
        int heading = decodeUnsignedInt(bits, startBit, endBit);

        return (heading > 360) ? 511 : heading;
    }

    /**
     *
     * @param bits     the BitSet from which we want to decode the speed
     * @param startBit the starting bit where the speed data is located
     * @param endBit   the ending bit where the speed data is located
     * @return a float representing the speed
     */
    public static float decodeSpeed(BitSet bits, int startBit, int endBit) {
        int i = decodeUnsignedInt(bits, startBit, endBit);

        if ((i < 0) || (i > 1023)) {
            // invalid speed
            return -1f;
        }
        return switch (i) {
            case 1023 -> 102.3f; // speed unavailable
            case 1022 -> 102.2f;
            default -> i / 10.f;
        };
    }

    /**
     *
     * @param bits     the BitSet containing the course information we wish to
     *                 decode
     * @param startBit the starting bit where the course information is located
     * @param endBit   the ending bit where the course information is located
     * @return a float representing the course
     */
    public static float decodeCourse(BitSet bits, int startBit, int endBit) {
        int i = decodeUnsignedInt(bits, startBit, endBit);

        if ((i < 0) || (i >= 3600)) {
            // invalid course
            return 3600;
        } else {
            return i / 10f;
        }
    }

    /**
     *
     * @param bits     the BitSet containing our String
     * @param startBit the starting bit where our String is located
     * @param endBit   the ending bit where our String is located
     * @return the decoded String
     */
    public static String decodeString(BitSet bits, int startBit, int endBit) {

        int capacity = (((endBit - startBit) / 6) + 1);
        if (capacity < 0) {
            return null;
        }

        CharBuffer cb = CharBuffer.allocate(capacity);

        int stopBit = Math.min(endBit, bits.size());

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
            if (c != '@') cb.put(c);
        }

        return new String(cb.array()).trim();
    }

    /**
     *
     * @param bits     the BitSet containing our byte array
     * @param startBit the starting bit where our byte array is located
     * @param endBit   the ending bit where our byte array is located
     * @return the decoded byte array
     */
    public static byte[] decodeToByteArray(BitSet bits, int startBit, int endBit) {
        String decoded = decodeString(bits, startBit, endBit);
        if (decoded != null)
            return decoded.getBytes();
        else
            return null;
    }
}
