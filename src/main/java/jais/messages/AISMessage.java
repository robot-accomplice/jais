/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author Jonathan Machenathan Machen
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
