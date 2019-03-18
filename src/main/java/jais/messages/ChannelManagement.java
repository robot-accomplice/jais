/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class ChannelManagement extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( ChannelManagement.class );

    private int _channelA;
    private int _channelB;
    private int _txrx;
    private boolean _highPower;
    private float _neLon;
    private float _neLat;
    private float _swLon;
    private float _swLat;
    private int _destMmsi1;
    private int _destMmsi2;
    private boolean _addressed;
    private boolean _bandA;
    private boolean _bandB;
    private int _zoneSize;

    /**
     *
     * @param source
     * @param packets
     */
    public ChannelManagement( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public ChannelManagement( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
    }

    /**
     *
     * @return
     */
    public int getChannelA() {
        return _channelA;
    }

    /**
     *
     * @return
     */
    public int getChannelB() {
        return _channelB;
    }

    /**
     *
     * @return
     */
    public int getTxrx() {
        return _txrx;
    }

    /**
     *
     * @return
     */
    public boolean isHighPower() {
        return _highPower;
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
    public int getDestMmsi1() {
        return _destMmsi1;
    }

    /**
     *
     * @return
     */
    public int getDestMmsi2() {
        return _destMmsi2;
    }

    /**
     *
     * @return
     */
    public boolean isAddressed() {
        return _addressed;
    }

    /**
     *
     * @return
     */
    public boolean isBandA() {
        return _bandA;
    }

    /**
     *
     * @return
     */
    public boolean isBandB() {
        return _bandB;
    }

    /**
     *
     * @return
     */
    public int getZoneSize() {
        return _zoneSize;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( ChannelManagementFieldMap field : ChannelManagementFieldMap.values() ) {
            switch( field ) {
                case NE_LON:
                    if( _bits.size() >= field.getStartBit() )
                        _neLon = AISMessageDecoder.decodeLongitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case NE_LAT:
                    if( _bits.size() >= field.getStartBit() )
                        _neLat = AISMessageDecoder.decodeLatitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case SW_LON:
                    if( _bits.size() >= field.getStartBit() )
                        _swLon = AISMessageDecoder.decodeLongitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case SW_LAT:
                    if( _bits.size() >= field.getStartBit() )
                        _swLat = AISMessageDecoder.decodeLatitude( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case DEST_MMSI1:
                    if( _bits.size() >= field.getStartBit() )
                        _destMmsi1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case DEST_MMSI2:
                    if( _bits.size() >= field.getStartBit() )
                        _destMmsi2 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case ADDRESSED:
                    if( _bits.size() >= field.getStartBit() )
                        _addressed = _bits.get( field.getStartBit() );
                    break;
                case CHANNEL_A_BAND:
                    if( _bits.size() >= field.getStartBit() )
                        _bandA = _bits.get( field.getStartBit() );
                    break;
                case CHANNEL_B_BAND:
                    if( _bits.size() >= field.getStartBit() )
                        _bandB = _bits.get( field.getEndBit() );
                    break;
                case ZONE_SIZE:
                    if( _bits.size() >= field.getStartBit() )
                        _zoneSize = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum ChannelManagementFieldMap implements FieldMap {

        SPARE( 38, 39 ),
        CHANNEL_A( 40, 51 ),
        CHANNEL_B( 52, 63 ),
        TXRX_MODE( 64, 67 ),
        HIGH_POWER( 68, 68 ),
        NE_LON( 69, 86 ),
        NE_LAT( 87, 103 ),
        SW_LON( 104, 121 ),
        SW_LAT( 122, 138 ),
        DEST_MMSI1( 69, 98 ),
        DEST_MMSI2( 104, 133 ),
        ADDRESSED( 139, 139 ),
        CHANNEL_A_BAND( 140, 140 ),
        CHANNEL_B_BAND( 141, 141 ),
        ZONE_SIZE( 142, 144 ),
        SPARE2( 145, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        ChannelManagementFieldMap( int startBit, int endBit ) {
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
