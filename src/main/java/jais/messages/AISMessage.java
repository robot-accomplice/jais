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
import jais.messages.enums.MMSIType;
import java.nio.charset.Charset;
import org.locationtech.spatial4j.shape.Point;
import java.time.ZonedDateTime;

/**
 *
 * @author Jonathan Machen
 */
public interface AISMessage {
    
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
     * @return 
     */
    ZonedDateTime getTimeReceived();

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
     * @param charset
     * @return 
     * @throws jais.exceptions.AISException 
     */
    AISMessage getSubTypeInstance( Charset charset ) throws AISException;
    
    /**
     * 
     * @throws jais.exceptions.AISException
     */
    void decode() throws AISException;
    
    /**
     * 
     * @param charset
     * @throws AISException 
     */
    void decode( Charset charset ) throws AISException;

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
