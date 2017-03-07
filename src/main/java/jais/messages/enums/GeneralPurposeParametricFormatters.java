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
    
    AAM( "Waypoint Arrival Alarm" );
    
    public final String description;
    
    /**
     * 
     * @param description 
     */
    GeneralPurposeParametricFormatters( String description ) {
        this.description = description;
    }
}
