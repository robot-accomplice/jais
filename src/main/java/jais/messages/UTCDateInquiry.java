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

package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;

/**
 *
 * @author Jonathan Machen
 */
public class UTCDateInquiry extends AISMessageBase {
    
    private int _destMmsi;
    
    /**
     * 
     * @param packets 
     * @throws jais.exceptions.AISException 
     */
    public UTCDateInquiry( AISPacket... packets ) throws AISException {
        super( packets );
    }
    
    /**
     * 
     * @param messageType
     * @param packets 
     */
    public UTCDateInquiry( AISMessageType messageType, AISPacket... packets ) {
        super( messageType, packets );
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
     * @throws AISException 
     */
    @Override
    public final void decode() throws AISException {
        super.decode();
        
        for( UTCDateInquiryFieldMap field : UTCDateInquiryFieldMap.values() ) {
            switch( field ) {
                case DEST_MMSI:
                    _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }
    
    /**
     * 
     */
    private enum UTCDateInquiryFieldMap implements FieldMap {
        
        SPARE1( 38, 39 ),
        DEST_MMSI( 40, 69 ),
        SPARE2( 70, 71 );
        
        private final int _startBit;
        private final int _endBit;
        
        /**
         * 
         * @param startBit
         * @param endBit 
         */
        UTCDateInquiryFieldMap( int startBit, int endBit ) {
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
