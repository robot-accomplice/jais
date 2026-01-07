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
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import jais.messages.enums.BinaryAddressedMessageType;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class IMO289RouteInformation extends BinaryAddressedMessageBase {

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289RouteInformation(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.ROUTE_INFORMATION, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();
    }

    /**
     *
     */
    @Getter
    private enum IMO289RouteInformationFieldMap implements FieldMap {

        DEFAULT(-1, -1);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO289RouteInformationFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        @Override
        public int getStartBit() {
            return startBit;
        }

        @Override
        public int getEndBit() {
            return endBit;
        }
    }
}
