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
import jais.messages.enums.FieldMap;
import com.spatial4j.core.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class StandardClassBCSPositionReport extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( StandardClassBCSPositionReport.class );
    
    private int _speed;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private float _course;
    private int _heading;
    private int _second;
    private boolean _cs;
    private boolean _display;
    private boolean _dsc;
    private boolean _band;
    private boolean _msg22;
    private boolean _assigned;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param source
     * @param packets
     */
    public StandardClassBCSPositionReport( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public StandardClassBCSPositionReport( String source, AISMessageType type, AISPacket... packets ) {
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
    public boolean isCs() {
        return _cs;
    }

    /**
     *
     * @return
     */
    public boolean isDisplay() {
        return _display;
    }

    /**
     *
     * @return
     */
    public boolean isDsc() {
        return _dsc;
    }

    /**
     *
     * @return
     */
    public boolean isBand() {
        return _band;
    }

    /**
     *
     * @return
     */
    public boolean isMsg22() {
        return _msg22;
    }

    /**
     *
     * @return
     */
    public boolean isAssigned() {
        return _assigned;
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

        for( StandardClassBCSPositionReportFieldMap field : StandardClassBCSPositionReportFieldMap.values() ) {
            try {
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
                    case CS:
                        _cs = _bits.get( field.getStartBit() );
                        break;
                    case DISPLAY:
                        _display = _bits.get( field.getStartBit() );
                        break;
                    case DSC:
                        _dsc = _bits.get( field.getStartBit() );
                        break;
                    case MSG22:
                        _msg22 = _bits.get( field.getStartBit() );
                        break;
                    case ASSIGNED:
                        _assigned = _bits.get( field.getStartBit() );
                        break;
                    case RAIM:
                        _raim = _bits.get( field.getStartBit() );
                        break;
                    case BAND:
                        _band = _bits.get( field.getStartBit() );
                        break;
                    case RADIO:
                        _radio = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                        break;
                    default:
                        if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
                }
            } catch( AISException ae ) {
                LOG.warn( "Unable to decode field: {}: {}", field.name(), ae.getMessage() );
                if( LOG.isDebugEnabled() ) LOG.debug( ae.getMessage() );
            }
        }
    }

    /**
     *
     */
    private enum StandardClassBCSPositionReportFieldMap implements FieldMap {

        RESERVED1( 38, 45 ),
        SPEED( 46, 55 ),
        ACCURATE( 56, 56 ),
        LON( 57, 84 ),
        LAT( 85, 111 ),
        COURSE( 112, 123 ),
        HEADING( 124, 132 ),
        SECOND( 133, 138 ),
        RESERVED2( 139, 140 ),
        CS( 141, 141 ),
        DISPLAY( 142, 142 ),
        DSC( 143, 143 ),
        BAND( 144, 144 ),
        MSG22( 145, 145 ),
        ASSIGNED( 146, 146 ),
        RAIM( 147, 147 ),
        RADIO( 148, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        StandardClassBCSPositionReportFieldMap( int startBit, int endBit ) {
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
