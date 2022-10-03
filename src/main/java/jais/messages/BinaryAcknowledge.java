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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class BinaryAcknowledge extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(BinaryAddressedMessageBase.class);

    private int mmsi1;
    private int mmsi2;
    private int mmsi3;
    private int mmsi4;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public BinaryAcknowledge(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public BinaryAcknowledge(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     * 
     * @return int representing the MMSI of the super class
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

        for (BinaryAcknowledgeFieldMap field : BinaryAcknowledgeFieldMap.values()) {
            switch (field) {
                case MMSI1:
                    if (bits.size() >= field.getStartBit())
                        mmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MMSI2:
                    if (bits.size() >= field.getStartBit())
                        mmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MMSI3:
                    if (bits.size() >= field.getStartBit())
                        mmsi3 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MMSI4:
                    if (bits.size() >= field.getStartBit())
                        mmsi4 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
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
    private enum BinaryAcknowledgeFieldMap implements FieldMap {

        SPARE1(38, 39),
        MMSI1(40, 69),
        // optional
        SPARE2(70, 71),
        MMSI2(72, 101),
        SPARE3(102, 103),
        MMSI3(104, 133),
        SPARE4(134, 135),
        MMSI4(136, 165),
        SPARE5(166, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit int
         * @param endBit   int
         */
        BinaryAcknowledgeFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
