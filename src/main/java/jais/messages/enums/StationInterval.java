/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum StationInterval {

    DEFAULT( 0, "Autonomous" ),
    TEN_MINUTES( 1, "10 Minutes" ),
    SIX_MINUTES( 2, "6 Minutes" ),
    THREE_MINUTES( 3, "3 Minutes" ),
    ONE_MINUTE( 4, "1 Minute" ),
    THIRTY_SECONDS( 5, "30 Seconds" ),
    FIFTEEN_SECONDS( 6, "15 Seconds" ),
    TEN_SECONDS( 7, "10 Seconds" ),
    FIVE_SECONDS( 8, "5 Seconds" ),
    NEXT_SHORTER( 9, "Next Shorter Reporting Interval" ),
    NEXT_LONGER( 10, "Next Longer Reporting Interval" ),
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
    StationInterval( int code, String description ) {
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
    public static StationInterval getForCode( int code ) {
       StationInterval t = null;
       
       for( StationInterval type : StationInterval.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
