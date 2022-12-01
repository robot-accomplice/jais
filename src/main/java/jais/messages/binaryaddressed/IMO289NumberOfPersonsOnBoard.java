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

import jais.messages.AISMessageDecoder;
import jais.AISSentence;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import jais.messages.enums.BinaryAddressedMessageType;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class IMO289NumberOfPersonsOnBoard extends BinaryAddressedMessageBase {

    private int persons;

    /**
     *
     * @param source the name of the source for this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289NumberOfPersonsOnBoard(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.NUMBER_OF_PERSONS_ON_BOARD, sentences);
    }

    /**
     *
     * @return the number of persons on the vessel
     */
    public int getPersons() {
        return persons;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (IMO289NumberOfPersonsOnBoardFieldMap field : IMO289NumberOfPersonsOnBoardFieldMap.values()) {

            if (field == IMO289NumberOfPersonsOnBoardFieldMap.PERSONS) {
                persons = AISMessageDecoder.decodeUnsignedInt(bits,
                        field.getStartBit(), field.getEndBit());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289NumberOfPersonsOnBoardFieldMap implements FieldMap {

        PERSONS(88, 100),
        SPARE(101, 135);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO289NumberOfPersonsOnBoardFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
