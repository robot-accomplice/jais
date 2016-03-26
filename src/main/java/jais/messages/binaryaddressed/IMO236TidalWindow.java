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
@Deprecated
public class IMO236TidalWindow extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO236TidalWindow.class );

    private int _month;
    private int _day;
    private float _lat1;
    private float _lon1;
    private int _fromHour1;
    private int _fromMinute1;
    private int _currentDir1;
    private float _currentSpeed1;

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO236TidalWindow( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.TIDAL_WINDOW_DEPRECATED, packets );
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        // here we need to figure out how many elements in an array of Tidal
        // information there are (could be up to three) based on the size of 
        // remaining data after we decode the month and day -- may use a public
        // static inner class to represent the tidal information and just store
        // the array
        for( IMO236TidalWindowFieldMap field
                : IMO236TidalWindowFieldMap.values() ) {
            switch( field ) {
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO236TidalWindowFieldMap implements FieldMap {

        DEFAULT( -1, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO236TidalWindowFieldMap( int startBit, int endBit ) {
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
