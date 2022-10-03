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
import jais.ByteArrayUtils;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.MMSIType;
import lombok.Data;
import lombok.Getter;

import java.time.ZoneOffset;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Data
public abstract class AISMessageBase implements AISMessage {

    private final static Logger LOG = LogManager.getLogger(AISMessageBase.class);

    private int mmsi;
    private MMSIType mmsiType;
    private int repeat; // bits 6-7

    protected final static SpatialContext CTX = SpatialContext.GEO;

    private byte[] source = ByteArrayUtils.str2bArray("UNKNOWN");
    final AISSentence[] sentences;
    private byte[] compositeMsg;
    private AISMessageType messageType;
    protected BitSet bits;
    protected Point position;
    private final Map<String, Object> decodedFieldMap = new HashMap<>();

    /**
     *
     * @param source    the message source
     * @param sentences the array of sentences used to compose this message
     */
    AISMessageBase(String source, AISSentence... sentences) {
        if (source != null)
            this.source = ByteArrayUtils.str2bArray(source);
        this.sentences = sentences;
    }

    /**
     *
     * @param source      the message source
     * @param messageType the specific type of message we are dealing with
     * @param sentences   the array of sentences used to compose this message
     */
    AISMessageBase(String source, AISMessageType messageType, AISSentence... sentences) {
        if (source != null)
            this.source = ByteArrayUtils.str2bArray(source);
        this.messageType = messageType;
        this.sentences = sentences;
    }

    /**
     *
     * @return this message converted into a String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AISMessage:{messageType:");

        sb.append(messageType.name()).append(",sentences:[");
        for (AISSentence packet : this.sentences)
            sb.append(packet.toString());
        sb.append("],").append("}");

        return sb.toString();
    }

    /**
     *
     * @return The source value
     */
    @Override
    public String getSource() {
        return ByteArrayUtils.bArray2Str(source);
    }

    /**
     *
     * @param source the source value
     */
    @Override
    public void setSource(String source) {
        this.source = ByteArrayUtils.str2bArray(source);
    }

    /**
     *
     * @return the time received
     */
    @Override
    public ZonedDateTime getTimeReceived(ZoneOffset offset) {
        return sentences[0].getTimeReceived(offset);
    }

    /**
     *
     * @return the time received
     */
    @Override
    public long getTimeReceived() {
        return sentences[0].getTimeReceived();
    }

    /**
     *
     * @return the time the message was sent
     */
    public long getTimeSent() {
        return sentences[0].getTimeSent();
    }

    /**
     * Type of AIS message pulled from bits 0 - 6
     *
     * @return the specific message type
     */
    @Override
    public AISMessageType getType() {
        return messageType;
    }

    /**
     *
     * @param type the specific message type
     */
    @Override
    public void setType(AISMessageType type) {
        messageType = type;
    }

    /**
     *
     * @return the array of sentences with which this message was composed
     */
    @Override
    public AISSentence[] getSentences() {
        return sentences;
    }

    /**
     *
     * @return the MMSI as an int
     */
    @Override
    public int getMmsi() {
        return mmsi;
    }

    /**
     *
     * @return a boolean indicating whether or not the MMSI is valid
     */
    @Override
    public boolean hasValidMmsi() {
        return isValidMmsi(mmsi);
    }

    /**
     *
     * @param mmsi the MMSI
     * @return a boolean indicating whether or not the MMSI is valid
     */
    private static boolean isValidMmsi(long mmsi) {
        boolean valid = ((mmsi < 800000000) && (mmsi > 199999999));

        if (!valid)
            LOG.warn("MMSI: {} is not valid!", mmsi);

        return valid;
    }

    /**
     *
     * @return the type of the MMSI
     */
    @Override
    public MMSIType getMMSIType() {
        if (mmsiType == null || mmsiType == MMSIType.UNKNOWN)
            mmsiType = MMSIType.forMMSI(mmsi);

        return mmsiType;
    }

    /**
     *
     * @return the MMSI's country of origin
     */
    public String getCountryOfOrigin() {
        return getMMSIType().name();
    }

    /**
     *
     * @return the repeat value of the message
     */
    @Override
    public int getRepeat() {
        return repeat;
    }

    /**
     *
     * @return a boolean indicating whether or not there is a sybtype for this
     *         message
     */
    @Override
    public boolean hasSubType() {
        return false;
    }

    /**
     *
     * @return an instance of AISMessage based on the correct subtype of the current
     *         message
     *         create a subtype instance for any reason
     */
    @Override
    public AISMessage getSubTypeInstance() {
        return this;
    }

    /**
     *
     * @return the FieldMap array for the current concrete instance of a message
     */
    @Override
    public FieldMap[] getFieldMap() {
        return AISFieldMap.values();
    }

    /**
     *
     * @return a boolean indicating whether or not this message contains positional
     *         data
     */
    @Override
    public boolean hasPosition() {
        return false;
    }

    /**
     *
     * @return the positional data for this message
     */
    @Override
    public Point getPosition() {
        return position;
    }

    /**
     *
     */
    @Override
    public void decode() {
        this.decodedFieldMap.put("timereceived", getTimeReceived());

        this.compositeMsg = AISSentence.concatenate(getSentences());

        this.bits = AISMessageDecoder.byteArrayToBitSet(this.compositeMsg);

        for (AISFieldMap field : AISFieldMap.values()) {
            switch (field) {
                case TYPE:
                    if (this.decodedFieldMap.get("messagetype") == null) {
                        Optional<AISMessageType> mType = AISMessageDecoder.decodeMessageType(this.bits);
                        mType.ifPresent(aisMessageType -> this.messageType = aisMessageType);
                    }
                    break;
                case REPEAT:
                    this.repeat = AISMessageDecoder.decodeUnsignedInt(this.bits, field.getStartBit(),
                            field.getEndBit());
                    break;
                case MMSI:
                    this.mmsi = AISMessageDecoder.decodeUnsignedInt(this.bits, field.getStartBit(), field.getEndBit());
                    break;
            }
        }
    }

    /**
     *
     */
    @Getter
    public enum AISFieldMap implements FieldMap {

        TYPE(0, 5),
        REPEAT(6, 7),
        MMSI(8, 37);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the starting bit for this field
         * @param endBit   the ending bit for this field
         */
        AISFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
