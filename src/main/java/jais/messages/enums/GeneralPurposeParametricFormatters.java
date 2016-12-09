/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.enums;

/**
 *
 * @author jmachen
 */
public enum GeneralPurposeParametricFormatters {
    
    AAM( "Waypoing Arrival Alarm" );
    
    public String description;
    
    /**
     * 
     * @param description 
     */
    private GeneralPurposeParametricFormatters( String description ) {
        this.description = description;
    }
}
