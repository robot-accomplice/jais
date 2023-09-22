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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;

import lombok.Data;

/**
 * An object representing the TagBlock section of an AIS packet. A "tagblock" is
 * a comma separated collection of fields that appear before the
 * message preamble (see example below) and provide a spec compliant way of
 * providing metadata about the AIS packet that travels with the packet.
 * Supported fields include:
 * c - timestamp : in c unix time and represented as a positive integer
 * d - destination : a string of 15 characters or less indicating a destination
 * g - sentence grouping: a numeric string used to indicate when messages are
 * associated and their proper message order
 * n - line count : a positive integer indicating the number of lines for a
 * given message
 * r - relative time : a positive integer representing the relative time
 * differential
 * s - source id : a string of 15 characters or less representing the message
 * source
 * t - text string : a text string of 15 characters or less containing any data
 * the sender cares to include
 * Example:
 * \g:1-2-73874,n:157036,s:r003669945,c:1241544035,t:*4A\!AIVDM,1,1,,B,15N4cJ`005Jrek0H@9n`DW5608EP,0*13
 * 
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Data
public final class TagBlock {

    public final static String TAGBLOCK_STRING = "\\\\(([cdgnrst]:([\\w\\-().])+,?)+)\\*([A-Za-z0-9]{2})\\\\";
    public final static Pattern TAGBLOCK_PATTERN = Pattern.compile(TAGBLOCK_STRING);
    public final static String UNKNOWN_SOURCE = "UNKNOWN";

    boolean parsed;

    byte[] rawTagBlock;
    byte[] checksum;

    // c unix time, positive int
    long timestamp;
    // d destination, alphanumeric (<= 15 chars)
    byte[] destination;
    // g sentence grouping, numeric string (e.g. \g:1-1-1234 or \g:1-2-1234
    byte[] sentenceGrouping;
    // n line count, positive int
    int lineCount;
    // r relative time, positive int
    long relativeTime;
    // s source id, alphanumeric (<= 15 chars)
    byte[] source = ByteArrayUtils.str2bArray(TagBlock.UNKNOWN_SOURCE);
    // t text string (<= 15 chars)
    byte[] textStr;

    /**
     * No argument constructor
     */
    public TagBlock() {
    }

    /**
     * Returns a boolean indicating whether or not this Tagblock includes a
     * timestamp
     * 
     * @return a boolean indicating whether or not this TagBlock includes a
     *         timestamp
     */
    public boolean hasTimestamp() {
        return (this.timestamp > 0);
    }

    /**
     * Returns true if there is destination data in this TagBlock
     * 
     * @return a boolean indicating whether or not this TagBlock contains a
     *         destination
     */
    public boolean hasDestination() {
        return (this.destination != null);
    }

    /**
     * Returns true if we have a sentence grouping value
     * 
     * @return a boolean indicating whether or not the TagBlock contains sentence
     *         grouping data
     */
    public boolean hasSentenceGrouping() {
        return (this.sentenceGrouping != null);
    }

    /**
     * Returns true if there is a line count value in this TagBlock
     * 
     * @return a boolean indicating whether or not there is a line count value in
     *         this TagBlock
     */
    public boolean hasLineCount() {
        return (this.lineCount != 0);
    }

    /**
     * Returns true if there is a relative time value for this TagBlock
     * 
     * @return a boolean indicating whether or not this TagBlock contains a relative
     *         time value
     */
    public boolean hasRelativeTime() {
        return (this.relativeTime > 0);
    }

    /**
     * Returns true if there is source data in the TagBlock
     * 
     * @return a boolean indicating whether or not the TagBlock contains source data
     */
    public boolean hasSource() {
        return (this.source != null);
    }

    /**
     * Returns true if there is text string data in the TagBlock
     * 
     * @return a boolean indicating whether or not the TagBlock contains text string
     *         data
     */
    public boolean hasTextStr() {
        return (this.textStr != null);
    }

    /**
     * Creates a new TagBlock object based on a current timestamp and the provided
     * "source" value
     *
     * @param source a byte [] representing the tagBlock to create
     * @return a newly created TagBlock object based on the current timestamp and
     *         the provided source value
     */
    public static TagBlock build(byte[] source) {
        if (source.length > 15) {
            source = Arrays.copyOfRange(source, 0, 15);
        }

        TagBlock tb = new TagBlock();
        tb.setSource(source);
        tb.setTimestamp(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond());

        return tb;
    }

    /**
     * Parses the given String and builds a TagBlock object out of it. If the source
     * parameter is not null this
     * method will ignore the source embedded in the rawTagBlock parameter (or lack
     * there of) in favor of the provided
     * source value.
     *
     * @param rawTagBlock a String containing the unparsed TagBlock data
     * @return a new TagBlock object based on the parsing of the provided String and
     *         source information
     */
    public static TagBlock parse(String rawTagBlock) {
        TagBlock tb = new TagBlock();

        // substring starts at 1 to remove leading \
        for (String part : ByteArrayUtils.fastSplit(rawTagBlock.substring(1, rawTagBlock.indexOf("*")))) {
            String[] tag = ByteArrayUtils.fastSplit(part, ':');

            switch (tag[0]) {
                case "c" -> tb.setTimestamp(Long.parseLong(tag[1]));
                case "d" -> tb.setDestination(ByteArrayUtils.str2bArray(tag[1]));
                case "g" -> tb.setSentenceGrouping(ByteArrayUtils.str2bArray(tag[1]));
                case "n" -> tb.setLineCount(Integer.parseInt(tag[1]));
                case "r" -> tb.setRelativeTime(Long.parseLong(tag[1]));
                case "s" -> tb.setSource(ByteArrayUtils.str2bArray(tag[1]));
                case "t" -> tb.setTextStr(ByteArrayUtils.str2bArray(tag[1]));
            }
        }

        tb.setChecksum(rawTagBlock.substring(rawTagBlock.indexOf("*") + 1, rawTagBlock.length() - 1).getBytes());

        tb.setParsed(true);
        tb.setRawTagBlock(ByteArrayUtils.str2bArray(tb.toString()));

        return tb;
    }

    /**
     *
     * @return the AIS formatted String representation of this object
     */
    @Override
    public String toString() {
        // c unix time, positive int
        // d destination, alphanumeric (<= 15 chars)
        // g sentence grouping, numeric string (e.g. \g:1-1-1234 or \g:1-2-1234
        // n line count, positive int
        // r relative time, positive int
        // s source id, alphanumeric (<= 15 chars)
        // t text string

        StringBuilder tbs = new StringBuilder();

        if (this.sentenceGrouping != null && this.sentenceGrouping.length != 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("g:").append(ByteArrayUtils.bArray2Str(this.sentenceGrouping));
        }
        if (this.lineCount > 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("n:").append(this.lineCount);
        }
        if (this.source != null && this.source.length != 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("s:").append(ByteArrayUtils.bArray2Str(this.source));
        }
        if (this.timestamp > 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("c:").append(this.timestamp);
        }
        if (this.destination != null && this.destination.length != 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("d:").append(ByteArrayUtils.bArray2Str(this.destination));
        }
        if (this.relativeTime > 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("r:").append(this.relativeTime);
        }
        if (this.textStr != null && this.textStr.length != 0) {
            if (tbs.length() > 1)
                tbs.append(",");
            tbs.append("t").append(ByteArrayUtils.bArray2Str(this.textStr));
        }

        // checksum is only relevant to the content within the paired backslashes
        String checksum = (this.checksum == null || this.checksum.length == 0) ?
                Checksum.generateChecksum(tbs.toString()).toHexString() : ByteArrayUtils.bArray2Str(this.checksum);

        tbs.append("*").append(checksum).append("\\");

        return "\\" + tbs;
    }
}
