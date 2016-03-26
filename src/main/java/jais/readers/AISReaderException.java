package jais.readers;

import jais.exceptions.AISException;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class AISReaderException extends AISException {

    /**
     * Constructs an instance of <code>AISReaderException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AISReaderException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an instance of <code>AISReaderException</code> with the
     * specified detail message and parent exception
     * 
     * @param msg the detail message
     * @param parent the parent exception
     */
    public AISReaderException( String msg, Exception parent ) {
        super( msg, parent );
    }
}
