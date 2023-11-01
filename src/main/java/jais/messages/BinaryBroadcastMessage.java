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
     * @param sentences AISPacket[] from which the message is composed
     */
    public BinaryBroadcastMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param sentences AISPacket[] from which the message is composed
     */
    public BinaryBroadcastMessage(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
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
     */
    @Override
    public void decode() {
        super.decode();

        for (BinaryBroadcastMessageBaseFieldMap field : BinaryBroadcastMessageBaseFieldMap.values()) {
            if (bits.size() > field.getEndBit() || field.getEndBit() == -1) {
                switch (field) {
                    case DAC -> dac = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case FID -> fid = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DATA -> {
                        // store the undecoded portion of the BitSet in the data
                        // field for later decoding by subtype
                        data = bits.get(field.getStartBit(), bits.size() - 1);
                    }
                    case DESTINATION_MMSI, RETRANSMIT, SEQUENCE_NUMBER, SPARE -> {}
                }
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
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        BinaryBroadcastMessageBaseFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
