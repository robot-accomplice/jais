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
import lombok.Getter;
import lombok.Setter;
import jais.messages.enums.AISMessageType;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class DataLinkManagementMessage extends AISMessageBase {

    private int offset1 = -1;
    private int slots1 = -1;
    private int timeout1 = -1;
    private int increment1 = -1;
    private int offset2 = -1;
    private int slots2 = -1;
    private int timeout2 = -1;
    private int increment2 = -1;
    private int offset3 = -1;
    private int slots3 = -1;
    private int timeout3 = -1;
    private int increment3 = -1;
    private int offset4 = -1;
    private int slots4 = -1;
    private int timeout4 = -1;
    private int increment4 = -1;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public DataLinkManagementMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source the name of the source of this message
     * @param type the type of AISMessage
     * @param sentences the AIS sentences from which this message was composed
     */
    public DataLinkManagementMessage(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (DataLinkManagementMessageFieldMap field : DataLinkManagementMessageFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case OFFSET1 -> this.offset1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case SLOTS1 -> this.slots1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TIMEOUT1 -> this.timeout1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case INCREMENT1 -> this.increment1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case OFFSET2 -> this.offset2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case SLOTS2 -> this.slots2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TIMEOUT2 -> this.timeout2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case INCREMENT2 -> this.increment2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case OFFSET3 -> this.offset3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case SLOTS3 -> this.slots3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TIMEOUT3 -> this.timeout3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case INCREMENT3 -> this.increment3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case OFFSET4 -> this.offset4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case SLOTS4 -> this.slots4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TIMEOUT4 -> this.timeout4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case INCREMENT4 -> this.increment4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum DataLinkManagementMessageFieldMap implements FieldMap {

        OFFSET1(40, 51),
        SLOTS1(52, 55),
        TIMEOUT1(56, 58),
        INCREMENT1(59, 69),
        OFFSET2(70, 81),
        SLOTS2(82, 85),
        TIMEOUT2(86, 88),
        INCREMENT2(89, 99),
        OFFSET3(100, 111),
        SLOTS3(112, 115),
        TIMEOUT3(116, 118),
        INCREMENT3(119, 129),
        OFFSET4(130, 141),
        SLOTS4(142, 145),
        TIMEOUT4(146, 148),
        INCREMENT4(149, 159);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        DataLinkManagementMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
