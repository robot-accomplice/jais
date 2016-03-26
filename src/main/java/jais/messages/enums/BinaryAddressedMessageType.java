/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

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
 * @author Jonathan Machen
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
    
    private int _dac;
    private int _fid;
    private int _length;
    private Class _msgClass;
    private IMOType _source;
    private String _description;
    
    /**
     * 
     * @param dac
     * @param fid
     * @param description 
     */
    BinaryAddressedMessageType( int dac, int fid, int length, Class msgClass, 
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
     */
    public Class getMsgClass() {
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
     * @param imoType
     * @return 
     */
    public static BinaryAddressedMessageType fetch( int dac, int fid, int length ) {
       BinaryAddressedMessageType bamt = null;
       
       for( BinaryAddressedMessageType type : BinaryAddressedMessageType.values() ) {
           if( type.getDac() == dac && type.getFid() == fid && 
                   ( type.getLength() == length || type.getLength() == -1 ) ) {
               bamt = type;
               break;
           }
       }
       
       return bamt;
    }
}
