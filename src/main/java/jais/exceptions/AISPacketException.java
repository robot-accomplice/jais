/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.exceptions;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class AISPacketException extends AISException {
    
    /**
     * 
     * @param message 
     */
    public AISPacketException( String message ) {
        super( message );
    }
    
    /**
     * 
     * @param message
     * @param t 
     */
    public AISPacketException( String message, Throwable t ) {
        super( message, t );
    }
}
