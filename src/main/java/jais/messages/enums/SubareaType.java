/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum SubareaType {

    CIRCLE_OR_POINT( 0, "Circle or point" ),
    RECTANGLE( 1, "Rectangle" ),
    SECTOR( 2, "Sector" ),
    POLYLINE( 3, "Polyline" ),
    POLYGON( 4, "Polygon" ),
    ASSOCIATED_TEXT( 5, "Associated text" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    SubareaType( int code, String description ) {
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
    public static SubareaType getForCode( int code ) {
       SubareaType t = null;
       
       for( SubareaType type : SubareaType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
