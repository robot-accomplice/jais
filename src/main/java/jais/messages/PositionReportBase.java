/*
 * Copyright 2016 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import org.locationtech.spatial4j.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public abstract class PositionReportBase extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( PositionReportBase.class );

    // bit positions are off spec by 1 because the BitSet counts from 0 rather than 1
    private NavigationStatus _status = NavigationStatus.NOT_DEFINED; // bits 38-41
    private float _turn; // bits 42-49
    private float _speed; // bits 50-59, represented in knots
    private boolean _accurate; // bit 60
    private float _lon; // bits 61-88 
    private float _lat; // 89-115
    private float _course; // bits 116-127, 0.1 degree precision, relative to true north
    private int _heading = 511; // bits 128-136, 0-359 degrees, 511 means not available
    private int _second; // bits 137-142, timestamp in seconds since epoch
    private ManeuverType _maneuver = ManeuverType.NOT_AVAILABLE; // bits 143-144, maneuver indicator
    // spare bits 145-147
    private boolean _raim; // bit 148
    private int _radio; // bits 149-167, Radio Status
    private boolean _positionValid;
    
    /**
     * 
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public PositionReportBase( String source, AISPacket... packets ) throws AISException {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param messageType
     * @param packets
     */
    public PositionReportBase( String source, AISMessageType messageType, AISPacket... packets ) {
        super( source, messageType, packets );
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
            _position = CTX.getShapeFactory().pointXY( _lon, _lat ); // must be in x, y (lon, lat) order
        }

        return _position;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isPositionValid() {
        return ( _lon >= -90 && _lon <= 90 && _lat >= -180 && _lat <= 180 );
    }

    /**
     *
     * @return
     */
    public NavigationStatus getStatus() {
        return _status;
    }

    /**
     *
     * @return
     */
    public float getTurn() {
        return _turn;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
        return _speed;
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
    public float getCourse() {
        return _course;
    }

    /**
     *
     * @return
     */
    public int getHeading() {
        return _heading;
    }

    /**
     *
     * @return
     */
    public int getSecond() {
        return _second;
    }

    /**
     *
     * @return
     */
    public ManeuverType getManeuver() {
        return _maneuver;
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
    public int getRadio() {
        return _radio;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( PositionFieldMap field : PositionFieldMap.values() ) {
            switch( field ) {
                case STATUS:
                    if( _bits.size() >= field.getStartBit() ) {
                        int nsId = AISMessageDecoder.decodeUnsignedInt( super._bits, field.getStartBit(), field.getEndBit() );
                        _status = NavigationStatus.getForCode( nsId );
                    }
                    break;
                case TURN:
                    if( _bits.size() >= field.getStartBit() )
                        _turn = AISMessageDecoder.decodeTurn( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case SPEED:
                    if( _bits.size() >= field.getStartBit() )
                        _speed = AISMessageDecoder.decodeSpeed( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case ACCURACY:
                    if( _bits.size() >= field.getStartBit() )
                        _accurate = _bits.get( field.getEndBit() );
                    break;
                case LON:
                    if( _bits.size() >= field.getStartBit() )
                        _lon = AISMessageDecoder.decodeLongitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    if( _bits.size() >= field.getStartBit() )
                        _lat = AISMessageDecoder.decodeLatitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case COURSE:
                    if( _bits.size() >= field.getStartBit() )
                        _course = AISMessageDecoder.decodeCourse( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case HEADING:
                    if( _bits.size() >= field.getStartBit() )
                        _heading = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case SECOND:
                    if( _bits.size() >= field.getStartBit() )
                        _second = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case MANEUVER:
                    if( _bits.size() >= field.getStartBit() ) {
                        int mId = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                        _maneuver = ManeuverType.getForCode( mId );
                        if( _maneuver == null ) {
                            _maneuver = ManeuverType.NOT_AVAILABLE;
                        }
                    }
                    break;
                case RAIM:
                    if( _bits.size() >= field.getStartBit() )
                        _raim = _bits.get( field.getEndBit() );
                    break;
                case RADIO:
                    if( _bits.size() >= field.getStartBit() )
                        _radio = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Encountered unhandled field type of : {}", field );
                    break;
            }
        }
    }

    /**
     *  bit position numbers differ from the NMEA spec in that the BitSet in 
     *  which they are stored indexes from zero rather than one
     */
    protected enum PositionFieldMap implements FieldMap {
        STATUS( 38, 41 ),
        TURN( 42, 49 ),
        SPEED( 50, 59 ),
        ACCURACY( 60, 60 ),
        LON( 61, 88 ),
        LAT( 89, 115 ),
        COURSE( 116, 127 ),
        HEADING( 128, 136 ),
        SECOND( 137, 142 ),
        MANEUVER( 143, 144 ),
        SPARE( 145, 147 ), // NOT used
        RAIM( 148, 148 ),
        RADIO( 149, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         * s
         *
         * @param startBit
         * @param endBit
         */
        PositionFieldMap( int startBit, int endBit ) {
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
