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
public class SafetyRelatedAcknowledgement extends AISMessageBase {

    private int mmsi1;
    private int mmsi2;
    private int mmsi3;
    private int mmsi4;

    /**
     *
     * @param source
     * @param sentences
     */
    public SafetyRelatedAcknowledgement(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source
     * @param messageType
     * @param sentences
     */
    public SafetyRelatedAcknowledgement(String source, AISMessageType messageType, AISSentence... sentences) {
        super(source, messageType, sentences);
    }

    /**
     *
     * @return
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

        for (SafetyRelatedAcknowledgeFieldMap field : SafetyRelatedAcknowledgeFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case MMSI1 -> mmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MMSI2 -> mmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MMSI3 -> mmsi3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MMSI4 -> mmsi4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case SPARE1, SPARE2, SPARE3, SPARE4, SPARE5 -> {}
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum SafetyRelatedAcknowledgeFieldMap implements FieldMap {

        SPARE1(38, 39),
        MMSI1(40, 69),
        SPARE2(70, 71),
        MMSI2(62, 101),
        SPARE3(102, 103),
        MMSI3(104, 133),
        SPARE4(134, 135),
        MMSI4(136, 165),
        SPARE5(166, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        SafetyRelatedAcknowledgeFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
