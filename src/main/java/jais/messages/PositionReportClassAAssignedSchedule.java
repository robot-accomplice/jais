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
public class PositionReportClassAAssignedSchedule extends PositionReportBase {

    /**
     * 
     * @param aisPackets 
     * @throws jais.exceptions.AISException 
     */
    public PositionReportClassAAssignedSchedule( AISPacket... aisPackets ) 
            throws AISException {
        super( AISMessageType.POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE, aisPackets );
    }
}
