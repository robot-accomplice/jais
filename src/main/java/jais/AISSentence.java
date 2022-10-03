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
package jais;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jais.messages.AISMessageDecoder;
import jais.messages.enums.Manufacturers;
import jais.messages.enums.SentenceType;
import jais.messages.enums.Talkers;
import lombok.Getter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public final class AISSentence implements Sentence {

    private final static Logger LOG = LogManager.getLogger(AISSentence.class);

    // reserved characters
    public final static char ENCAP_START = '!';
    public final static char PARAM_START = '$';
    public final static char CHECKSUM_DELIMITER = '*';
    public final static char FIELD_DELIMITER = ',';
    public final static char HEX_DELIMITER = '^';
    public final static char RESERVED_DELIMITER = '~';
    private final static String DEFAULT_SOURCE = "UNSPECIFIED";

    private final static double CHANNEL_A_FREQUENCY_IN_MHZ = 161.975;
    private final static double CHANNEL_B_FREQUENCY_IN_MHZ = 162.025;

    public final static String PREAMBLE = "([!|$])([A-Z0-9]{1,2})(([A-Z]{2})([A-Z]))";
    public final static Pattern PREAMBLE_PATTERN = Pattern.compile(PREAMBLE);
    public final static Pattern SENTENCE_PATTERN = Pattern
            .compile("(" + TagBlock.TAGBLOCK_STRING + ")?(" + PREAMBLE + "(.*))");
    public final static int PREAMBLE_GROUPS = 5;
    public final static Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    private TagBlock tagBlock;
    private Preamble preamble;
    private byte[] source;
    private byte[] type;

    private int fragmentCount = 1;
    private int fragmentNumber = 1;
    private int sequentialMessageId = -1;
    private char radioChannelCode;
    private byte[] rawSentence; // the unparsed initial string
    private byte[] binaryString; // the binary string
    private byte[] sentenceBody; // the message without the tagblock
    private int fillBits;
    private byte[] checksum;
    private final long timeReceived = ZonedDateTime.now(ZoneOffset.UTC.normalized()).toInstant().toEpochMilli();
    private byte[][] sentenceParts;
    private boolean parsed = false;

    private final SentenceType sentenceType = SentenceType.NMEA_AIS;

    /**
     * Constructor
     *
     * @param rawSentence byte[] composed of the characters from the original
     *                    non-decoded String representing a complete or partial AIS
     *                    message
     */
    public AISSentence(byte[] rawSentence) {
        this(rawSentence, ByteArrayUtils.str2bArray(DEFAULT_SOURCE));
    }

    /**
     * Constructor
     *
     * @param rawSentence byte[] composed of the characters from the original
     *                    non-decoded String representing a complete or partial AIS
     *                    message
     * @param source      byte[] for the named source of the AIS sentence
     */
    public AISSentence(byte[] rawSentence, byte[] source) {
        if (LOG.isTraceEnabled())
            LOG.trace("Constructor instantiated with: \"{}\", \"{}\"", rawSentence, source);
        this.rawSentence = ByteArrayUtils.trimByteArray(rawSentence);
        this.source = ByteArrayUtils.trimByteArray(source);
    }

    /**
     * Constructor
     *
     * @param rawSentence String representing the original 6 bit encoded String
     *                    representing a complete or
     *                    partial AIS message
     */
    public AISSentence(String rawSentence) {
        this(rawSentence, DEFAULT_SOURCE);
    }

    /**
     * Constructor
     *
     * @param rawSentence String representing the original 6 bit encoded String
     *                    representing a complete or
     *                    partial AIS message
     * @param source      String representing the named source of this AIS sentence
     */
    public AISSentence(String rawSentence, String source) {
        if (LOG.isTraceEnabled())
            LOG.trace("Constructor instantiated with: \"{}\", \"{}\"", rawSentence, source);
        this.rawSentence = ByteArrayUtils.str2bArray(rawSentence);
        if (source != null)
            this.source = ByteArrayUtils.str2bArray(source);
        else
            this.source = ByteArrayUtils.str2bArray(DEFAULT_SOURCE);
    }

    /**
     * 
     */
    @Override
    public void parse() {
        if (this.sentenceParts[0] != null) this.preamble = new Preamble(this.sentenceParts[0]);
        this.validatePreamble();
        this.process();
    }

    /**
     * Validates the AIS sentence preamble against a regular expression constant
     *
     * @return boolean indicating whether or not the preamble is valid
     */
    private boolean validatePreamble() {
        if (this.sentenceParts == null) {
            LOG.trace("this.sentenceParts is null");
            return false;
        } else if (this.sentenceParts.length == 0) {
            LOG.trace("this.sentenceParts has zero members");
            return false;
        } else if (this.preamble == null && this.sentenceParts[0] == null) {
            LOG.trace("this.sentenceParts[0] is null");
            return false;
        } else {
            if (LOG.isTraceEnabled())
                LOG.trace("Creating preamble object from {}", ByteArrayUtils.bArray2Str(this.sentenceParts[0]));
            return validatePreamble(Preamble.parse(this.sentenceParts[0]));
        }
    }

    /**
     * Validates that the provided Preamble object is non-null and does not contain
     * null fields
     *
     * @param p Preamble object to evaluate for validity
     * @return boolean indicating whether or not the preamble is valid
     */
    private static boolean validatePreamble(Preamble p) {
        return ((p != null) && (p.talker != null) && (p.format != null));
    }

    /**
     * Validates the AIS sentence preamble against a regular expression constant
     * 
     * @param preambleStr String preamble to evaluate for validity
     * @return boolean indicating whether or not the preamble is valid
     */
    public static boolean validatePreamble(String preambleStr) {
        return validatePreamble(Preamble.parse(preambleStr));
    }

    /**
     * Determines whether or not a TagBlock was parsed from this AISsentence
     * 
     * @see jais.TagBlock
     *
     * @return boolean representing whether or not this sentence has a TagBlock
     */
    public boolean hasTagBlock() {
        return (this.tagBlock != null);
    }

    /**
     * Validates the contents of the sentence and breaks it into its constituent
     * parts
     * 
     * @return an AISSentence that is the product of the processing
     */
    public final AISSentence process() {
        return process(false);
    }

    /**
     * Validates the contents of the sentence and breaks it into its constituent
     * parts, optionally generates a TagBlock
     * for the resulting AISsentence @see jais.TagBlock
     * 
     * @param addTagBlock boolean flag indicating whether or not a TagBlock should
     *                    be pre-pended to the sentence
     * @see jais.TagBlock
     * @return a reference to the current AISsentence object
     *         malformed
     */
    public final AISSentence process(boolean addTagBlock) {
        String rawSentence;

        if (this.rawSentence == null) {
            LOG.trace("Raw sentence is null");
            return this;
        } else if (this.rawSentence.length == 0) {
            LOG.trace("Raw sentence is empty");
            return this;
        } else {
            rawSentence = ByteArrayUtils.bArray2Str(ByteArrayUtils.trimByteArray(this.rawSentence));
            LOG.trace("Processing new raw sentence: {}", rawSentence);
        }

        Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher(rawSentence);
        if (m.find()) {
            LOG.trace("Found a TagBlock in \"{}\"", rawSentence);
            try {
                if (this.source == null || this.source.length == 0) {
                    this.tagBlock = TagBlock.parse(m.group(0));
                    this.source = this.tagBlock.getSource();
                } else
                    this.tagBlock = TagBlock.parse(m.group(0), this.source);
            } catch (Throwable t) {
                if (LOG.isTraceEnabled())
                    LOG.trace("Unable to parse TagBlock from {}", m.group(0));
            }

            this.sentenceBody = ByteArrayUtils.str2bArray(rawSentence.substring(m.end()));
        } else if (addTagBlock) {
            if (this.source != null && this.source.length != 0)
                this.tagBlock = TagBlock.build(this.source);
            this.sentenceBody = this.rawSentence;
        } else {
            LOG.trace("No TagBlock found and addTagBlock is false");
            this.sentenceBody = this.rawSentence;
        }

        if (LOG.isTraceEnabled())
            LOG.trace("this.sentenceBody = \"{}\"", ByteArrayUtils.bArray2Str(this.sentenceBody));

        if (this.sentenceParts == null)
            this.sentenceParts = ByteArrayUtils.fastSplit(this.sentenceBody, FIELD_DELIMITER);

        if (this.sentenceParts == null || this.sentenceParts.length < 6)
            LOG.trace("Raw sentence contains no message (inadequate number of comma-separated values).");

        switch (this.sentenceParts.length) {
            case 10:
                if (LOG.isTraceEnabled())
                    LOG.trace("Unrecognized field at position 10: {}",
                            ByteArrayUtils.bArray2Str(this.sentenceParts[9]));
            case 9:
                if (LOG.isTraceEnabled())
                    LOG.trace("Unrecognized field at position  9: {}",
                            ByteArrayUtils.bArray2Str(this.sentenceParts[8]));
            case 8:
                if (LOG.isTraceEnabled())
                    LOG.trace("Unrecognized field at position  8: {}",
                            ByteArrayUtils.bArray2Str(this.sentenceParts[7]));
            case 7:
                if (this.sentenceParts[6] != null && this.sentenceParts[6].length > 0) {
                    try {
                        byte[] firstByte = { this.sentenceParts[6][0] };
                        this.fillBits = Integer.parseInt(ByteArrayUtils.bArray2Str(firstByte));
                    } catch (NumberFormatException nfe) {
                    }

                    int csIndex = ByteArrayUtils.indexOf(this.sentenceParts[6], CHECKSUM_DELIMITER);
                    if (csIndex != -1) {
                        byte[] checksumBytes = Arrays.copyOfRange(
                                this.sentenceParts[6], csIndex + 1, this.sentenceParts[6].length);
                        if (checksumBytes != null && checksumBytes.length > 0) {
                            this.checksum = ByteArrayUtils.trimByteArray(checksumBytes);
                        }
                    } else
                        LOG.trace("Sentence is missing checksum!");
                }
            case 6:
                if (this.sentenceParts[5] == null)
                    LOG.trace("Raw message is null.");
                else if (this.sentenceParts[5].length == 0)
                    LOG.trace("Raw message is empty.");
                this.binaryString = this.sentenceParts[5]; // only the binary string
                break;
            default:
                if (LOG.isTraceEnabled())
                    LOG.trace("Sentence is corrupt and has no message body: {}",
                            ByteArrayUtils.bArray2Str(this.rawSentence));
        }

        this.parsed = true;
        return this;
    }

    /**
     * Checks the validity of the current AIS sentence by analyzing the length of
     * its
     * String representation, the number of comma separated fields it
     * contains, whether or not it has a valid preamble, whether or not it contains
     * any invalid characters, and whether or not it has a valid
     * checksum
     *
     * @return a boolean value representing the validity of this AISsentence
     */
    public final boolean isValid() {
        try {
            // so we don't throw NPEs over the failure to split the raw String
            if (this.sentenceParts == null)
                process();

            if (LOG.isTraceEnabled())
                LOG.trace("Validating sentenceBody: {}", ByteArrayUtils.bArray2Str(this.sentenceBody));

            if (this.sentenceBody.length > 82)
                return false; // invalid sentence length
            if (this.sentenceParts.length == 0)
                return false; // split failed
            if (this.sentenceParts.length != 7)
                return false; // invalid number of csv fields
            if (!validatePreamble())
                return false; // invalid preamble

            // check for bad characters in binary string
            for (char c : ByteArrayUtils.bArray2cArray(this.sentenceParts[5])) {
                // is this character within an accepted range?
                if (!((c <= AISMessageDecoder.CHAR_RANGE_A_MAX && c >= AISMessageDecoder.CHAR_RANGE_A_MIN)
                        || (c <= AISMessageDecoder.CHAR_RANGE_B_MAX && c >= AISMessageDecoder.CHAR_RANGE_B_MIN))) {
                    LOG.trace("sentence contains an invalid character: {}", c);
                    return false;
                }
            }

            // if we don't have any bad characters validate the checksum
            int csIndex = ByteArrayUtils.indexOf(this.sentenceBody, CHECKSUM_DELIMITER) + 1;

            if (csIndex > 0) {
                // validate checksum
                if (!validateChecksum(this.sentenceBody, this.checksum)) {
                    LOG.trace("sentence failed checksum validation.");
                    return false;
                }
            } else {
                LOG.fatal("sentence is missing checksum.");
                return false;
            }
        } catch (Exception e) {
            LOG.trace("sentence validation failed: {}", e.getMessage(), e);
            return false;
        }

        return true;
    }

    /**
     * Generates a valid checksum based on the provided char []
     *
     * @param source the source char [] for which you wish to generate a checksum
     * @return a generated int checksum for the provided char []
     */
    private static int generateChecksum(char[] source) {
        if (LOG.isTraceEnabled())
            LOG.trace("Generating checksum for String \"{}\"", new String(source));

        int crc = 0;
        for (char aSource : source)
            crc ^= aSource;

        if (LOG.isTraceEnabled())
            LOG.trace("Generated CRC = {}(int)/{}(hex)", crc, Integer.toHexString(crc));

        return crc;
    }

    /**
     * Generates a valid checksum based on the provided String
     *
     * @param sourceString the source String fro which you wish to generate a
     *                     checksum
     * @return a generated int checksum for the provided String
     */
    public static String generateChecksumString(String sourceString) {
        String hexString = Integer.toHexString(generateChecksum(sourceString.toCharArray()));

        hexString = (hexString.length() == 1) ? "0" + hexString : hexString;

        LOG.trace("Produced hex string {} from sourceString {}", hexString, sourceString);

        return hexString;
    }

    /**
     * Attempts to parse a checksum from the provided String and generates a new one
     * if the parsing operation is unsuccessful
     *
     * @param data the AIS sentence string for which you wish to parse the checksum
     * @return the int checksum for the provided string
     */
    private static int getChecksum(String data) {
        int index = data.indexOf(String.valueOf(CHECKSUM_DELIMITER));
        if (index > -1) {
            LOG.trace("Found * at {}", index);
            return getChecksum(data, 1, data.indexOf((String.valueOf(CHECKSUM_DELIMITER))));
        } else {
            LOG.trace("Index was {}", index);
            return getChecksum(data, 1, data.length());
        }
    }

    /**
     * Generates a checksum for the provided byte []
     *
     * @param bytes the byte [] from which you wish to extract a checksum
     * @return the int checksum for the provided byte []
     */
    private static int getChecksum(byte[] bytes) {
        return AISSentence
                .generateChecksum(ByteArrayUtils
                        .bArray2cArray(Arrays.copyOfRange(bytes, 1,
                                ByteArrayUtils.indexOf(bytes, CHECKSUM_DELIMITER))));
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
    public static int getChecksum(String genString, int startFrom, int endAt) {
        if (endAt <= startFrom || endAt > genString.length())
            return -1;

        return AISSentence.generateChecksum(genString.substring(startFrom, endAt).toCharArray());
    }

    /**
     * Validates the provided checksum (byte [] sentenceChecksum) by generating a
     * new
     * checksum for byte [] data and comparing them
     *
     * @param data             the byte [] to which the provided sentenceChecksum
     *                         should
     *                         apply
     * @param sentenceChecksum a byte [] representation of the checksum to be
     *                         validated
     * @return a boolean representing the validity of the checksum
     */
    private static boolean validateChecksum(byte[] data, byte[] sentenceChecksum) {
        long calcChecksum;
        long pktChecksum;

        byte[] trimmed = ByteArrayUtils.trimByteArray(data);

        try {
            calcChecksum = getChecksum(trimmed);
            LOG.trace("Generated checksum {}", calcChecksum);
        } catch (NumberFormatException nfe) {
            if (LOG.isTraceEnabled())
                LOG.trace("Cannot produce a checksum from  \"{}\"", ByteArrayUtils.bArray2Str(trimmed));
            return false;
        }

        try {
            pktChecksum = Long.parseUnsignedLong(ByteArrayUtils.bArray2Str(sentenceChecksum), 16);
        } catch (NumberFormatException nfe) {
            if (LOG.isInfoEnabled())
                LOG.info("Cannot parse \"{}\" into a valid long", ByteArrayUtils.bArray2Str(sentenceChecksum));
            return false;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Comparing: \"{}/{}\" to \"{}/{}\"", pktChecksum, ByteArrayUtils
                    .bArray2Str(sentenceChecksum).toUpperCase(),
                    calcChecksum, Long.toHexString(calcChecksum).toUpperCase());
            LOG.trace("\"{}\" is {} equal to \"{}\"", calcChecksum, ((calcChecksum == pktChecksum) ? "" : "not"),
                    pktChecksum);
        }

        return (pktChecksum == calcChecksum);
    }

    /**
     * A utility method that enables binary decoding even when the binary string is
     * all we have
     *
     * @param rawData the binary String from an AIS sentence String which has no
     *                prefix or suffix
     * @return a generated String representation of a complete AIS sentence (with
     *         prefix, suffix, checksum, etc)
     */
    public static String createSentenceStringFromBinaryString(String rawData) {
        String sentenceString = "!AIVDM,1,1,,A," + rawData + ",0*";
        LOG.trace("sentence before checksum: {}", sentenceString);
        sentenceString += Integer.toHexString(AISSentence.getChecksum(sentenceString));
        LOG.trace("sentence after checksum: {}", sentenceString);

        return sentenceString;
    }

    /**
     * A utility method that creates an AISsentence object based solely on the 6-bit
     * encoded String from an AIS sentence
     * String
     * 
     * @param rawData The binary encoded String
     * @return an AISsentence object based on the provided binary string
     */
    public static AISSentence createFromBinaryString(String rawData) {
        return createFromBinaryString(rawData, null);
    }

    /**
     * Generates an AISsentence object from a raw 6-bit encoded String and a String
     * representing the data source
     *
     * @param rawData The binary encoded String
     * @param source  A string representing the source from which this sentence
     *                originated
     * @return an AISsentence object based on the provided binary string
     */
    public static AISSentence createFromBinaryString(String rawData, String source) {
        if (source == null)
            source = "UNKNOWN";
        return new AISSentence(createSentenceStringFromBinaryString(rawData), source);
    }

    /**
     * Returns the unparsed, non-decoded raw AISsentence contents as a byte []
     *
     * @return the byte [] containing the raw, non-decoded AISsentence data
     */
    public final byte[] getRawSentence() {
        return this.rawSentence;
    }

    /**
     * Generates a String representation of the AISsentence with a pre-pended
     * TagBlock
     * String which contains only
     * the source and time stamp values of the AISsentence object
     *
     * @return a String representation of the AISsentence with a pre-pended TagBlock
     *         String
     */
    public final String generateTagBlockSentenceString() {
        TagBlock tb = new TagBlock();
        tb.setSource(this.source);
        tb.setTimestamp(this.timeReceived);
        return generateTagBlockSentenceString(this.rawSentence, tb);
    }

    /**
     * Generates a String representation of the AISsentence with a pre-pended
     * TagBlock
     * String containing the source and time stamp values of the
     * AISsentence object as well as the text provided at method invocation
     *
     * @param text the byte array we wish to use to construct a TagBlock String
     * @return a String representing the TagBlock contents
     */
    public final String generateTagBlockSentenceString(byte[] text) {
        TagBlock tb = new TagBlock();
        tb.setSource(this.source);
        tb.setTimestamp(this.timeReceived);
        tb.setTextStr(text);
        return generateTagBlockSentenceString(this.rawSentence, tb);
    }

    /**
     * Generates a String representation of the AISsentence with its pre-pended
     * TagBlock String as already defined
     *
     * @param rawSentence A byte array containing representing the binary AIS
     *                    sentence
     *                    String
     * @param tb          The TagBlock object we wish to prepend to the AIS sentence
     * @return A String representation of the concatenated TagBlock and AIS sentence
     */
    private static String generateTagBlockSentenceString(byte[] rawSentence, TagBlock tb) {
        return tb.toString() + ByteArrayUtils.bArray2Str(rawSentence);
    }

    /**
     *
     * @return the value of this.type
     */
    public final byte[] getType() {
        return this.type;
    }

    /**
     * Returns the AIS message fragmentation count as defined in the original
     * non-decoded AIS sentence String
     * This count indicates how many related AIS sentences the complete AIS message
     * is
     * composed of
     *
     * @return an int representing the total number of sentence fragments that
     *         compose
     *         the final message
     */
    public final int getFragmentCount() {
        return this.fragmentCount;
    }

    /**
     * Returns the AIS message fragmentation number as defined in the original
     * non-decoded AIS sentence String
     * This number indicates the position of this fragment in the fully assembled
     * AIS message
     *
     * @return an int representing the specific fragment (of the total message) this
     *         sentence represents
     */
    public final int getFragmentNumber() {
        return this.fragmentNumber;
    }

    /**
     * This returns the value of the locally distinct "talker" sending this and
     * other AIS messages so that they can easily be grouped by source
     *
     * @return an int representing the locally unique broadcaster of this message
     */
    public final int getSequentialMessageId() {
        return this.sequentialMessageId;
    }

    /**
     * Returns the letter representation of the Radio frequency on which this AIS
     * sentence was broadcast
     *
     * @return A single character representing the radio frequency on which this
     *         sentence was broadcast
     */
    public final char getRadioChannelCode() {
        return this.radioChannelCode;
    }

    /**
     * Returns the actual numeric frequency (as a double) indicated by the
     * this.radioChannelCode (see {@link #getRadioChannelCode()})
     *
     * @return a double representing the numeric frequency on which this message was
     *         broadcast
     */
    public final double getRadioChannelFrequencyInMhz() {
        switch (this.radioChannelCode) {
            case 'a':
                return CHANNEL_A_FREQUENCY_IN_MHZ;
            case 'b':
                return CHANNEL_B_FREQUENCY_IN_MHZ;
        }

        return 0d;
    }

    /**
     * Returns the contents of the non-decoded binary string as a byte []
     *
     * @return the raw binary string in the form of a byte array
     */
    public final byte[] getBinaryStringAsByteArray() {
        return this.binaryString;
    }

    /**
     * Returns the body of the AIS sentence as a byte []
     *
     * @return the raw body (binary string portion) of the sentence in the form of a
     *         byte array
     */
    public final byte[] getSentenceBodyAsByteArray() {
        return this.sentenceBody;
    }

    /**
     * Returns the parsed count of fill bits from the AIS sentence String
     *
     * @return an int representing the number of fillbits specified in the AIS
     *         sentence String
     */
    public final int getFillBits() {
        return this.fillBits;
    }

    /**
     * Returns the parsed or generated checksum from the AIS sentence
     *
     * @return a byte array representation of the sentence checksum
     */
    public final byte[] getChecksum() {
        return this.checksum;
    }

    /**
     * Returns the time at which this AISsentence object was instantiated
     *
     * @return a long representing the time at which we instantiated this instance
     *         of AISsentence
     */
    public final long getTimeReceived() {
        return this.timeReceived;
    }

    /**
     * Returns the time at which this AISsentence object was instantiated for the
     * specified ZoneOffset
     *
     * @param offset The ZoneOffset for calculating the time since epoch value of
     *               the time at which this sentence was received
     * @return a ZonedDateTime object representing the time at which this AIS
     *         sentence
     *         was received
     */
    public final ZonedDateTime getTimeReceived(ZoneOffset offset) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.timeReceived), offset);
    }

    /**
     * Returns the time at which this AISsentence object was instantiated for the
     * specified ZoneId
     *
     * @param zone the ZoneId which we want to use to calculate the ZonedDateTime
     *             value
     * @return the calculated ZonedDateTime value
     */
    public final ZonedDateTime getTimeReceived(ZoneId zone) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this.timeReceived), zone);
    }

    /**
     * Returns the time at which this AISsentence object was presumably sent based
     * on
     * the timestamp contained within the TagBlock
     *
     * @return a long representation of the timestamp at which the sentence was sent
     */
    public final long getTimeSent() {
        if (hasTagBlock())
            return this.tagBlock.getTimestamp();
        return 0;
    }

    /**
     * Returns the source of the AISsentence as a byte []
     *
     * @return a byte array representation of the name of the sentence source
     */
    public final byte[] getSource() {
        return this.source;
    }

    /**
     * Sets the source of the AISsentence to the provided byte [] value
     *
     * @param source sets the name of the source of this sentence as a byte array
     */
    public final void setSource(byte[] source) {
        this.source = source;
    }

    /**
     * Takes a String parameter and divides it into one or more distinct AISSentece
     * objects
     * 
     * @param sentence String to split or Truncate into multiple AISSentence objects
     * @return Optional AISSentence[] which may be empty if no valid AISSentences
     *         could be found
     */
    public static Optional<AISSentence[]> splitOrTruncate(String sentence) {
        if (sentence != null && sentence.length() > 0) {
            int index = getSentenceTruncIndex(sentence);
            if (index > -1) {
                String[] sentStrs = ByteArrayUtils.fastSplit(sentence, '!');
                List<AISSentence> sList = Arrays.stream(sentStrs).map(AISSentence::new)
                        .collect(Collectors.toList());
                AISSentence[] sentences = new AISSentence[sList.size()];
                return Optional.of(sList.toArray(sentences));
            }
        }
        return Optional.empty();
    }

    /**
     * Truncates the provided StringBuilder object based on the index returned by
     * {@link #getSentenceTruncIndex( String s )}
     * 
     * @param sb the StringBuilder from which we want to produce a truncated String
     * @return a truncated String
     */
    public static String truncateSentence(StringBuilder sb) {
        return truncateSentence(sb.toString());
    }

    /**
     * Truncates the provided String object based on the index returned by
     * {@link #getSentenceTruncIndex( String s )}
     *
     * @param s a String we wish to truncate
     * @return the substring produced by truncating the provided String
     */
    public static String truncateSentence(String s) {
        int truncIndex = AISSentence.getSentenceTruncIndex(s);
        String substring = null;

        if (truncIndex != -1) {
            LOG.trace("Truncating: {}", s);
            substring = s.substring(0, truncIndex);
        }

        return substring;
    }

    /**
     *
     * @param s Determines the character index at which this String should be
     *          truncated based on the String contents
     * @return the calculated index at which truncation should occur
     */
    private static int getSentenceTruncIndex(String s) {
        int truncIndex = 0;

        LOG.trace("Evaluating \"{}\" for truncation point.", s);

        // String [] sansTagBlock = s.split( "\\\\!" );
        //
        // Matcher m = AISsentence.PREAMBLE_PATTERN.matcher( ( sansTagBlock.length > 1)
        // ?
        // sansTagBlock[1] : sansTagBlock[0] );

        Matcher m = AISSentence.PREAMBLE_PATTERN.matcher(s);
        if (m.find()) {
            if (s.contains("\n")) {
                LOG.trace("String is terminated by a newline");
                truncIndex = s.indexOf("\n");
            } else if (s.contains("\r")) {
                LOG.trace("String is terminated by a carriage return");
                truncIndex = s.indexOf("\r");
            } else if (m.find()) {
                truncIndex = s.indexOf(m.group(0), 1);
                LOG.trace("Truncating based on preamble");
                if (LOG.isTraceEnabled())
                    LOG.trace("Matched string for index is: \"{}\"", m.group(0));
            } else {
                LOG.trace("Line should not be truncated.");
                truncIndex = -1;
            }
        }

        LOG.trace("Truncation index set to {}", truncIndex);

        return truncIndex;
    }

    /**
     * Combines two or more AISsentence objects into a single non-decoded AIS
     * message
     * and returns the results as a byte []
     *
     * @param sentences one or more AISsentence objects that we wish to combine into
     *                  a single binary string for decoding
     * @return a byte [] representation of the concatenated AIS binary strings
     */
    public static byte[] concatenate(AISSentence... sentences) {
        if (sentences.length == 1) {
            if (!sentences[0].isParsed())
                sentences[0].process();
            return sentences[0].getBinaryStringAsByteArray();
        }

        byte[] compositeMsg = null;

        LOG.trace("Concatenating {} sentences.", sentences.length);
        for (AISSentence sentence : sentences) {
            if (sentence == null) {
                LOG.warn("Skipping null sentence in {} length array of sentences.", sentences.length);
                continue;
            } else if (!sentence.isParsed())
                sentence.process();

            byte[] bytes = sentence.getBinaryStringAsByteArray();
            if (compositeMsg == null)
                compositeMsg = bytes;
            else {
                byte[] temp = new byte[compositeMsg.length + bytes.length];
                System.arraycopy(compositeMsg, 0, temp, 0, compositeMsg.length);
                System.arraycopy(bytes, 0, temp, compositeMsg.length, bytes.length);
                compositeMsg = temp;
            }
        }

        return compositeMsg;
    }

    /**
     * An override of Object.equals()
     *
     * @param o the object against which we will perform our comparison
     * @return the boolean result of comparing the the provided object to the
     *         current one
     */
    @Override
    public final boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof AISSentence))
            return false;

        AISSentence that = (AISSentence) o;
        if (that.getRawSentence() == null)
            return false;

        return (Arrays.equals(that.getRawSentence(), this.rawSentence)
                && Arrays.equals(that.getSource(), this.source));
    }

    /**
     * An override of Object.hashCode()
     *
     * @return an int representing a hashcode
     */
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.rawSentence != null ? Arrays.hashCode(this.rawSentence) : 0);
        hash = 79 * hash + (this.source != null ? Arrays.hashCode(this.source) : 0);
        return hash;
    }

    /**
     * Returns a HashMap representation of the AISsentence fields
     *
     * @return a HashMap representation of the AISsentence
     */
    public final HashMap<String, Object> toMap() {
        HashMap<String, Object> sentenceMap = new HashMap<>();

        sentenceMap.put("tagblock", this.tagBlock);
        sentenceMap.put("preamble", this.preamble);
        sentenceMap.put("raw_message", this.binaryString);
        sentenceMap.put("raw_sentence", this.rawSentence);
        sentenceMap.put("time_received", this.timeReceived);
        sentenceMap.put("source", this.source);
        sentenceMap.put("fragment_count", this.fragmentCount);
        sentenceMap.put("fragment_number", this.fragmentNumber);
        sentenceMap.put("sequential_message_id", this.sequentialMessageId);
        sentenceMap.put("radio_channel_code", this.radioChannelCode);
        sentenceMap.put("checksum", this.checksum);
        sentenceMap.put("fillbits", this.fillBits);

        return sentenceMap;
    }

    /**
     * An object representation of an AISsentence preamble
     *
     */
    public static class Preamble {

        public final byte[] rawPreamble;
        public char firstChar;
        public boolean isEncapsulated;
        public Talkers talker;
        public boolean isProprietary;
        public Manufacturers manufacturer;
        public byte[] format;
        public boolean isQuery;
        public byte[] parsed;

        /**
         *
         * @param rawPreamble a byte [] representation of the AISsentence preamble
         */
        public Preamble(byte[] rawPreamble) {
            this.rawPreamble = rawPreamble;
        }

        /**
         * Populates the fields of this Preamble object based on the parsing of it's
         * rawPreamble byte [] and returns this Preamble object
         *
         * @return a Preamble object
         */
        public Preamble parse() {
            return parse(this, ByteArrayUtils.bArray2Str(rawPreamble));
        }

        /**
         * Returns a Preamble object based on the parsing of the provided raw preamble
         * byte []
         *
         * @param rawPreamble the unparsed preamble in byte array form
         * @return a Preamble object based on parsing the provided rawPreamble
         */
        public static Preamble parse(byte[] rawPreamble) {
            return parse(ByteArrayUtils.bArray2Str(rawPreamble));
        }

        /**
         * Returns a Preamble object based on the parsing of the provided raw preamble
         * String
         *
         * @param rawPreamble a String representation of the unparsed preamble
         * @return a Preamble object
         */
        public static Preamble parse(String rawPreamble) {
            return parse(new Preamble(ByteArrayUtils.str2bArray(rawPreamble)), rawPreamble);
        }

        /**
         * Parses the provided rawPreamble String and populates the fields of the
         * provided Preamble object before returning it
         *
         * @param p           the Preamble we wish to populate
         * @param rawPreamble the raw String we wish to parse in order to build our
         *                    Preamble object
         * @return the completed Preamble object
         */
        public static Preamble parse(Preamble p, String rawPreamble) {
            LOG.trace("Parsing {}", rawPreamble);
            Matcher m = PREAMBLE_PATTERN.matcher(rawPreamble);
            if (m.find()) {
                String parsed = m.group(0);
                p.parsed = ByteArrayUtils.str2bArray(parsed);
                if (LOG.isTraceEnabled())
                    LOG.trace("Found {} matcher groups: {}=({})({})({})({})", m.groupCount(),
                            m.group(), m.group(1), m.group(2), m.group(4), m.group(5));
                p.firstChar = m.group(1).charAt(0);

                if (p.firstChar == '!')
                    p.isEncapsulated = true;
                else if (m.group(1).equals("$"))
                    p.isEncapsulated = false;
                else {
                    if (LOG.isTraceEnabled())
                        LOG.trace("Unrecognized starting character in address field: {}", m.group(1));
                    p.isEncapsulated = false;
                }

                if (m.group(3).startsWith("P")) {
                    p.talker = Talkers.P;
                    p.manufacturer = Manufacturers.valueOf((m.group(3) + m.group(4)).toUpperCase());
                } else if (Talkers.isValid(m.group(2).toUpperCase())) {
                    p.talker = Talkers.valueOf(m.group(2).toUpperCase());
                } else {
                    p.talker = null;
                    if (LOG.isTraceEnabled())
                        LOG.trace("Unrecognized/invalid talker type: {}", m.group(2));
                }

                p.format = ByteArrayUtils.str2bArray(m.group(4));
                p.isQuery = m.group(5).equals("Q");
            } else {
                LOG.trace("Preamble {} appears to be invalid and does not match the format: {}", rawPreamble, PREAMBLE);
            }

            return p;
        }
    }
}
