/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.messages.enums.AISMessageType;
import jais.exceptions.AISException;
import jais.AISPacket;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class PositionReportClassA extends PositionReportBase {

    /**
     * 
     * @param aisPackets 
     * @throws jais.exceptions.AISException 
     */
    public PositionReportClassA( AISPacket... aisPackets ) throws AISException {
        super( AISMessageType.POSITION_REPORT_CLASS_A, aisPackets );
    }
}
