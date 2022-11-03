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

package jais.messages.binaryaddressed;

import jais.AISSentence;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import jais.messages.enums.BinaryAddressedMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289TextDescription extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger(IMO289TextDescription.class);

    private int linkageId;
    private String description;

    /**
     *
     * @param source the name of the source for this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289TextDescription(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.TEXT_DESCRIPTION, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (IMO289TextDescriptionFieldMap field : IMO289TextDescriptionFieldMap.values()) {
            switch (field) {
                case LINKAGE_ID -> linkageId = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
                case DESCRIPTION -> description = AISMessageDecoder.decodeToString(bits,
                        field.getStartBit(), bits.size() - 1);
                default -> LOG.warn("Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289TextDescriptionFieldMap implements FieldMap {

        LINKAGE_ID(88, 97),
        DESCRIPTION(98, -1);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO289TextDescriptionFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
