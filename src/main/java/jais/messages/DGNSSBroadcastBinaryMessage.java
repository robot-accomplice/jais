/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import com.spatial4j.core.shape.Point;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class DGNSSBroadcastBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( DGNSSBroadcastBinaryMessage.class );

    private float _lon;
    private float _lat;
    private BitSet _data;

    /**
     *
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage( AISPacket... packets ) {
        super( packets );
    }

    /**
     *
     * @param type
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage( AISMessageType type, AISPacket... packets ) {
        super( type, packets );
    }

    /**
     *
     * @return
     */
    public float getLon() {
        return _lon;
    }

    /**
     *
     * @return
     */
    public float getLat() {
        return _lat;
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
     * @return
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public Point getPosition() {
        if( _position == null ) {
            _position = CTX.makePoint( _lon, _lat );
        }

        return _position;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( DGNSSBroadcastBinaryMessageFieldMap field : DGNSSBroadcastBinaryMessageFieldMap.values() ) {
            try {
                switch( field ) {
                    case LON:
                        _lon = AISMessageDecoder.decodeLongitude( _bits,
                                field.getStartBit(), field.getEndBit() );
                    case LAT:
                        _lat = AISMessageDecoder.decodeLatitude( _bits,
                                field.getStartBit(), field.getEndBit() );
                    case DATA:
                        // store the undecoded portion of the bitArray in the data 
                        // field for later decoding by subtype
                        _data = new BitSet( _bits.size() - field.getStartBit() );
                        for( int b = field.getStartBit(); b < _bits.size() - 1; b++ ) {
                            _data.set( b, _bits.get( field.getStartBit() + b ) );
                        }
                        break;
                    default:
                        LOG.debug( "Ignoring field: {}", field.name() );
                }
            } catch( ArrayIndexOutOfBoundsException aioobe ) {
                LOG.debug( "Encountered an ArrayIndexOutofBoundsException: {}.", aioobe.getMessage(), aioobe );
            }
        }
    }

    /**
     *
     */
    private enum DGNSSBroadcastBinaryMessageFieldMap implements FieldMap {

        LON( 40, 57 ),
        LAT( 58, 74 ),
        SPARE( 75, 79 ),
        DATA( 80, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private DGNSSBroadcastBinaryMessageFieldMap( int startBit, int endBit ) {
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
