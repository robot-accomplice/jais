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
import jais.exceptions.AISException;
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
public class IMO289AreaNotice extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger(IMO289AreaNotice.class);

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289AreaNotice(String source, AISSentence... packets)
            throws AISException {
        super(source, BinaryAddressedMessageType.AREA_NOTICE, packets);
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        // here we need to figure out how many elements in an array of sub-
        // areas there are (could be up to ten) based on the size of
        // remaining data after we decode the duration -- may use a public
        // static inner class to represent the sub-area information and just store
        // the array
        for (IMO289AreaNoticeFieldMap field : IMO289AreaNoticeFieldMap.values()) {
            switch (field) {
                default:
                    LOG.warn("Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289AreaNoticeFieldMap implements FieldMap {

        DEFAULT(-1, -1);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        IMO289AreaNoticeFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
