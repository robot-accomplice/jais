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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class MultipleSlotBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(MultipleSlotBinaryMessage.class);

    private boolean addressed;
    private boolean structured;
    private int destMmsi;
    private int dac;
    private int fid;
    private BitSet data;
    private int radio;

    /**
     *
     * @param source
     * @param packets
     */
    public MultipleSlotBinaryMessage(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public MultipleSlotBinaryMessage(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (MultipleSlotBinaryMessageFieldMap field : MultipleSlotBinaryMessageFieldMap.values()) {
            switch (field) {
                case ADDRESSED:
                    if (bits.size() >= field.getStartBit())
                        addressed = bits.get(field.getStartBit());
                    break;
                case STRUCTURED:
                    if (bits.size() >= field.getStartBit())
                        structured = bits.get(field.getStartBit());
                    break;
                case DESTINATION_MMSI:
                    if (addressed) {
                        if (bits.size() >= field.getStartBit())
                            destMmsi = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    }
                    break;
                case DAC:
                    if (structured) {
                        if (bits.size() >= field.getStartBit())
                            dac = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    }
                    break;
                case FID:
                    if (structured) {
                        if (bits.size() >= field.getStartBit())
                            fid = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    }
                    break;
                case DATA:
                    if (addressed && bits.length() >= 160) {
                        data = bits.get(70, (bits.size() - 90));
                    } else if (structured && bits.length() >= 166) {
                        data = bits.get(56, (bits.size() - 76));
                    } else if (bits.length() > 61) {
                        data = bits.get(40, bits.size());
                    } else {
                        LOG.trace("Invalid bit count.  BitVector size: " + bits.size()
                                + ", BitVector length: " + bits.length());
                    }
                    break;
                case RADIO:
                    radio = AISMessageDecoder.decodeUnsignedInt(bits,
                            bits.size() - 21, bits.size() + 1);
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum MultipleSlotBinaryMessageFieldMap implements FieldMap {

        ADDRESSED(38, 38),
        STRUCTURED(39, 39),
        DESTINATION_MMSI(40, 70),
        DAC(40, 50), // depends on the value of structured
        FID(50, 56), // depends on the value of structured
        DATA(-1, -1), // could be anywhere from 0-1004 bits
        RADIO(-1, 20);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        MultipleSlotBinaryMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
