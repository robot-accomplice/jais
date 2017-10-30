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

package jais.messages.binaryaddressed;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import org.locationtech.spatial4j.shape.Point;
import jais.messages.AISMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289ClearanceTimeToEnterPort extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger( IMO289ClearanceTimeToEnterPort.class );

    private int _linkageId;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private String _portName;
    private String _destination;
    private float _lon;
    private float _lat;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289ClearanceTimeToEnterPort( String source, AISPacket... packets )
            throws AISException {
        super( source, BinaryAddressedMessageType.CLEARANCE_TIME_TO_ENTER_PORT, packets );
    }

    /**
     *
     * @return
     */
    public int getLinkageId() {
        return _linkageId;
    }

    /**
     *
     * @return
     */
    public int getMonth() {
        return _month;
    }

    /**
     *
     * @return
     */
    public int getDay() {
        return _day;
    }

    /**
     *
     * @return
     */
    public int getHour() {
        return _hour;
    }

    /**
     *
     * @return
     */
    public int getMinute() {
        return _minute;
    }

    /**
     *
     * @return
     */
    public String getPortName() {
        return _portName;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        return _destination;
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
            _position = CTX.getShapeFactory().pointXY( _lat, _lon );
        }

        return _position;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289ClearanceTimeToEnterPortFieldMap field
                : IMO289ClearanceTimeToEnterPortFieldMap.values() ) {
            switch( field ) {
                case MESSAGE_LINKAGE_ID:
                    _linkageId = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MONTH:
                    _month = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DAY:
                    _day = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                   break;
                case HOUR:
                    _hour = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MINUTE:
                    _minute = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case PORT_NAME_AND_BERTH:
                    _portName = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DESTINATION:
                    _destination = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeLongitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeLatitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289ClearanceTimeToEnterPortFieldMap implements FieldMap {

        MESSAGE_LINKAGE_ID( 88, 97 ),
        MONTH( 98, 101 ),
        DAY( 102, 106 ),
        HOUR( 107, 111 ),
        MINUTE( 112, 117 ),
        PORT_NAME_AND_BERTH( 118, 237 ),
        DESTINATION( 238, 267 ),
        LON( 268, 292 ),
        LAT( 293, 316 ),
        SPARE( 317, 359 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        IMO289ClearanceTimeToEnterPortFieldMap( int startBit, int endBit ) {
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
