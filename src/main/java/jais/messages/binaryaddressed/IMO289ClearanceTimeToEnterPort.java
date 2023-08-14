/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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

package jais.messages.binaryaddressed;

import jais.AISSentence;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;
import jais.messages.AISMessageDecoder;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class IMO289ClearanceTimeToEnterPort extends BinaryAddressedMessageBase {

    private int linkageId;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private String portName;
    private String destination;
    private double lon;
    private double lat;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289ClearanceTimeToEnterPort(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.CLEARANCE_TIME_TO_ENTER_PORT, sentences);
    }

    /**
     *
     * @return whether or not the message contains position data
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return a geometric point representing the position
     */
    @Override
    public Point getPosition() {
        if (this.position == null) {
            this.position = CTX.getShapeFactory().pointXY(this.lat, this.lon);
        }

        return this.position;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (IMO289ClearanceTimeToEnterPortFieldMap field : IMO289ClearanceTimeToEnterPortFieldMap.values()) {
            switch (field) {
                case MESSAGE_LINKAGE_ID -> linkageId = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case MONTH -> month = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case DAY -> day = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case HOUR -> hour = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case MINUTE -> minute = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case PORT_NAME_AND_BERTH -> this.portName = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case DESTINATION -> this.destination = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LON -> this.lon = AISMessageDecoder.decodeLongitude(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LAT -> this.lat = AISMessageDecoder.decodeLatitude(this.bits,
                        field.getStartBit(), field.getEndBit());
                default -> {
                    // ignore field
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289ClearanceTimeToEnterPortFieldMap implements FieldMap {

        MESSAGE_LINKAGE_ID(88, 97),
        MONTH(98, 101),
        DAY(102, 106),
        HOUR(107, 111),
        MINUTE(112, 117),
        PORT_NAME_AND_BERTH(118, 237),
        DESTINATION(238, 267),
        LON(268, 292),
        LAT(293, 316),
        SPARE(317, 359);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO289ClearanceTimeToEnterPortFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
