/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum MarpolAnnexIIType {

    DEFAULT( 0, "N/A (default)" ),
    CATEGORY_X( 1, "Category X" ),
    CATEGORY_Y( 2, "Category Y" ),
    CATEGORY_Z( 3, "Category Z" ),
    OTHER_SUBSTANCES( 4, "Other substances" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    MarpolAnnexIIType( int code, String description ) {
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
    public static MarpolAnnexIIType getForCode( int code ) {
       MarpolAnnexIIType t = null;
       
       for( MarpolAnnexIIType type : MarpolAnnexIIType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
