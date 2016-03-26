/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum CargoCode {

    NOT_AVAILABLE( 0, "Not available (default)" ),
    IMDG_CODE( 1, "IMDG code (in packed form)" ),
    IGC_CODE( 2, "IGC code" ),
    BC_CODE( 3, "BC code (from 1.1.2011 IMSBC)" ),
    MARPOL_ANNEX_I( 4, "MARPOL Annex I list of oils (appendix 1)" ),
    MARPOL_ANNEX_II( 5, "MARPOL Annex II IBC code" ),
    REGIONAL( 6, "Regional use" );

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    CargoCode( int code, String description ) {
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
    public static CargoCode getForCode( int code ) {
       CargoCode t = null;
       
       for( CargoCode type : CargoCode.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
