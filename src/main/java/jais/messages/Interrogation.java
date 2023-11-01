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
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class Interrogation extends AISMessageBase {

    private int mmsi1; // interrogated mmsi
    private int type1_1; // first message type
    private int offset1_1;
    private int type1_2;
    private int offset1_2;
    private int mmsi2;
    private int type2_1;
    private int offset2_1;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public Interrogation(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public Interrogation(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     *
     * @return the MMSI of the source of the interrogation
     */
    public int getSourceMmsi() {
        return super.getMmsi();
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (InterrogationFieldMap field : InterrogationFieldMap.values()) {
            if (bits.size() > field.getEndBit() || field.getEndBit() == -1) {
                switch (field) {
                    case MMSI1 -> mmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TYPE1_1 -> type1_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case OFFSET1_1 -> offset1_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TYPE1_2 -> type1_2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case OFFSET1_2 -> offset1_2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MMSI2 -> mmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TYPE2_1 -> type2_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case OFFSET2_1 -> offset2_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum InterrogationFieldMap implements FieldMap {

        SPARE1(38, 39),
        MMSI1(40, 69),
        TYPE1_1(70, 75),
        OFFSET1_1(76, 87),
        SPARE2(88, 89),
        TYPE1_2(90, 95),
        OFFSET1_2(96, 107),
        SPARE3(108, 109),
        MMSI2(110, 139),
        TYPE2_1(140, 145),
        OFFSET2_1(146, 157),
        SPARE4(158, 159);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        InterrogationFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
