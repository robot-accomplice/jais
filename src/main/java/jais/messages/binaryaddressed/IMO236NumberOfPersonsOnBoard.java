/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class IMO236NumberOfPersonsOnBoard extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger(
            IMO236NumberOfPersonsOnBoard.class );

    private int _persons;

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO236NumberOfPersonsOnBoard( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.NUMBER_OF_PERSONS_ON_BOARD_DEPRECATED,
                packets );
    }

    /**
     *
     * @return
     */
    public int getPersons() {
        return _persons;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO236NumberOfPersonsOnBoardFieldMap field
                : IMO236NumberOfPersonsOnBoardFieldMap.values() ) {
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
    private enum IMO236NumberOfPersonsOnBoardFieldMap implements FieldMap {

        PERSONS( 55, 68 ),
        SPARE( 69, 71 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO236NumberOfPersonsOnBoardFieldMap( int startBit, int endBit ) {
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
