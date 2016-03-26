/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class SafetyRelatedAcknowledgement extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( SafetyRelatedAcknowledgement.class );

    private int _mmsi1;
    private int _mmsi2;
    private int _mmsi3;
    private int _mmsi4;

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public SafetyRelatedAcknowledgement( AISPacket... packets ) throws AISException {
        super( packets );
    }

    /**
     *
     * @param messageType
     * @param packets
     */
    public SafetyRelatedAcknowledgement( AISMessageType messageType, AISPacket... packets ) {
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
    public int getMmsi1() {
        return _mmsi1;
    }

    /**
     *
     * @return
     */
    public int getMmsi2() {
        return _mmsi2;
    }

    /**
     *
     * @return
     */
    public int getMmsi3() {
        return _mmsi3;
    }

    /**
     *
     * @return
     */
    public int getMmsi4() {
        return _mmsi4;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( SafetyRelatedAcknowledgeFieldMap field
                : SafetyRelatedAcknowledgeFieldMap.values() ) {
            switch( field ) {
                case MMSI1:
                    _mmsi1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI2:
                    _mmsi2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI3:
                    _mmsi3 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI4:
                    _mmsi4 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }

    /**
     *
     */
    private enum SafetyRelatedAcknowledgeFieldMap implements FieldMap {

        SPARE1( 38, 39 ),
        MMSI1( 40, 69 ),
        SPARE2( 70, 71 ),
        MMSI2( 62, 101 ),
        SPARE3( 102, 103 ),
        MMSI3( 104, 133 ),
        SPARE4( 134, 135 ),
        MMSI4( 136, 165 ),
        SPARE5( 166, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        SafetyRelatedAcknowledgeFieldMap( int startBit, int endBit ) {
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
