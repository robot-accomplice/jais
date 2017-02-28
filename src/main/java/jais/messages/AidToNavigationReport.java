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
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.NavaidType;
import com.spatial4j.core.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AidToNavigationReport extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( AidToNavigationReport.class );

    private NavaidType _navaidType;
    private String _name;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private int _toBow;
    private int _toStern;
    private int _toPort;
    private int _toStarboard;
    private EPFDFixType _epfd;
    private int _second;
    private boolean _offPosition;
    private int _regional;
    private boolean _raim;
    private boolean _virtualAid;
    private boolean _assigned;
    private String _nameExtension;

    /**
     *
     * @param source
     * @param packets
     */
    public AidToNavigationReport( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public AidToNavigationReport( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
    }

    /**
     *
     * @return
     */
    public NavaidType getNavaidType() {
        return _navaidType;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return _name;
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
    public int getSecond() {
        return _second;
    }

    /**
     *
     * @return
     */
    public boolean isOffPosition() {
        return _offPosition;
    }

    /**
     *
     * @return
     */
    public int getRegional() {
        return _regional;
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
    public boolean isVirtualAid() {
        return _virtualAid;
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
    public String getNameExtension() {
        return _nameExtension;
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
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( AidToNavigationReportFieldMap field : AidToNavigationReportFieldMap.values() ) {
            try {
                switch( field ) {
                    case NAVAID_TYPE:
                        int navCode = AISMessageDecoder.decodeSignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                        _navaidType = NavaidType.getForCode( navCode );
                        break;
                    case NAME:
                        _name = AISMessageDecoder.decodeToString( _bits,
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
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                        _epfd = EPFDFixType.getForCode( epfdCode );
                        break;
                    case SECOND:
                        _second = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                        break;
                    case OFF_POSITION:
                        _offPosition = _bits.get( field.getStartBit() );
                        break;
                    case REGIONAL_RESERVED:
                        _regional = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                        break;
                    case RAIM:
                        _raim = _bits.get( field.getStartBit() );
                        break;
                    case VIRTUAL_AID:
                        _virtualAid = _bits.get( field.getStartBit() );
                        break;
                    case NAME_EXTENSION:
                        _nameExtension = AISMessageDecoder.decodeToString( _bits,
                                field.getStartBit(), _bits.size() - 1 );
                        break;
                    default:
                        if( LOG.isTraceEnabled() ) LOG.trace( "Ignoring field: {}", field.name() );
                }
            } catch( Exception e ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Failed to decode field: {} due to {}", field.name(),
                        e.getMessage(), e );
            }
        }
    }

    /**
     *
     */
    private enum AidToNavigationReportFieldMap implements FieldMap {

        NAVAID_TYPE( 38, 42 ),
        NAME( 43, 162 ),
        ACCURATE( 163, 163 ),
        LON( 164, 191 ),
        LAT( 192, 218 ),
        TO_BOW( 219, 227 ),
        TO_STERN( 228, 236 ),
        TO_PORT( 237, 242 ),
        TO_STARBOARD( 243, 248 ),
        EPFD( 249, 252 ),
        SECOND( 253, 258 ),
        OFF_POSITION( 259, 259 ),
        REGIONAL_RESERVED( 260, 267 ),
        RAIM( 268, 268 ),
        VIRTUAL_AID( 269, 269 ),
        ASSIGNED( 270, 270 ),
        SPARE( 271, 271 ),
        NAME_EXTENSION( 272, 360 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private AidToNavigationReportFieldMap( int startBit, int endBit ) {
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
