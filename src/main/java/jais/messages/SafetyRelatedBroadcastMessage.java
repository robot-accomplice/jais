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
public class SafetyRelatedBroadcastMessage extends AISMessageBase {
    
    private String _text;
    
    /**
     * 
     * @param packets 
     * @throws jais.exceptions.AISException 
     */
    public SafetyRelatedBroadcastMessage( AISPacket... packets ) throws AISException {
        super( packets );
    }
    
    /**
     * 
     * @param messageType
     * @param packets 
     */
    public SafetyRelatedBroadcastMessage( AISMessageType messageType, AISPacket... packets ) {
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
        
        for( SRBMFieldMap field : SRBMFieldMap.values() ) {
            switch( field ) {
                case TEXT:
                    _text = AISMessageDecoder.decodeString( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }
    
    /**
     * 
     */
    private enum SRBMFieldMap implements FieldMap {
        
        TEXT( 40, -1 );
        
        private final int _startBit;
        private final int _endBit;
        
        /**
         * 
         * @param startBit
         * @param endBit 
         */
        SRBMFieldMap( int startBit, int endBit ) {
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