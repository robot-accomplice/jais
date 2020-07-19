/*
 * Copyright 2016-2019 Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}.
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
import jais.messages.enums.MMSIType;
import java.time.ZoneOffset;
import org.locationtech.spatial4j.shape.Point;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public interface AISMessage {

    Logger LOG = LogManager.getLogger(AISMessage.class);

    /**
     * Determines whether or not this is a valid IMO according to NMEA standards
     * @param imo the IMO (in String form) of the originating vessel
     * @return a boolean indicating whether or not the provided IMO is valid
     */
    public static boolean isValidImo( String imo ) {
        if( imo.toLowerCase().startsWith( "imo" ) ) return isValidImo( Long.parseLong( imo.substring( 4 ) ) );
        return isValidImo(Long.parseLong( imo ) );
    }

    /**
     * Determines whether or not this is a valid IMO according to NMEA standards
     * @param imo the IMO (in long form) of the originating vessel
     * @return a boolean indicating whether or not the provided IMO is valid
     */
    public static boolean isValidImo( long imo ) {
        LOG.info( "Validating IMO: {}", imo );

        boolean valid = ( Long.toString( imo ).length() == 7 );

        if( valid ) {
            int d = 0;
            Integer[] digits = new Integer[7];
            for( char c : Long.toString( imo ).toCharArray() ) {
                digits[d] = Integer.valueOf( "" + c );
                if( LOG.isDebugEnabled() ) LOG.debug( "Digit at position: {} is {}", d, digits[d] );
                d++;
            }

            digits[0] *= 7;
            digits[1] *= 6;
            digits[2] *= 5;
            digits[3] *= 4;
            digits[4] *= 3;
            digits[5] *= 2;

            int sum = 0;
            for( int i = 0; i < 6; i++ ) sum += digits[i];
            if( LOG.isDebugEnabled() ) LOG.debug( "Sum of products is : {}", sum );

            valid = ( sum % 10 == digits[6] );

            LOG.info( "Modulus of sum divided by 10 is: {} vs {}", sum % 10, digits[6] );
        }

        return valid;
    }

    /**
     *
     * @return a String containing the source of the message
     */
    String getSource();

    /**
     *
     * @param source a String containing the source of the message
     */
    void setSource( String source );

    /**
     *
     * @return the array of packets from which this message was composed
     */
    AISPacket [] getPackets();

    /**
     *
     * @return type AISMessageType of the message
     */
    AISMessageType getType();

    /**
     *
     * @return The map of fields for this message type
     */
    FieldMap [] getFieldMap();

    /**
     * @param offset the timezone offset for which we want the time received to be returned
     * @return a ZonedDateTime object generated using the time received and provided ZoneOffset
     */
    ZonedDateTime getTimeReceived( ZoneOffset offset );

    /**
     *
     * @return the time received in its unaltered long value
     */
    long getTimeReceived();

    /**
     *
     * @param mType the AISMessageType of this message
     */
    void setType( AISMessageType mType );

    /**
     *
     * @return the "repeat" value of the message
     */
    int getRepeat();

    /**
     *
     * @return the MMSI of the vessel that sent this message
     */
    int getMmsi();

    /**
     *
     * @return the type of MMSI
     */
    MMSIType getMMSIType();

    /**
     *
     * @return a boolean indicating whether or not the MMSI is valid
     */
    boolean hasValidMmsi();

    /**
     *
     * @return a boolean indicating whether or not this message contains positional data
     */
    boolean hasPosition();

    /**
     *
     * @return the position as a Point
     */
    Point getPosition();

    /**
     *
     * @return whether or not there is a sub type for the current message
     */
    boolean hasSubType();

    /**
     *
     * @return A properly typed instance of AISMessage given the message content
     * @throws AISException if we are unable to determine the subtype instance (usually because of a decoding or parsing error)
     */
    AISMessage getSubTypeInstance() throws AISException;

    /**
     * Decodes this message
     * @throws AISException if decoding fails
     */
    void decode() throws AISException;

    /**
     * Fields common to all messages
     */
    enum AISFieldMap implements FieldMap {

        TYPE( 0, 5 ),
        REPEAT( 6, 7 ),
        MMSI( 8, 37 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        AISFieldMap( int startBit, int endBit ) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return an int indicating at which bit this field starts
         */
        @Override
        public int getStartBit() { return _startBit; }

        /**
         *
         * @return an int indicating at which bit this field ends
         */
        @Override
        public int getEndBit() { return _endBit; }
    }
}
