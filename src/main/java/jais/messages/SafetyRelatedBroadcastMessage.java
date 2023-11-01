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
public class SafetyRelatedBroadcastMessage extends AISMessageBase {

    private String text;

    /**
     * 
     * @param source
     * @param sentences
     */
    public SafetyRelatedBroadcastMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     * 
     * @param source
     * @param messageType
     * @param sentences
     */
    public SafetyRelatedBroadcastMessage(String source, AISMessageType messageType, AISSentence... sentences) {
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

        for (SRBMFieldMap field : SRBMFieldMap.values()) {
            if (field == SRBMFieldMap.TEXT) {
                this.text = AISMessageDecoder.decodeString(bits, field.getStartBit(), bits.size());
            }
        }
    }

    /**
     * 
     */
    @Getter
    private enum SRBMFieldMap implements FieldMap {

        TEXT(40, -1);

        private final int startBit;
        private final int endBit;

        /**
         * 
         * @param startBit
         * @param endBit
         */
        SRBMFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        /**
         * 
         * @return
         */
        @Override
        public int getStartBit() {
            return this.startBit;
        }

        /**
         * 
         * @return
         */
        @Override
        public int getEndBit() {
            return this.endBit;
        }
    }
}