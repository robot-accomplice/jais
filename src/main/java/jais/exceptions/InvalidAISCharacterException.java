/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.exceptions;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class InvalidAISCharacterException extends AISException {

    /**
     * 
     * @param message 
     */
    public InvalidAISCharacterException( String message ) {
       super( message );
    }
    
    /**
     * 
     * @param message
     * @param t 
     */
    public InvalidAISCharacterException( String message, Throwable t ) {
        super( message, t );
        super.setStackTrace( t.getStackTrace() );
    }
}
