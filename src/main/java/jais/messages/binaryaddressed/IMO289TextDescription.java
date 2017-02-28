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

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import jais.messages.enums.BinaryAddressedMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289TextDescription extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger( IMO289TextDescription.class );

    private int _linkageId;
    private String _description;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289TextDescription( String source, AISPacket... packets )
            throws AISException {
        super( source, BinaryAddressedMessageType.TEXT_DESCRIPTION, packets );
    }

    /**
     *
     * @return
     */
    public int getLinkageId() {
        return _linkageId;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289TextDescriptionFieldMap field
                : IMO289TextDescriptionFieldMap.values() ) {
            switch( field ) {
                case LINKAGE_ID:
                    _linkageId = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DESCRIPTION:
                    _description = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), _bits.size() - 1 );
                    break;
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289TextDescriptionFieldMap implements FieldMap {

        LINKAGE_ID( 88, 97 ),
        DESCRIPTION( 98, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO289TextDescriptionFieldMap( int startBit, int endBit ) {
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
