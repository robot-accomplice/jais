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
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.ShipType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class StaticDataReport extends AISMessageBase {

    private int partNo;
    private String shipName;
    private ShipType shipType;
    private String vendorId;
    private String callSign;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private int mothershipMmsi;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public StaticDataReport(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public StaticDataReport(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (StaticDataReportFieldMap field : StaticDataReportFieldMap.values()) {
            switch (field) {
                case PART_NUMBER -> {
                    if (bits.size() >= field.getStartBit())
                        this.partNo = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case SHIP_NAME -> {
                    if (bits.size() >= field.getStartBit())
                        this.shipName = AISMessageDecoder.decodeToString(bits, field.getStartBit(), field.getEndBit());
                }
                case SHIP_TYPE -> {
                    if (bits.size() >= field.getStartBit()) {
                        int stCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        this.shipType = ShipType.getForCode(stCode);
                    }
                }
                case VENDOR_ID -> {
                    if (bits.size() >= field.getStartBit())
                        this.vendorId = AISMessageDecoder.decodeToString(bits, field.getStartBit(), field.getEndBit());
                }
                case CALL_SIGN -> {
                    if (bits.size() >= field.getStartBit())
                        this.callSign = AISMessageDecoder.decodeToString(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_BOW -> {
                    if (bits.size() >= field.getStartBit())
                        this.toBow = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_STERN -> {
                    if (bits.size() >= field.getStartBit())
                        this.toStern = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case TO_PORT -> {
                    if (bits.size() >= field.getStartBit())
                        this.toPort = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_STARBOARD -> this.toStarboard = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                        field.getEndBit());
                case MOTHERSHIP_MMSI -> {
                    if (bits.size() >= field.getStartBit())
                        this.mothershipMmsi = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                default -> {
                }
                // ignore field
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum StaticDataReportFieldMap implements FieldMap {

        PART_NUMBER(38, 39),
        SHIP_NAME(40, 159),
        SPARE1(160, 167),
        SHIP_TYPE(40, 47),
        VENDOR_ID(48, 89),
        CALL_SIGN(90, 131),
        TO_BOW(132, 140),
        TO_STERN(141, 149),
        TO_PORT(150, 155),
        TO_STARBOARD(156, 161),
        MOTHERSHIP_MMSI(132, 161),
        SPARE2(162, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the index of the first bit to include in the decoding of this field
         * @param endBit the index of the last bit to include in the decoding of this field
         */
        StaticDataReportFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
