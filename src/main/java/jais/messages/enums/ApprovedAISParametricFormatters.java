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
public enum ApprovedAISParametricFormatters {
    
    ABK( "AIS Addressed and Binary Broadcast Acknowledgement" );
    
    public String description;
    
    /**
     * 
     * @param description 
     */
    private ApprovedAISParametricFormatters( String description ) {
        this.description = description;
    }
}
