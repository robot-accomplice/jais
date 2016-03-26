/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.binaryaddressed;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289TidalWindow extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO289TidalWindow.class );

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289TidalWindow( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.TIDAL_WINDOW, packets );
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289TidalWindowFieldMap field
                : IMO289TidalWindowFieldMap.values() ) {

            switch( field ) {
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289TidalWindowFieldMap implements FieldMap {

        DEFAULT( -1, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO289TidalWindowFieldMap( int startBit, int endBit ) {
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
