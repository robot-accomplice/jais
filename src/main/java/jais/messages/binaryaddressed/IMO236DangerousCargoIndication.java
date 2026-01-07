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
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.CargoUnitCode;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO236DangerousCargoIndication extends BinaryAddressedMessageBase {

    private String lastPort;
    private int lastMonth;
    private int lastDay;
    private int lastHour;
    private int lastMinute;
    private String nextPort;
    private int nextMonth;
    private int nextDay;
    private int nextHour;
    private int nextMinute;
    private String dangerous;
    private String imdCat;
    private int unId;
    private int amount;
    private CargoUnitCode cargoUnit;

    /**
     *
     * @param source the name of the source for this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO236DangerousCargoIndication(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.DANGEROUS_CARGO_INDICATION_DEPRECATED,
                sentences);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (IMO236DangerousCargoIndicationFieldMap field : IMO236DangerousCargoIndicationFieldMap.values()) {

            switch (field) {
                case LAST_PORT_OF_CALL -> this.lastPort = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LAST_ETA_MONTH -> this.lastMonth = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LAST_ETA_DAY -> this.lastDay = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LAST_ETA_HOUR -> this.lastHour = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case LAST_ETA_MINUTE -> this.lastMinute = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case NEXT_PORT_OF_CALL -> this.nextPort = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case NEXT_ETA_MONTH -> this.nextMonth = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case NEXT_ETA_DAY -> this.nextDay = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case NEXT_ETA_HOUR -> this.nextHour = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case NEXT_ETA_MINUTE -> this.nextMinute = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case DANGEROUS_GOOD -> this.dangerous = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case IMD_CATEGORY -> this.imdCat = AISMessageDecoder.decodeString(this.bits,
                        field.getStartBit(), field.getEndBit());
                case UN_NUMBER -> this.unId = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case AMOUNT_OF_CARGO -> this.amount = AISMessageDecoder.decodeUnsignedInt(this.bits,
                        field.getStartBit(), field.getEndBit());
                case UNIT_OF_QUANTITY -> {
                    int cargoCode = AISMessageDecoder.decodeUnsignedInt(this.bits,
                            field.getStartBit(), field.getEndBit());
                    this.cargoUnit = CargoUnitCode.getForCode(cargoCode);
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO236DangerousCargoIndicationFieldMap implements FieldMap {

        LAST_PORT_OF_CALL(88, 117),
        LAST_ETA_MONTH(118, 121),
        LAST_ETA_DAY(122, 126),
        LAST_ETA_HOUR(127, 131),
        LAST_ETA_MINUTE(132, 137),
        NEXT_PORT_OF_CALL(138, 167),
        NEXT_ETA_MONTH(168, 171),
        NEXT_ETA_DAY(172, 176),
        NEXT_ETA_HOUR(177, 181),
        NEXT_ETA_MINUTE(182, 187),
        DANGEROUS_GOOD(188, 307),
        IMD_CATEGORY(308, 331),
        UN_NUMBER(332, 344),
        AMOUNT_OF_CARGO(345, 354),
        UNIT_OF_QUANTITY(355, 356),
        SPARE(357, 359);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO236DangerousCargoIndicationFieldMap(int startBit, int endBit) {
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
