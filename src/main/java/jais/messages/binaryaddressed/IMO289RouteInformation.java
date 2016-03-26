/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.binaryaddressed;

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
public class IMO289RouteInformation extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO289RouteInformation.class );

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289RouteInformation( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.ROUTE_INFORMATION, packets );
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289RouteInformationFieldMap field
                : IMO289RouteInformationFieldMap.values() ) {
            switch( field ) {
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289RouteInformationFieldMap implements FieldMap {

        DEFAULT( -1, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO289RouteInformationFieldMap( int startBit, int endBit ) {
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
