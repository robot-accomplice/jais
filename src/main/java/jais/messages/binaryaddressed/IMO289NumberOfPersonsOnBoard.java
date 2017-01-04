/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import jais.messages.enums.BinaryAddressedMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289NumberOfPersonsOnBoard extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO289NumberOfPersonsOnBoard.class );

    private int _persons;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289NumberOfPersonsOnBoard( String source, AISPacket... packets )
            throws AISException {
        super( source, BinaryAddressedMessageType.NUMBER_OF_PERSONS_ON_BOARD, packets );
    }

    /**
     *
     * @return
     */
    public int getPersons() {
        return _persons;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289NumberOfPersonsOnBoardFieldMap field
                : IMO289NumberOfPersonsOnBoardFieldMap.values() ) {

            switch( field ) {
                case PERSONS:
                    _persons = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289NumberOfPersonsOnBoardFieldMap implements FieldMap {

        PERSONS( 88, 100 ),
        SPARE( 101, 135 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO289NumberOfPersonsOnBoardFieldMap( int startBit, int endBit ) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getStartBit() {
            return _startBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getEndBit() {
            return _endBit;
        }
    }
}
