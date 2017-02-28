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

package jais.messages.binaryaddressed;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.CargoUnitCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO236DangerousCargoIndication extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO236DangerousCargoIndication.class );

    private String _lastPort;
    private int _lastMonth;
    private int _lastDay;
    private int _lastHour;
    private int _lastMinute;
    private String _nextPort;
    private int _nextMonth;
    private int _nextDay;
    private int _nextHour;
    private int _nextMinute;
    private String _dangerous;
    private String _imdCat;
    private int _unId;
    private int _amount;
    private CargoUnitCode _cargoUnit;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO236DangerousCargoIndication( String source, AISPacket... packets )
            throws AISException {
        super( source, BinaryAddressedMessageType.DANGEROUS_CARGO_INDICATION_DEPRECATED,
                packets );
    }

    /**
     *
     * @return
     */
    public String getLastPort() {
        return _lastPort;
    }

    /**
     *
     * @return
     */
    public int getLastMonth() {
        return _lastMonth;
    }

    /**
     *
     * @return
     */
    public int getLastDay() {
        return _lastDay;
    }

    /**
     *
     * @return
     */
    public int getLastHour() {
        return _lastHour;
    }

    /**
     *
     * @return
     */
    public int getLastMinute() {
        return _lastMinute;
    }

    /**
     *
     * @return
     */
    public String getNextPort() {
        return _nextPort;
    }

    /**
     *
     * @return
     */
    public int getNextMonth() {
        return _nextMonth;
    }

    /**
     *
     * @return
     */
    public int getNextDay() {
        return _nextDay;
    }

    /**
     *
     * @return
     */
    public int getNextHour() {
        return _nextHour;
    }

    /**
     *
     * @return
     */
    public int getNextMinute() {
        return _nextMinute;
    }

    /**
     *
     * @return
     */
    public String getDangerous() {
        return _dangerous;
    }

    /**
     *
     * @return
     */
    public String getImdCat() {
        return _imdCat;
    }

    /**
     *
     * @return
     */
    public int getUnId() {
        return _unId;
    }

    /**
     *
     * @return
     */
    public int getAmount() {
        return _amount;
    }

    /**
     *
     * @return
     */
    public CargoUnitCode getCargoUnit() {
        return _cargoUnit;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO236DangerousCargoIndicationFieldMap field
                : IMO236DangerousCargoIndicationFieldMap.values() ) {

            switch( field ) {
                case LAST_PORT_OF_CALL:
                    _lastPort = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAST_ETA_MONTH:
                    _lastMonth = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAST_ETA_DAY:
                    _lastDay = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAST_ETA_HOUR:
                    _lastHour = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAST_ETA_MINUTE:
                    _lastMinute = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NEXT_PORT_OF_CALL:
                    _nextPort = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NEXT_ETA_MONTH:
                    _nextMonth = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NEXT_ETA_DAY:
                    _nextDay = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NEXT_ETA_HOUR:
                    _nextHour = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case NEXT_ETA_MINUTE:
                    _nextMinute = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DANGEROUS_GOOD:
                    _dangerous = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case IMD_CATEGORY:
                    _imdCat = AISMessageDecoder.decodeToString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case UN_NUMBER:
                    _unId = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case AMOUNT_OF_CARGO:
                    _amount = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case UNIT_OF_QUANTITY:
                    int cargoCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _cargoUnit = CargoUnitCode.getForCode( cargoCode );
                    break;
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO236DangerousCargoIndicationFieldMap implements FieldMap {

        LAST_PORT_OF_CALL( 88, 117 ),
        LAST_ETA_MONTH( 118, 121 ),
        LAST_ETA_DAY( 122, 126 ),
        LAST_ETA_HOUR( 127, 131 ),
        LAST_ETA_MINUTE( 132, 137 ),
        NEXT_PORT_OF_CALL( 138, 167 ),
        NEXT_ETA_MONTH( 168, 171 ),
        NEXT_ETA_DAY( 172, 176 ),
        NEXT_ETA_HOUR( 177, 181 ),
        NEXT_ETA_MINUTE( 182, 187 ),
        DANGEROUS_GOOD( 188, 307 ),
        IMD_CATEGORY( 308, 331 ),
        UN_NUMBER( 332, 344 ),
        AMOUNT_OF_CARGO( 345, 354 ),
        UNIT_OF_QUANTITY( 355, 356 ),
        SPARE( 357, 359 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO236DangerousCargoIndicationFieldMap( int startBit, int endBit ) {
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
