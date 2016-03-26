/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum RouteType {

    UNDEFINED( 0, "Undefined (default)" ),
    MANDATORY( 1, "Mandatory" ),
    RECOMMENDED( 2, "Recommended" ),
    ALTERNATIVE( 3, "Alternative" ),
    RECOMMENDED_ICE( 4, "Recommended route through ice" ),
    SHIP_ROUTE_PLAN( 5, "Ship route plan" ),
    CANCEL_ROUTE( 31, "Cancel route identified by message linkage" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    RouteType( int code, String description ) {
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
    public static RouteType getForCode( int code ) {
       RouteType t = null;
       
       for( RouteType type : RouteType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
