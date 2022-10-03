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
public class IMO236NumberOfPersonsOnBoard extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger(IMO236NumberOfPersonsOnBoard.class);

    private int persons;

    /**
     *
     * @param source
     * @param packets
     * 
     */
    public IMO236NumberOfPersonsOnBoard(String source, AISSentence... packets) {
        super(source, BinaryAddressedMessageType.NUMBER_OF_PERSONS_ON_BOARD_DEPRECATED,
                packets);
    }

    /**
     *
     * @return
     */
    public int getPersons() {
        return persons;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (IMO236NumberOfPersonsOnBoardFieldMap field : IMO236NumberOfPersonsOnBoardFieldMap.values()) {
            switch (field) {
                case PERSONS:
                    persons = AISMessageDecoder.decodeUnsignedInt(bits,
                            field.getStartBit(), field.getEndBit());
                    break;
                default:
                    LOG.warn("Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO236NumberOfPersonsOnBoardFieldMap implements FieldMap {

        PERSONS(55, 68),
        SPARE(69, 71);

        private final int startBit;

        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        IMO236NumberOfPersonsOnBoardFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
