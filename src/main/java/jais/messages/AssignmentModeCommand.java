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
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;
import jais.messages.enums.AISMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class AssignmentModeCommand extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(AssignmentModeCommand.class);

    private int mmsi1;
    private int offset1;
    private int increment1;
    private int mmsi2;
    private int offset2;
    private int increment2;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public AssignmentModeCommand(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public AssignmentModeCommand(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     * @throws AISException if any part of the decoding process fails
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (AssignmentModeCommandFieldMap field : AssignmentModeCommandFieldMap.values()) {
            switch (field) {
                case MMSI1:
                    if (bits.size() >= field.getStartBit())
                        mmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case OFFSET1:
                    if (bits.size() >= field.getStartBit())
                        offset1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case INCREMENT1:
                    if (bits.size() >= field.getStartBit())
                        increment1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case MMSI2:
                    if (bits.size() >= field.getStartBit())
                        mmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case OFFSET2:
                    if (bits.size() >= field.getStartBit())
                        offset2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case INCREMENT2:
                    if (bits.size() >= field.getStartBit())
                        increment2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
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
    private enum AssignmentModeCommandFieldMap implements FieldMap {

        SPARE(38, 39),
        MMSI1(40, 69),
        OFFSET1(70, 81),
        INCREMENT1(82, 91),
        MMSI2(92, 121),
        OFFSET2(122, 133),
        INCREMENT2(134, 143);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit int
         * @param endBit   int
         */
        AssignmentModeCommandFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
