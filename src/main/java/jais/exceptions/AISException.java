/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.exceptions;

/**
 *
 * @author vermi
 */
public class AISException extends Exception {

        /**
     * 
     * @param message 
     */
    public AISException( String message ) {
        super( message );
    }
    
    /**
     * 
     * @param message
     * @param t 
     */
    public AISException( String message, Throwable t ) {
        super( message, t );
    }
}
