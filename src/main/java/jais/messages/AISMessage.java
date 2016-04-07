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
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.MMSIType;
import com.spatial4j.core.shape.Point;
import org.joda.time.DateTime;

/**
 *
 * @author Jonathan Machen
 */
public interface AISMessage {

    /**
     * 
     * @return 
     */
    public AISPacket [] getPackets();
    
    /**
     * 
     * @return 
     */
    public AISMessageType getType();
    
    /**
     * 
     * @return 
     */
    public FieldMap [] getFieldMap();
    
    /**
     * 
     * @return 
     */
    public DateTime getTimeReceived();

    /**
     * 
     * @param mType 
     */
    public void setType( AISMessageType mType );
    
    /**
     * 
     * @return 
     */
    public int getRepeat();
    
    /**
     * 
     * @return 
     */
    public int getMmsi();
    
    /**
     * 
     * @return 
     */
    public MMSIType getMMSIType();
    
    /**
     * 
     * @return 
     */
    public boolean hasValidMmsi();
    
    /**
     * 
     * @return 
     */
    public boolean hasPosition();
    
    /**
     * 
     * @return 
     */
    public Point getPosition();
    
    /**
     * 
     * @return 
     */
    public boolean hasSubType();
    
    /**
     * 
     * @return 
     * @throws jais.exceptions.AISException 
     */
    public AISMessage getSubTypeInstance() throws AISException;
    
    /**
     * 
     * @throws jais.exceptions.AISException
     */
    public void decode() throws AISException;

    /**
     * Fields common to all messages
     */
    public static enum AISFieldMap implements FieldMap {

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
        private AISFieldMap( int startBit, int endBit ) {
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
