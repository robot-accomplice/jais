/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum MarpolAnnexIType {

    DEFAULT( 0, "N/A (default)" ),
    ASPHALT( 1, "Asphalt solutions" ),
    OILS( 2, "Oils" ),
    DISTILLATES( 3, "Distillates" ),
    GAS_OIL( 4, "Gas oil" ),
    GASOLINE_BLENDING_STOCKS( 5, "Gasoline blending stocks" ),
    GASOLINE( 6, "Gasoline" ),
    JET_FUELS( 7, "Jet fuels" ),
    NAPHTHA( 8, "Naphtha" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    MarpolAnnexIType( int code, String description ) {
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
    public static MarpolAnnexIType getForCode( int code ) {
       MarpolAnnexIType t = null;
       
       for( MarpolAnnexIType type : MarpolAnnexIType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
