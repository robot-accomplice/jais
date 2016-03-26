/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
