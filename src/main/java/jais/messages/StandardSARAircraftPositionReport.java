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
import org.locationtech.spatial4j.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class StandardSARAircraftPositionReport extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( StandardSARAircraftPositionReport.class );

    private int _alt;
    private int _speed;
    private boolean _accurate;
    private int _lon;
    private int _lat;
    private float _course;
    private int _second;
    private boolean _dte;
    private boolean _assigned;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public StandardSARAircraftPositionReport( String source, AISPacket... packets )
            throws AISException {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param messageType
     * @param packets
     */
    public StandardSARAircraftPositionReport( String source, AISMessageType messageType,
            AISPacket... packets ) {
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
            _position = CTX.getShapeFactory().pointXY( _lat, _lon );
        }

        return _position;
    }

    /**
     *
     * @return
     */
    public int getAlt() {
        return _alt;
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
    public int getLon() {
        return _lon;
    }

    /**
     *
     * @return
     */
    public int getLat() {
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
    public int getSecond() {
        return _second;
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
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( SSARAircraftPositionReportFieldMap field
                : SSARAircraftPositionReportFieldMap.values() ) {
            switch( field ) {
                case ALT:
                    _alt = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SPEED:
                    _speed = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ACCURACY:
                    _accurate = _bits.get( field.getStartBit() );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeSignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeSignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case COURSE:
                    _course = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SECOND:
                    _second = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DTE:
                    _dte = _bits.get( field.getStartBit() );
                    break;
                case ASSIGNED:
                    _assigned = _bits.get( field.getStartBit() );
                    break;
                case RAIM:
                    _raim = _bits.get( field.getStartBit() );
                    break;
                case RADIO:
                    _radio = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }

    /**
     *
     */
    private enum SSARAircraftPositionReportFieldMap implements FieldMap {

        ALT( 38, 49 ),
        SPEED( 50, 59 ),
        ACCURACY( 60, 60 ),
        LON( 61, 88 ),
        LAT( 89, 115 ),
        COURSE( 116, 127 ),
        SECOND( 128, 133 ),
        REGIONAL( 134, 141 ), // reserved
        DTE( 142, 142 ),
        SPARE( 143, 145 ),
        ASSIGNED( 146, 146 ),
        RAIM( 147, 147 ),
        RADIO( 148, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit '
         */
        SSARAircraftPositionReportFieldMap( int startBit, int endBit ) {
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
