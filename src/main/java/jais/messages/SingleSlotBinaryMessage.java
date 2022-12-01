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
import java.util.BitSet;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class SingleSlotBinaryMessage extends AISMessageBase {

    private boolean addressed;
    private boolean structured;
    private int destMmsi;
    private int dac;
    private int fid;
    private BitSet data;

    /**
     *
     * @param source the name of the source of the AISSentence(s)
     * @param sentences the AISSentence(s) from which the message should be composed
     */
    public SingleSlotBinaryMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source the name of the source of the AISSentence(s)
     * @param type the AISMessageType of the sentence (used in the creation of messages with subtypes)
     * @param sentences the AISSentence(s) from which the message should be composed
     */
    public SingleSlotBinaryMessage(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     * 
     */
    @Override
    public final void decode() {
        super.decode();

        for (SingleSlotBinaryMessageFieldMap field : SingleSlotBinaryMessageFieldMap.values()) {
            switch (field) {
                case ADDRESSED:
                    if (bits.size() >= field.getStartBit())
                        addressed = bits.get(field.getStartBit());
                    break;
                case STRUCTURED:
                    structured = bits.get(field.getStartBit());
                    break;
                case DESTINATION_MMSI:
                    if (this.addressed) {
                        if (bits.size() >= field.getStartBit())
                            destMmsi = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    }
                    break;
                case DAC:
                    if (this.structured) {
                        if (bits.size() >= field.getStartBit())
                            dac = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    }
                    break;
                case FID:
                    if (this.structured) {
                        if (bits.size() >= field.getStartBit())
                            fid = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    }
                    break;
                case DATA:
                    data = new BitSet(bits.size());
                    if (this.addressed && bits.size() >= 70) {
                        if (bits.size() >= field.getStartBit())
                            data = bits.get(70, 70);
                    } else if (this.structured && bits.size() >= 56) {
                        if (bits.size() >= field.getStartBit())
                            data = bits.get(56, bits.size());
                    } else if (bits.size() >= 40) {
                        if (bits.size() >= field.getStartBit())
                            data = bits.get(40, bits.size());
                    }
                    break;
                default:
                    // ignore field
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum SingleSlotBinaryMessageFieldMap implements FieldMap {

        ADDRESSED(38, 38),
        STRUCTURED(39, 39),
        DESTINATION_MMSI(40, 70), // as many as 30 bits
        DAC(40, 59), // as many as 10 bits
        FID(50, 55), // as many as 6 bits
        DATA(40, -1); // as many as 128 bits

        // from gpsd.berlios.de/AIVDM.html#_type_25_single_slot_binary_message
        // If the addressed flag is on, 30 bits of data at offset 40 are interpreted
        // as a destination MMSI. Otherwise that field span becomes part of the
        // message payload, with the first 16 bits used as an Application ID if
        // the structured flag is on. If the structured flag is on, a 16-bit
        // application identifier is extracted; this field is to be interpreted
        // as a 10 bit DAC and 6-bit FID as in message types 6 and 8. Otherwise
        // that field span becomes part of the message payload.
        // The data fields are not, in contrast to message type 26, followed by a
        // radio status block. Note: Type 25 is extremely rare. As of April 2011 it
        // has not been observed even in long-duration samples from AISHub.
        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit that should be considered for the decoding of the given field
         * @param endBit the last bit that should be considered for the decoding of the given field
         */
        SingleSlotBinaryMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
