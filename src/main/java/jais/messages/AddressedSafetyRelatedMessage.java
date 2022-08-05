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
import jais.exceptions.AISException;
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
public class AddressedSafetyRelatedMessage extends AISMessageBase {

    private int destMmsi;
    private boolean retransmit;
    private String text;

    /**
     * 
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     * @throws jais.exceptions.AISException if decoding is unsuccessful
     */
    public AddressedSafetyRelatedMessage(String source, AISSentence... packets) throws AISException {
        super(source, packets);
    }

    /**
     * 
     * @param source      String denoting the source of the packet
     * @param messageType AISMessageType
     * @param packets     AISPacket[] from which the message is composed
     */
    public AddressedSafetyRelatedMessage(String source, AISMessageType messageType, AISSentence... packets) {
        super(source, messageType, packets);
    }

    /**
     * 
     * @throws AISException if any part of the AIS message decoding fails
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (AddressedSafetyRelatedMessageFieldMap field : AddressedSafetyRelatedMessageFieldMap.values()) {
            switch (field) {
                case DEST_MMSI:
                    destMmsi = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case RETRANSMIT:
                    retransmit = bits.get(field.getStartBit());
                    break;
                case TEXT:
                    text = AISMessageDecoder.decodeToString(bits, field.getStartBit(), bits.size() - 1);
                    break;
                case SPARE:
                    break;
            }
        }
    }

    /**
     * 
     */
    @Getter
    private enum AddressedSafetyRelatedMessageFieldMap implements FieldMap {

        DEST_MMSI(40, 69),
        RETRANSMIT(70, 70),
        SPARE(71, 71),
        TEXT(72, -1);

        private final int startBit;
        private final int endBit;

        /**
         * 
         * @param startBit
         * @param endBit
         */
        AddressedSafetyRelatedMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
