/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public enum ManeuverType {
    
    NOT_AVAILABLE( 0, "Not available (default)" ),
    NO_SPECIAL_MANEUVER( 1, "No special maneuver" ),
    SPECIAL_MANEUVER( 2, "Special maneuver" );  // ie, regional passing arrangement

    private int _code;
    private String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    private ManeuverType( int code, String description ) {
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
    public String getdescription() {
        return _description;
    }
    
    /**
     * 
     * @param code
     * @return 
     */
    public static ManeuverType getForCode( int code ) {
        ManeuverType mType = null;
        
        for( ManeuverType type : ManeuverType.values() ) {
            if( type.getCode() == code ) {
                mType = type;
                break;
            }
        }
        
        return mType;
    }
    
}
