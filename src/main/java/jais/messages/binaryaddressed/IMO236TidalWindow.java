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

package jais.messages.binaryaddressed;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import jais.messages.enums.BinaryAddressedMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
@Deprecated
public class IMO236TidalWindow extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager.getLogger( IMO236TidalWindow.class );

    private int _month;
    private int _day;
    private String[] _tidals;
    private float _lat;
    private float _lon;
    private int _fromHour;
    private int _fromMinute;
    private int _toHour;
    private int _toMinute;
    private int _currentDir;
    private float _currentSpeed;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO236TidalWindow( String source, AISPacket... packets )
            throws AISException {
        super( source, BinaryAddressedMessageType.TIDAL_WINDOW_DEPRECATED, packets );
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
     * @param month
     */
    public void setMonth( int month ) {
        this._month = month;
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
     * @param day
     */
    public void setDay( int day ) {
        this._day = day;
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
     * @param lat
     */
    public void setLat( float lat ) {
        this._lat = lat;
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
     * @param lon
     */
    public void setLon( float lon ) {
        this._lon = lon;
    }

    /**
     *
     * @return
     */
    public int getFromHour() {
        return _fromHour;
    }

    /**
     *
     * @param fromHour
     */
    public void setFromHour( int fromHour ) {
        this._fromHour = fromHour;
    }

    /**
     *
     * @return
     */
    public int getFromMinute() {
        return _fromMinute;
    }

    /**
     *
     * @param fromMinute
     */
    public void setFromMinute( int fromMinute ) {
        this._fromMinute = fromMinute;
    }

    /**
     *
     * @return
     */
    public int getToHour() {
        return _toHour;
    }

    /**
     *
     * @param toHour
     */
    public void setToHour( int toHour ) {
        this._toHour = toHour;
    }

    /**
     *
     * @return
     */
    public int getToMinute() {
        return _toMinute;
    }

    /**
     *
     * @param toMinute
     */
    public void setToMinute( int toMinute ) {
        this._toMinute = toMinute;
    }

    /**
     *
     * @return
     */
    public int getCurrentDir() {
        return _currentDir;
    }

    /**
     *
     * @param currentDir
     */
    public void setCurrentDir( int currentDir ) {
        this._currentDir = currentDir;
    }

    /**
     *
     * @return
     */
    public float getCurrentSpeed() {
        return _currentSpeed;
    }

    /**
     *
     * @param currentSpeed
     */
    public void setCurrentSpeed( float currentSpeed ) {
        this._currentSpeed = currentSpeed;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        // here we need to figure out how many elements in an array of Tidal
        // information there are (could be up to three) based on the size of 
        // remaining data after we decode the month and day -- may use a public
        // static inner class to represent the tidal information and just store
        // the array
        for( IMO236TidalWindowFieldMap field
                : IMO236TidalWindowFieldMap.values() ) {
            switch( field ) {
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO236TidalWindowFieldMap implements FieldMap {

        DEFAULT( -1, -1 ),
        LATITUDE( 0, 26 ),
        LONGITUDE( 27, 54 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        IMO236TidalWindowFieldMap( int startBit, int endBit ) {
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
