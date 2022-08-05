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
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.BinaryBroadcastMessageType;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;

import java.util.BitSet;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class BinaryBroadcastMessage extends AISMessageBase {

    private int dac; // designated area code
    private int fid; // functional id
    private BitSet data;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public BinaryBroadcastMessage(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public BinaryBroadcastMessage(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     *
     * @return int
     */
    public int getSourceMmsi() {
        return super.getMmsi();
    }

    /**
     * 
     * @return BinaryBroadcastMessageType
     */
    public BinaryBroadcastMessageType getSubType() {
        return BinaryBroadcastMessageType.UNKNOWN;
    }

    /**
     *
     * @throws jais.exceptions.AISException if we are unable to decode the message
     */
    @Override
    public void decode() throws AISException {
        super.decode();

        for (BinaryBroadcastMessageBaseFieldMap field : BinaryBroadcastMessageBaseFieldMap.values()) {
            switch (field) {
                case DAC:
                    if (bits.size() >= field.getStartBit())
                        dac = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case FID:
                    if (bits.size() >= field.getStartBit())
                        fid = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case DATA:
                    // store the undecoded portion of the BitSet in the data
                    // field for later decoding by subtype
                    if (bits.length() > field.getStartBit())
                        data = bits.get(field.getStartBit(), bits.size() - 1);
                    break;
                case DESTINATION_MMSI:
                    break;
                case RETRANSMIT:
                    break;
                case SEQUENCE_NUMBER:
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
    private enum BinaryBroadcastMessageBaseFieldMap implements FieldMap {

        SEQUENCE_NUMBER(38, 39),
        DESTINATION_MMSI(40, 69),
        RETRANSMIT(70, 70),
        SPARE(71, 71),
        DAC(72, 81), // designated area code
        FID(82, 87), // Functional ID
        DATA(88, -1) // -1 means from startBit to end of bitArray
        ;

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit int
         * @param endBit   int
         */
        BinaryBroadcastMessageBaseFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
