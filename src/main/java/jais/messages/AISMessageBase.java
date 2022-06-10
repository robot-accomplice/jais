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
import jais.ByteArrayUtils;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.MMSIType;
import java.time.ZoneOffset;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.shape.Point;
import java.time.ZonedDateTime;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import java.util.Arrays;
import java.util.Optional;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public abstract class AISMessageBase implements AISMessage {

    private final static Logger LOG = LogManager.getLogger(AISMessageBase.class);

    private int _mmsi;
    private MMSIType _mmsiType;
    private int _repeat; // bits 6-7

    protected final static SpatialContext CTX = SpatialContext.GEO;

    private byte[] _source = ByteArrayUtils.str2bArray("UNKNOWN");
    final AISPacket[] _packets;
    private byte[] _compositeMsg;
    private AISMessageType _messageType;
    protected BitSet _bits;
    protected Point _position;
    private final Map<String, Object> _decodedFieldMap = new HashMap<>();

    /**
     *
     * @param source  the message source
     * @param packets the array of packets used to compose this message
     */
    AISMessageBase(String source, AISPacket... packets) {
        if (source != null)
            _source = ByteArrayUtils.str2bArray(source);
        _packets = packets;
    }

    /**
     *
     * @param source      the message source
     * @param messageType the specific type of message we are dealing with
     * @param packets     the array of packets used to compose this message
     */
    AISMessageBase(String source, AISMessageType messageType, AISPacket... packets) {
        if (source != null)
            _source = ByteArrayUtils.str2bArray(source);
        _messageType = messageType;
        _packets = packets;
    }

    /**
     *
     * @return this message converted into a String
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AISMessage:{messageType:");

        sb.append(_messageType.name()).append(",packets:[");
        for (AISPacket packet : _packets)
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
        return ByteArrayUtils.bArray2Str(_source);
    }

    /**
     *
     * @param source the source value
     */
    @Override
    public void setSource(String source) {
        _source = ByteArrayUtils.str2bArray(source);
    }

    /**
     *
     * @return the time received
     */
    @Override
    public ZonedDateTime getTimeReceived(ZoneOffset offset) {
        return _packets[0].getTimeReceived(offset);
    }

    /**
     *
     * @return the time received
     */
    @Override
    public long getTimeReceived() {
        return _packets[0].getTimeReceived();
    }

    /**
     *
     * @return the time the message was sent
     */
    public long getTimeSent() {
        return _packets[0].getTimeSent();
    }

    /**
     * Type of AIS message pulled from bits 0 - 6
     *
     * @return the specific message type
     */
    @Override
    public AISMessageType getType() {
        return _messageType;
    }

    /**
     *
     * @param type the specific message type
     */
    @Override
    public void setType(AISMessageType type) {
        _messageType = type;
    }

    /**
     *
     * @return the array of packets with which this message was composed
     */
    @Override
    public AISPacket[] getPackets() {
        return _packets;
    }

    /**
     *
     * @return the MMSI as an int
     */
    @Override
    public int getMmsi() {
        return _mmsi;
    }

    /**
     *
     * @return a boolean indicating whether or not the MMSI is valid
     */
    @Override
    public boolean hasValidMmsi() {
        return isValidMmsi(_mmsi);
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
        if (_mmsiType == null || _mmsiType == MMSIType.UNKNOWN)
            _mmsiType = MMSIType.forMMSI(_mmsi);

        return _mmsiType;
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
        return _repeat;
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
     * @throws AISException if an overridden version of this method is unable to
     *                      create a subtype instance for any reason
     */
    @Override
    public AISMessage getSubTypeInstance() throws AISException {
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
        return _position;
    }

    /**
     *
     * @throws AISException if we are unable to decode the message
     */
    @Override
    public void decode() throws AISException {
        _decodedFieldMap.put("time_received", getTimeReceived());

        _compositeMsg = AISPacket.concatenate(getPackets());

        _bits = AISMessageDecoder.byteArrayToBitSet(_compositeMsg);

        for (AISFieldMap field : AISFieldMap.values()) {
            switch (field) {
                case TYPE:
                    if (_decodedFieldMap.get("message_type") == null) {
                        Optional<AISMessageType> mType = AISMessageDecoder.decodeMessageType(_bits);
                        mType.ifPresent(aisMessageType -> _messageType = aisMessageType);
                    }
                    break;
                case REPEAT:
                    _repeat = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case MMSI:
                    _mmsi = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
            }
        }
    }

    /**
     *
     * @return the calculated hashcode for this object
     */
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(this._source);
        return hash;
    }

    /**
     *
     * @param obj the object to which we wish to compare our current object
     * @return true if the objects are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final AISMessageBase other = (AISMessageBase) obj;
        if (!Arrays.equals(this._source, other._source))
            return false;

        return Arrays.equals(this._compositeMsg, other._compositeMsg);
    }

    /**
     *
     */
    public enum AISFieldMap implements FieldMap {

        TYPE(0, 5),
        REPEAT(6, 7),
        MMSI(8, 37);

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit the starting bit for this field
         * @param endBit   the ending bit for this field
         */
        AISFieldMap(int startBit, int endBit) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return the starting bit for this field
         */
        @Override
        public int getStartBit() {
            return _startBit;
        }

        /**
         *
         * @return the ending bit for this field
         */
        @Override
        public int getEndBit() {
            return _endBit;
        }
    }
}
