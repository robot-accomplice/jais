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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class StaticDataReport extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( StaticDataReport.class );

    private int _partNo;
    private String _shipName;
    private ShipType _shipType;
    private String _vendorId;
    private String _callSign;
    private int _toBow;
    private int _toStern;
    private int _toPort;
    private int _toStarboard;
    private int _mothershipMmsi;

    /**
     *
     * @param source
     * @param packets
     */
    public StaticDataReport( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public StaticDataReport( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
    }

    /**
     *
     * @return
     */
    public int getPartNo() {
        return _partNo;
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
    public String getVendorId() {
        return _vendorId;
    }

    /**
     *
     * @return
     */
    public String getCallSign() {
        return _callSign;
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
    public int getMothershipMmsi() {
        return _mothershipMmsi;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( StaticDataReportFieldMap field : StaticDataReportFieldMap.values() ) {
            switch( field ) {
                case PART_NUMBER:
                    _partNo = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_NAME:
                    _shipName = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_TYPE:
                    int stCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _shipType = ShipType.getForCode( stCode );
                    break;
                case VENDOR_ID:
                    _vendorId = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case CALL_SIGN:
                    _callSign = AISMessageDecoder.decodeString( _bits,
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
                case MOTHERSHIP_MMSI:
                    _mothershipMmsi = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum StaticDataReportFieldMap implements FieldMap {

        PART_NUMBER( 38, 39 ),
        SHIP_NAME( 40, 159 ),
        SPARE1( 160, 167 ),
        SHIP_TYPE( 40, 47 ),
        VENDOR_ID( 48, 89 ),
        CALL_SIGN( 90, 131 ),
        TO_BOW( 132, 140 ),
        TO_STERN( 141, 149 ),
        TO_PORT( 150, 155 ),
        TO_STARBOARD( 156, 161 ),
        MOTHERSHIP_MMSI( 132, 161 ),
        SPARE2( 162, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private StaticDataReportFieldMap( int startBit, int endBit ) {
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
