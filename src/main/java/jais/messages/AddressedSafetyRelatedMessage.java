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
public class AddressedSafetyRelatedMessage extends AISMessageBase {
    
    private int _destMmsi;
    private boolean _retransmit;
    private String _text;
    
    /**
     * 
     * @param packets 
     * @throws jais.exceptions.AISException 
     */
    public AddressedSafetyRelatedMessage( AISPacket... packets ) throws AISException {
        super( packets );
    }
    
    /**
     * 
     * @param messageType
     * @param packets 
     */
    public AddressedSafetyRelatedMessage( AISMessageType messageType, AISPacket... packets ) {
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
        
        for( AddressedSafetyRelatedMessageFieldMap field :
                AddressedSafetyRelatedMessageFieldMap.values() ) {
            switch( field ) {
                case DEST_MMSI:
                    _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case RETRANSMIT:
                    _retransmit = _bits.get( field.getStartBit() );
                    break;
                case TEXT:
                    _text = AISMessageDecoder.decodeString( _bits, 
                            field.getStartBit(), _bits.size() - 1 );
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
