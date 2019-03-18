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

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;

/**
 *
 * @author Jonathan Machen
 */
public class AddressedSafetyRelatedMessage extends AISMessageBase {
    
    private int _destMmsi;
    private boolean _retransmit;
    private String _text;
    
    /**
     * 
     * @param source
     * @param packets 
     * @throws jais.exceptions.AISException 
     */
    public AddressedSafetyRelatedMessage( String source, AISPacket... packets ) throws AISException {
        super( source, packets );
    }
    
    /**
     * 
     * @param source
     * @param messageType
     * @param packets 
     */
    public AddressedSafetyRelatedMessage( String source, AISMessageType messageType, AISPacket... packets ) {
        super( source, messageType, packets );
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
     * @return 
     */
    public int getDestMmsi() {
        return _destMmsi;
    }

    /**
     * 
     * @return 
     */
    public boolean isRetransmit() {
        return _retransmit;
    }

    /**
     * 
     * @return 
     */
    public String getText() {
        return _text;
    }
    
    /**
     * 
     * @throws AISException 
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( AddressedSafetyRelatedMessageFieldMap field : AddressedSafetyRelatedMessageFieldMap.values() ) {
            switch( field ) {
                case DEST_MMSI:
                    _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case RETRANSMIT:
                    _retransmit = _bits.get( field.getStartBit() );
                    break;
                case TEXT:
                    _text = AISMessageDecoder.decodeToString( _bits, field.getStartBit(), _bits.size() - 1 );
                    break;
            }
        }
    }
    
    /**
     * 
     */
    private enum AddressedSafetyRelatedMessageFieldMap implements FieldMap {
        
        DEST_MMSI( 40, 69 ),
        RETRANSMIT( 70, 70 ),
        SPARE( 71, 71 ),
        TEXT( 72, -1 );
        
        private final int _startBit;
        private final int _endBit;
        
        /**
         * 
         * @param startBit
         * @param endBit 
         */
        AddressedSafetyRelatedMessageFieldMap( int startBit, int endBit ) {
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
