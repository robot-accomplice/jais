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

package jais.messages;

import jais.AISSentence;
import jais.ByteArrayUtils;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.ShipType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class StaticAndVoyageRelatedData extends AISMessageBase {

    private int version;
    private int imo;
    private byte[] callSign;
    private byte[] shipName;
    private ShipType shipType = ShipType.OTHER_NO_INFO;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private EPFDFixType epfd;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private float draught;
    private byte[] destination;
    private boolean dte;
    private boolean spare;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public StaticAndVoyageRelatedData(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public StaticAndVoyageRelatedData(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     *
     * @return A String representation of the vessel callsign
     */
    public String getCallSign() {
        return ByteArrayUtils.bArray2Str(this.callSign);
    }

    /**
     *
     * @return a String representation of the ship's name
     */
    public String getShipName() {
        return ByteArrayUtils.bArray2Str(this.shipName);
    }

    /**
     *
     * @return a ZonedDateTime representation of the ship's ETA
     */
    public String getETA() {
        return String.format("%02d-%02d %02d:%02d", month, day, hour, minute);
    }

    /**
     *
     * @return a String representation of the programmed destination
     */
    public String getDestination() {
        if (this.destination == null)
            return null;
        return ByteArrayUtils.bArray2Str(this.destination);
    }

    /**
     *
     * @return a boolean representation of the inverse of the message dte value
     */
    public boolean dteReady() {
        return !this.dte;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (StaticAndVoyageFieldMap field : StaticAndVoyageFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case VERSION -> this.version = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    case IMO -> this.imo = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case CALL_SIGN -> this.callSign = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    case SHIP_NAME -> this.shipName = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    case SHIP_TYPE -> {
                        int shipCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        this.shipType = ShipType.getForCode(shipCode);
                        if (this.shipType == null) {
                            this.shipType = ShipType.OTHER_NO_INFO;
                        }
                    }
                    case TO_BOW -> this.toBow = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_STERN -> this.toStern = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_PORT -> this.toPort = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_STARBOARD -> this.toStarboard = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    case EPFD -> {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        this.epfd = EPFDFixType.getForCode(epfdCode);
                    }
                    case ETA_MONTH -> this.month = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case ETA_DAY -> this.day = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case ETA_HOUR -> this.hour = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case ETA_MINUTE -> this.minute = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DRAUGHT -> this.draught = AISMessageDecoder.decodeDraught(bits, field.getStartBit(), field.getEndBit());
                    case DESTINATION -> this.destination = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    case DTE -> this.dte = bits.get(field.getStartBit());
                    case SPARE -> this.spare = bits.get(field.getStartBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum StaticAndVoyageFieldMap implements FieldMap {

        VERSION(38, 39),
        IMO(40, 69),
        CALL_SIGN(70, 111),
        SHIP_NAME(112, 231),
        SHIP_TYPE(232, 239),
        TO_BOW(240, 248),
        TO_STERN(249, 257),
        TO_PORT(258, 263),
        TO_STARBOARD(264, 269),
        EPFD(270, 273),
        ETA_MONTH(274, 277),
        ETA_DAY(278, 282),
        ETA_HOUR(283, 287),
        ETA_MINUTE(288, 293),
        DRAUGHT(294, 301),
        DESTINATION(302, 421),
        DTE(422, 422),
        SPARE(423, 423);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the index of the first bit to include in the decoding of this field
         * @param endBit the index of the last bit to include in the decoding of this field
         */
        StaticAndVoyageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        @Override
        public int getStartBit() {
            return startBit;
        }

        @Override
        public int getEndBit() {
            return endBit;
        }
    }
}
