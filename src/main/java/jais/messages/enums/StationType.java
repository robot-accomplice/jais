/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum StationType {
    
    DEFAULT( 0, "All types of mobiles (default)" ),
    RESERVED( 1, "Reserved for future use" ),
    CLASS_B( 2, "All types of Class B mobile stations" ),
    SAR( 3, "SAR airborne mobile station" ),
    NAV_AID( 4, "Aid to Navigation station" ),
    CLASS_B_SHIPBORNE( 5, "Class B shipborne mobile station (IEC62287 only)" ),
    REGIONAL6( 6, "Regional use and inland waterways" ),
    REGIONAL7( 7, "Regional use and inland waterways" ),
    REGIONAL8( 8, "Regional use and inland waterways" ),
    REGIONAL9( 9, "Regional use and inland waterways" ),
    RESERVED10( 10, "Reserved for future use" ),
    RESERVED11( 11, "Reserved for future use" ),
    RESERVED12( 12, "Reserved for future use" ),
    RESERVED13( 13, "Reserved for future use" ),
    RESERVED14( 14, "Reserved for future use" ),
    RESERVED15( 15, "Reserved for future use" );
    
    private int _code;
    private String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    StationType( int code, String description ) {
        _code = code;
        _description = description;
    }
    
    /**
     * 
     * @return 
     */
    public int getCode() {
        return _code;
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
     * @param code
     * @return 
     */
    public static StationType getForCode( int code ) {
       StationType st = null;
       
       for( StationType type : StationType.values() ) {
           if( type.getCode() == code ) {
               st = type;
               break;
           }
       }
       
       return st;
    }
}
