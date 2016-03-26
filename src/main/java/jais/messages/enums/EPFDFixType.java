/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public enum EPFDFixType {
    
    DEFAULT( 0, "Undefined" ),
    GPS( 1, "GPS" ),
    GLONASS( 2, "GLONASS" ),
    COMBINED_GPS_GLONASS( 3, "Combined GPS/GLONASS" ),
    LORAN_C( 4, "Loran-C" ),
    CHAYKA( 5, "Chayka" ),
    INTEGRATED_NAVIGATION_SYSTEM( 6, "Integrated navigation system" ),
    SURVEYED( 7, "Surveyed" ),
    GALILEO( 8, "Galileo" ),
    UNDEFINED( 15, "Undefined" );
    
    private int _code;
    private String _description;
    
    /**
     * 
     * @param id
     * @param description 
     */
    private EPFDFixType( int code, String description ) {
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
    public static EPFDFixType getForCode( int code ) {
        EPFDFixType typeForCode = null;
        
        for( EPFDFixType type : EPFDFixType.values() ) {
            if( type.getCode() == code ) {
                typeForCode = type;
                break;
            }
        }
        
        return typeForCode;
    }
}
