/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

/**
 *
 * @author Jonathan Machen
 */
public enum TransmitMode {
    
    DEFAULT( 0, "TxA/TxB, RxA/RxB (default" ),
    TXA_RXA_B( 1, "TxA, RxA/RxB" ),
    TXB_RXA_B( 2, "TxB, RxA/RxB" ),
    RESERVED( 3, "Reserved for Future Use" );
    
    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    TransmitMode( int code, String description ) {
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
    public static TransmitMode getForCode( int code ) {
       TransmitMode tm = null;
       
       for( TransmitMode type : TransmitMode.values() ) {
           if( type.getCode() == code ) {
               tm = type;
               break;
           }
       }
       
       return tm;
    }
}
