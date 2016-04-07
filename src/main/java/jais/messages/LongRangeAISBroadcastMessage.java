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
import jais.messages.enums.NavigationStatus;
import com.spatial4j.core.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class LongRangeAISBroadcastMessage extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( LongRangeAISBroadcastMessage.class );

    private boolean _accurate;
    private boolean _raim;
    private NavigationStatus _navStatus;
    private float _lon;
    private float _lat;
    private int _speed;
    private int _course;
    private boolean _gnss;
    
    /**
     *
     * @param packets
     */
    public LongRangeAISBroadcastMessage( AISPacket... packets ) {
        super( packets );
    }
    
    /**
     * 
     * @param type
     * @param packets 
     */
    public LongRangeAISBroadcastMessage( AISMessageType type, AISPacket... packets ) {
        super( type, packets );
    }

    /**
     * 
     * @return 
     */
    public boolean isAccurate() {
        return _accurate;
    }

    /**
     * 
     * @return 
     */
    public boolean isRaim() {
        return _raim;
    }

    /**
     * 
     * @return 
     */
    public NavigationStatus getNavStatus() {
        return _navStatus;
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
    public int getSpeed() {
        return _speed;
    }

    /**
     * 
     * @return 
     */
    public int getCourse() {
        return _course;
    }

    /**
     * 
     * @return 
     */
    public boolean isGnss() {
        return _gnss;
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
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();
        
        for( LongRangeAISBroadcastMessageFieldMap field : LongRangeAISBroadcastMessageFieldMap.values() ) {
            switch( field ) {
                case ACCURATE:
                    _accurate = _bits.get( field.getStartBit() );
                    break;
                case RAIM:
                    _raim = _bits.get( field.getStartBit() );
                    break;
                case NAVIGATION_STATUS:
                    int nsCode = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    _navStatus = NavigationStatus.getForCode( nsCode );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeLongitude( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeLatitude( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SPEED:
                    _speed = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case COURSE:
                    _course = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case GNSS:
                    _gnss = !_bits.get( field.getStartBit() );
                    break;
                default:
                    LOG.debug( "Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    private enum LongRangeAISBroadcastMessageFieldMap implements FieldMap {

        ACCURATE( 38, 38 ),
        RAIM( 39, 39 ),
        NAVIGATION_STATUS( 40, 43 ),
        LON( 44, 61 ),
        LAT( 62, 78 ),
        SPEED( 79, 84 ),
        COURSE( 85, 93 ),
        GNSS( 94, 94 ),
        SPARE( 95, 95 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private LongRangeAISBroadcastMessageFieldMap( int startBit, int endBit ) {
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
