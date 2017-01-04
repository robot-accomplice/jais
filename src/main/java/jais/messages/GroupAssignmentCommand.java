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
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.ShipType;
import jais.messages.enums.StationInterval;
import jais.messages.enums.StationType;
import jais.messages.enums.TransmitMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class GroupAssignmentCommand extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( GroupAssignmentCommand.class );

    private float _neLon;
    private float _neLat;
    private float _swLon;
    private float _swLat;
    private StationType _stationType;
    private ShipType _shipType;
    private TransmitMode _txrx;
    private StationInterval _interval;
    private int _quietTime;

    /**
     *
     * @param source
     * @param packets
     */
    public GroupAssignmentCommand( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public GroupAssignmentCommand( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
    }

    /**
     *
     * @return
     */
    public float getNeLon() {
        return _neLon;
    }

    /**
     *
     * @return
     */
    public float getNeLat() {
        return _neLat;
    }

    /**
     *
     * @return
     */
    public float getSwLon() {
        return _swLon;
    }

    /**
     *
     * @return
     */
    public float getSwLat() {
        return _swLat;
    }

    /**
     *
     * @return
     */
    public StationType getStationType() {
        return _stationType;
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
    public TransmitMode getTxrx() {
        return _txrx;
    }

    /**
     *
     * @return
     */
    public StationInterval getInterval() {
        return _interval;
    }

    /**
     *
     * @return
     */
    public int getQuietTime() {
        return _quietTime;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( GroupAssignmentCommandFieldMap field : GroupAssignmentCommandFieldMap.values() ) {
            switch( field ) {
                case NE_LON:
                    _neLon = AISMessageDecoder.decodeLongitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NE_LAT:
                    _neLat = AISMessageDecoder.decodeLatitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SW_LON:
                    _swLon = AISMessageDecoder.decodeLongitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SW_LAT:
                    _swLat = AISMessageDecoder.decodeLatitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case STATION_TYPE:
                    int stCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _stationType = StationType.getForCode( stCode );
                    break;
                case TXRX_MODE:
                    int txrxCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _txrx = TransmitMode.getForCode( txrxCode );
                    break;
                case REPORT_INTERVAL:
                    int iCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _interval = StationInterval.getForCode( iCode );
                    break;
                case QUIET_TIME:
                    _quietTime = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum GroupAssignmentCommandFieldMap implements FieldMap {

        SPARE( 38, 39 ),
        NE_LON( 40, 57 ),
        NE_LAT( 58, 74 ),
        SW_LON( 75, 92 ),
        SW_LAT( 93, 109 ),
        STATION_TYPE( 110, 113 ),
        SHIP_TYPE( 114, 121 ),
        SPARE2( 122, 143 ),
        TXRX_MODE( 144, 145 ),
        REPORT_INTERVAL( 146, 149 ),
        QUIET_TIME( 150, 153 ),
        SPARE3( 154, 159 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private GroupAssignmentCommandFieldMap( int startBit, int endBit ) {
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
