/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.BinaryBroadcastMessageType;
import jais.messages.enums.FieldMap;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public abstract class BinaryBroadcastMessageBase extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( BinaryBroadcastMessageBase.class );

    private int _dac; // designated area code
    private int _fid; // functional id
    private BitSet _data;

    /**
     *
     * @param packets
     */
    public BinaryBroadcastMessageBase( AISPacket... packets ) {
        super( packets );
    }

    /**
     *
     * @param messageType
     * @param packets
     */
    public BinaryBroadcastMessageBase( AISMessageType messageType, AISPacket... packets ) {
        super( messageType, packets );
    }

    /**
     *
     * @return
     */
    public abstract BinaryBroadcastMessageType getSubType();

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
    public static Logger getLOG() {
        return LOG;
    }

    /**
     *
     * @return
     */
    public int getDac() {
        return _dac;
    }

    /**
     *
     * @return
     */
    public int getFid() {
        return _fid;
    }

    /**
     *
     * @return
     */
    public BitSet getData() {
        return _data;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public void decode() throws AISException {

        for( BinaryBroadcastMessageBaseFieldMap field
                : BinaryBroadcastMessageBaseFieldMap.values() ) {
            switch( field ) {
                case DAC:
                    _dac = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case FID:
                    _fid = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DATA:
                    // store the undecoded portion of the BitSet in the data 
                    // field for later decoding by subtype
                    _data = _bits.get( field.getStartBit(), _bits.size() - 1 );
                    break;
            }
        }
    }

    /**
     *
     */
    private enum BinaryBroadcastMessageBaseFieldMap implements FieldMap {

        SEQUENCE_NUMBER( 38, 39 ),
        DESTINATION_MMSI( 40, 69 ),
        RETRANSMIT( 70, 70 ),
        SPARE( 71, 71 ),
        DAC( 72, 81 ), // designated area code
        FID( 82, 87 ), // Functional ID
        DATA( 88, -1 ) // -1 means from startBit to end of bitArray
        ;

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        BinaryBroadcastMessageBaseFieldMap( int startBit, int endBit ) {
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
