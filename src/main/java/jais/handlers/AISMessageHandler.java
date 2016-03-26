/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jais.handlers;

import jais.messages.AISMessage;

/**
 *
 * @author Jonathan Machen
 */
public interface AISMessageHandler extends AISHandler {
    
    public abstract void processMessage( AISMessage message );
}
