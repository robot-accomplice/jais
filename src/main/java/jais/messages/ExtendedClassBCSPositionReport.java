/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
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
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.ShipType;
import org.locationtech.spatial4j.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class ExtendedClassBCSPositionReport extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( ExtendedClassBCSPositionReport.class );

    private int _speed;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private float _course;
    private int _heading;
    private int _second;
    private String _shipName;
    private ShipType _shipType;
    private int _toBow;
    private int _toStern;
    private int _toPort;
    private int _toStarboard;
    private EPFDFixType _epfd;
    private boolean _raim;
    private boolean _dte;
    private int _assigned;
    
    /**
     *
     * @param source
     * @param packets
     */
    public ExtendedClassBCSPositionReport( String source, AISPacket... packets ) {
        super( source, packets );
    }
    
    /**
     * 
     * @param source
     * @param type
     * @param packets 
     */
    public ExtendedClassBCSPositionReport( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
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
    public String getShipName() {
        return _shipName;
    }

    /**
     * 
     * @return 
     */
    public ShipType getShipType() {
        return _shipType;
    }

    /**
     * 
     * @return 
     */
    public int getToBow() {
        return _toBow;
    }

    /**
     * 
     * @return 
     */
    public int getToStern() {
        return _toStern;
    }

    /**
     * 
     * @return 
     */
    public int getToPort() {
        return _toPort;
    }

    /**
     * 
     * @return 
     */
    public int getToStarboard() {
        return _toStarboard;
    }

    /**
     * 
     * @return 
     */
    public EPFDFixType getEpfd() {
        return _epfd;
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
    public boolean isDte() {
        return _dte;
    }

    /**
     * 
     * @return 
     */
    public int getAssigned() {
        return _assigned;
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
        
        for( ExtendedClassBCSPositionReportFieldMap field : ExtendedClassBCSPositionReportFieldMap.values() ) {
            switch( field ) {
                case SPEED:
                    _speed = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ACCURATE:
                    _accurate = _bits.get( field.getStartBit() );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeLongitude( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeLatitude( _bits, 
                            field.getStartBit(), field.getEndBit() );
                   break;
                case COURSE:
                    _course = AISMessageDecoder.decodeCourse( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case HEADING:
                    _heading = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SECOND:
                    _second = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_NAME:
                    _shipName = AISMessageDecoder.decodeToString( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_TYPE:
                    int shipCode = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    _shipType = ShipType.getForCode( shipCode );
                    break;
                case TO_BOW:
                    _toBow = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_STERN:
                    _toStern = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_PORT:
                    _toPort = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_STARBOARD:
                    _toStarboard = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                case EPFD:
                    int epfdCode = AISMessageDecoder.decodeUnsignedInt(  _bits, 
                            field.getStartBit(), field.getEndBit() );
                    _epfd = EPFDFixType.getForCode( epfdCode );
                    break;
                case RAIM:
                    _raim = _bits.get( field.getStartBit() );
                    break;
                case DTE:
                    _dte = _bits.get( field.getStartBit() );
                    break;
                case ASSIGNED:
                    _assigned = AISMessageDecoder.decodeUnsignedInt( _bits, 
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: " + field.name() );
            }
        }
    }

    /**
     *
     */
    private enum ExtendedClassBCSPositionReportFieldMap implements FieldMap {

        RESERVED( 38, 45 ),
        SPEED( 46, 55 ),
        ACCURATE( 56, 56 ),
        LON( 57, 84 ),
        LAT( 85, 111 ),
        COURSE( 123, 123 ),
        HEADING( 124, 132 ),
        SECOND( 133, 138),
        RESERVED2( 139, 142 ),
        SHIP_NAME( 143, 262),
        SHIP_TYPE( 263, 270 ),
        TO_BOW( 271, 279 ),
        TO_STERN( 280, 288 ),
        TO_PORT( 289, 294 ),
        TO_STARBOARD( 295, 300 ),
        EPFD( 301, 304 ),
        RAIM( 305, 305 ),
        DTE( 306, 306 ),
        ASSIGNED( 307, 307 ),
        SPARE( 308, 311 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        ExtendedClassBCSPositionReportFieldMap( int startBit, int endBit ) {
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
