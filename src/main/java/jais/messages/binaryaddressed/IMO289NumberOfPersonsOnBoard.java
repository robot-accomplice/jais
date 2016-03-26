/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289NumberOfPersonsOnBoard( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.NUMBER_OF_PERSONS_ON_BOARD, packets );
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
