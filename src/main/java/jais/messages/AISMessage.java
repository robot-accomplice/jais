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
 * @author Jonathan Machen
 */
public interface AISMessage {

    Logger LOG = LogManager.getLogger(AISMessage.class);
    
    /**
     *
     * @param imo
     * @return
     */
    public static boolean isValidImo( String imo ) {
        if( imo.toLowerCase().startsWith( "imo" ) ) return isValidImo( Long.parseLong( imo.substring( 4 ) ) );

        return isValidImo( Long.parseLong( imo ) );
    }

    /**
     *
     * @param imo
     * @return
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

            for( int i = 0; i < 6; i++ ) {
                sum += digits[i];
            }

            if( LOG.isDebugEnabled() ) LOG.debug( "Sum of products is : {}", sum );

            valid = ( sum % 10 == digits[6] );

            LOG.info( "Modulus of sum divided by 10 is: {} vs {}",
                    sum % 10, digits[6] );
        }

        return valid;
    }
    
    /**
     * 
     * @return 
     */
    String getSource();
    
    /**
     * 
     * @param source 
     */
    void setSource( String source );

    /**
     * 
     * @return 
     */
    AISPacket [] getPackets();
    
    /**
     * 
     * @return 
     */
    AISMessageType getType();
    
    /**
     * 
     * @return 
     */
    FieldMap [] getFieldMap();
    
    /**
     * 
     * @param offset
     * @return 
     */
    ZonedDateTime getTimeReceived( ZoneOffset offset );
    
    /**
     * 
     * @return 
     */
    long getTimeReceived();

    /**
     * 
     * @param mType 
     */
    void setType( AISMessageType mType );
    
    /**
     * 
     * @return 
     */
    int getRepeat();
    
    /**
     * 
     * @return 
     */
    int getMmsi();
    
    /**
     * 
     * @return 
     */
    MMSIType getMMSIType();
    
    /**
     * 
     * @return 
     */
    boolean hasValidMmsi();
    
    /**
     * 
     * @return 
     */
    boolean hasPosition();
    
    /**
     * 
     * @return 
     */
    Point getPosition();
    
    /**
     * 
     * @return 
     */
    boolean hasSubType();
    
    /**
     * 
     * @return 
     * @throws jais.exceptions.AISException 
     */
    AISMessage getSubTypeInstance() throws AISException;
    
    /**
     * 
     * @throws jais.exceptions.AISException 
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
