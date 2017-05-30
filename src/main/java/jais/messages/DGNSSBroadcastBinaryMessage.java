/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import com.spatial4j.core.shape.Point;
import java.util.BitSet;
import org.slf4j.*;

/**
 *
 * @author Jonathan Machen
 */
public class DGNSSBroadcastBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LoggerFactory.getLogger( DGNSSBroadcastBinaryMessage.class );

    private float _lon;
    private float _lat;
    private BitSet _data;

    /**
     *
     * @param source
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
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
                        if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
                }
            } catch( ArrayIndexOutOfBoundsException aioobe ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Encountered an ArrayIndexOutofBoundsException: {}.", aioobe.getMessage(), aioobe );
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
        DGNSSBroadcastBinaryMessageFieldMap( int startBit, int endBit ) {
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
