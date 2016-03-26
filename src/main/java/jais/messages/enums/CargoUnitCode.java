/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum CargoUnitCode {

    DEFAULT( 0, "Not available (default)" ),
    KILOGRAMS( 1, "Kilograms" ),
    METRIC_TONS( 2, "Metric tons" ),
    METRIC_KILOTONS( 3, "Metric kilotons" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    CargoUnitCode( int code, String description ) {
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
    public static CargoUnitCode getForCode( int code ) {
       CargoUnitCode t = null;
       
       for( CargoUnitCode type : CargoUnitCode.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
