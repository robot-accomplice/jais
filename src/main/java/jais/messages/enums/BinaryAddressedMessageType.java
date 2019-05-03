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
package jais.messages.enums;

import jais.messages.BinaryAddressedMessageBase;
import jais.messages.binaryaddressed.IMO236TidalWindow;
import jais.messages.binaryaddressed.IMO236NumberOfPersonsOnBoard;
import jais.messages.binaryaddressed.IMO289NumberOfPersonsOnBoard;
import jais.messages.binaryaddressed.IMO289DangerousCargoIndication;
import jais.messages.binaryaddressed.IMO289RouteInformation;
import jais.messages.binaryaddressed.IMO289AreaNotice;
import jais.messages.binaryaddressed.IMO289TidalWindow;
import jais.messages.binaryaddressed.IMO289TextDescription;
import jais.messages.binaryaddressed.IMO236DangerousCargoIndication;
import jais.messages.binaryaddressed.IMO289ClearanceTimeToEnterPort;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public enum BinaryAddressedMessageType {
    
    DANGEROUS_CARGO_INDICATION_DEPRECATED( 1, 12, -1, 
            IMO236DangerousCargoIndication.class, IMOType.IMO236,
            "Dangerous cargo indication (deprecated)" ),
    TIDAL_WINDOW_DEPRECATED( 1, 14, -1, 
            IMO236TidalWindow.class, IMOType.IMO236,
            "Tidal window (deprecated)" ), // 190 - 376
    NUMBER_OF_PERSONS_ON_BOARD_DEPRECATED( 1, 16, 72, 
            IMO236NumberOfPersonsOnBoard.class, IMOType.IMO236,
            "Number of persons on board (deprecated)" ),
    NUMBER_OF_PERSONS_ON_BOARD( 1, 16, 136, 
            IMO289NumberOfPersonsOnBoard.class, IMOType.IMO289, 
            "Number of persons on board" ),
    CLEARANCE_TIME_TO_ENTER_PORT( 1, 18, -1, 
            IMO289ClearanceTimeToEnterPort.class, IMOType.IMO289, 
            "Clearance time to enter port" ),
    AREA_NOTICE( 1, 23, -1, 
            IMO289AreaNotice.class, IMOType.IMO289, 
            "Area notice (addressed)" ),
    DANGEROUS_CARGO_INDICATION( 1, 25, -1, 
            IMO289DangerousCargoIndication.class, IMOType.IMO289, 
            "Dangerous Cargo indication" ),
    ROUTE_INFORMATION( 1, 28, -1, 
            IMO289RouteInformation.class, IMOType.IMO289, 
            "Route info addressed" ),
    TEXT_DESCRIPTION( 1, 30, -1, 
            IMO289TextDescription.class, IMOType.IMO289, 
            "Text description addressed" ),
    TIDAL_WINDOW( 1, 32, -1, 
            IMO289TidalWindow.class, IMOType.IMO289, 
            "Tidal Window" );
    
    private final int _dac;
    private final int _fid;
    private final int _length;
    private final Class<? extends BinaryAddressedMessageBase> _msgClass;
    private final IMOType _source;
    private final String _description;
    
    /**
     * 
     * @param dac
     * @param fid
     * @param description 
     */
    BinaryAddressedMessageType( int dac, int fid, int length, Class<? extends BinaryAddressedMessageBase> msgClass, 
            IMOType source, String description ) {
        _dac = dac;
        _fid = fid;
        _length = length;
        _msgClass = msgClass;
        _source = source;
        _description = description;
    }
    
    /**
     * 
     * @return 
     */
    public int getDac() {
        return _dac;
    }
    
    /**
     * 
     * @return 
     */
    public int getFid() {
        return _fid;
    }
    
    /**
     * 
     * @return 
     */
    public int getLength() {
        return _length;
    }
    
    /**
     * 
     * @return 
     */
    public Class<? extends BinaryAddressedMessageBase> getMsgClass() {
        return _msgClass;
    }
    
    /**
     * 
     * @return 
     */
    public IMOType getSource() {
        return _source;
    }
    
    /**
     * 
     * @return 
     */
    public String getDescription() {
        return _description;
    }
    
    /**
     * 
     * @param dac
     * @param fid
     * @param length
     * @return 
     */
    public static BinaryAddressedMessageType fetch( int dac, int fid, int length ) {
       for( BinaryAddressedMessageType type : BinaryAddressedMessageType.values() ) {
           if( type.getDac() == dac && type.getFid() == fid && 
                   ( type.getLength() == length || type.getLength() == -1 ) ) {
               return type;
           }
       }
       
       return null;
    }
}
