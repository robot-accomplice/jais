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
public class Interrogation extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(Interrogation.class);

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
     * @param source
     * @param sentences
     */
    public Interrogation(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source
     * @param type
     * @param sentences
     */
    public Interrogation(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
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
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (InterrogationFieldMap field : InterrogationFieldMap.values()) {
            switch (field) {
                case MMSI1:
                    if (bits.size() >= field.getStartBit())
                        mmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TYPE1_1:
                    if (bits.size() >= field.getStartBit())
                        type1_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case OFFSET1_1:
                    if (bits.size() >= field.getStartBit())
                        offset1_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TYPE1_2:
                    if (bits.size() >= field.getStartBit())
                        type1_2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case OFFSET1_2:
                    if (bits.size() >= field.getStartBit())
                        offset1_2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MMSI2:
                    if (bits.size() >= field.getStartBit())
                        mmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TYPE2_1:
                    if (bits.size() >= field.getStartBit())
                        type2_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case OFFSET2_1:
                    if (bits.size() >= field.getStartBit())
                        offset2_1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
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
         * @param startBit
         * @param endBit
         */
        InterrogationFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
