/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.readers;

/**
 *
 * @author Jonathan Machen
 */
public interface AISReader extends AutoCloseable, Runnable {
 
    public abstract void read() throws AISReaderException;
    
    @Override
    public abstract void close();
    
    public abstract long getBadPacketCount();
}
