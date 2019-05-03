/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jais.messages.enums;

import jais.messages.AISMessage;
import jais.messages.MultipleSlotBinaryMessage;
import jais.messages.UTCDateResponse;
import jais.messages.BinaryAcknowledge;
import jais.messages.DataLinkManagementMessage;
import jais.messages.StandardSARAircraftPositionReport;
import jais.messages.GroupAssignmentCommand;
import jais.messages.SingleSlotBinaryMessage;
import jais.messages.PositionReportClassAAssignedSchedule;
import jais.messages.SafetyRelatedBroadcastMessage;
import jais.messages.StaticDataReport;
import jais.messages.BinaryBroadcastMessage;
import jais.messages.Interrogation;
import jais.messages.StandardClassBCSPositionReport;
import jais.messages.ChannelManagement;
import jais.messages.PositionReportClassAResponseToInterrogation;
import jais.messages.PositionReportClassA;
import jais.messages.SafetyRelatedAcknowledgement;
import jais.messages.AddressedSafetyRelatedMessage;
import jais.messages.LongRangeAISBroadcastMessage;
import jais.messages.AidToNavigationReport;
import jais.messages.BaseStationReport;
import jais.messages.AssignmentModeCommand;
import jais.messages.UTCDateInquiry;
import jais.messages.DGNSSBroadcastBinaryMessage;
import jais.messages.StaticAndVoyageRelatedData;
import jais.messages.ExtendedClassBCSPositionReport;
import jais.messages.BinaryAddressedMessageBase;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public enum AISMessageType {

    // these are sent every 2 to 10 seconds while underway (depending on speed)
    // and every 3 minutes while at anchor and stationary
    POSITION_REPORT_CLASS_A( 1, 
            "Position Report Class A", 
            PositionReportClassA.class ),
    POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE( 2, 
            "Position Report Class A (Assigned Schedule)", 
            PositionReportClassAAssignedSchedule.class ),
    POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION( 3, 
            "Position Report Class A (Response to interrogation)",
            PositionReportClassAResponseToInterrogation.class ),
    
    BASE_STATION_REPORT( 4, 
            "Base Station Report", 
            BaseStationReport.class ),
    
    // sent every 6 minutes
    STATIC_AND_VOYAGE_RELATED_DATA( 5, 
            "Static & Voyage Related Data", 
            StaticAndVoyageRelatedData.class),
    
    // local authority 
    BINARY_ADDRESSED_MESSAGE( 6, 
            "Binary Addressed Message", 
            BinaryAddressedMessageBase.class ),
    
    BINARY_ACKNOWLEDGE( 7, 
            "Binary Acknowledge", 
            BinaryAcknowledge.class ),
    
    // encrypted private message, usually military
    BINARY_BROADCAST_MESSAGE( 8, 
            "Binary Broadcast Message", 
            BinaryBroadcastMessage.class ),
    STANDARD_SAR_AIRCRAFT_POSITION_REPORT( 9, 
            "Standard SAR Aircraft Position Report", 
            StandardSARAircraftPositionReport.class ),
    UTC_AND_DATE_INQUIRY( 10, 
            "UTC & Date Inquiry", 
            UTCDateInquiry.class ),
    UTC_AND_DATE_RESPONSE( 11, 
            "UTC & Date Response", 
            UTCDateResponse.class ),
    
    // text messaging, safety-related, traffic control, chatter
    ADDRESSED_SAFETY_RELATED_MESSAGE( 12, 
            "Addressed Safety Related Message", 
            AddressedSafetyRelatedMessage.class ),
    
    SAFETY_RELATED_ACKNOWLEDGEMENT( 13, 
            "Safety Related Acknowledgement", 
            SafetyRelatedAcknowledgement.class ),

    // text messaging, safety-related, traffic control, chatter
    SAFETY_RELATED_BROADCAST_MESSAGE( 14, 
            "Safety Related Broadcast Message", 
            SafetyRelatedBroadcastMessage.class ),
    
    INTERROGATION( 15, 
            "Interrogation", 
            Interrogation.class ),
    ASSIGNMENT_MODE_COMMAND( 16, 
            "Assignment Mode Command", 
            AssignmentModeCommand.class ),
    DGNSS_BROADCAST_BINARY_MESSAGE( 17, 
            "DGNSS Broadcast Binary Message", 
            DGNSSBroadcastBinaryMessage.class ),
    STANDARD_CLASS_B_CS_POSITION_REPORT( 18, 
            "Standard Class B CS Position Report", 
            StandardClassBCSPositionReport.class ),
    EXTENDED_CLASS_B_CS_POSITION_REPORT( 19, 
            "Extended Class B Equipment Position Report", 
            ExtendedClassBCSPositionReport.class ),
    DATA_LINK_MANAGEMENT_MESSAGE( 20, 
            "Datalink Management Message", 
            DataLinkManagementMessage.class ),
    AID_TO_NAVIGATION_REPORT( 21, 
            "Aid-to-Navigation Report", 
            AidToNavigationReport.class ),
    CHANNEL_MANAGEMENT( 22, 
            "Channel Management", 
            ChannelManagement.class ),
    GROUP_ASSIGNMENT_COMMAND( 23, 
            "Group Assignment Command", 
            GroupAssignmentCommand.class ),
    STATIC_DATA_REPORT( 24, 
            "Static Data Report", 
            StaticDataReport.class ),
    SINGLE_SLOT_BINARY_MESSAGE( 25, 
            "Single Slot Binary Message", 
            SingleSlotBinaryMessage.class ),
    MULTIPLE_SLOT_BINARY_MESSAGE( 26, 
            "Multiple Slot Binary Message With Communications State", 
            MultipleSlotBinaryMessage.class ),
    POSITION_REPORT_FOR_LONG_RANGE_APPLICATIONS( 27, 
            "Position Report For Long Range Applications", 
            LongRangeAISBroadcastMessage.class );
    /*
     * private variables 
     */
    private final int _id;
    private final String _description;
    private final Class<? extends AISMessage> _messageClass;
    
    /**
     * 
     * @param description 
     */
    AISMessageType( int id, String description, Class<? extends AISMessage> messageClass ) {
        _id = id;
        _description = description;
        _messageClass = messageClass;
    }
    
    /**
     * 
     * @return 
     */
    private int getId() {
        return _id;
    }
    
    /**
     * 
     * @return 
     */
    public String getDescription() {
        return _description;
    }
    
    /**
     * 
     * @return 
     */
    public Class<? extends AISMessage> getMessageClass() {
        return _messageClass;
    }
    
    /**
     * 
     * @param id 
     * @return  
     */
    public static AISMessageType fetchById( int id ) {
        AISMessageType foundType = null;
        
        for( AISMessageType type : AISMessageType.values() ) {
            if( type.getId() == id ) {
                foundType = type;
                break;
            }
        }
        
        return foundType;
    }
    
    /**
     * 
     * @param messageClass
     * @return 
     */
    public static AISMessageType fetchByMessageClass( Class<? extends AISMessage> messageClass ) {
        AISMessageType foundType = null;
        
        for( AISMessageType type : AISMessageType.values() ) {
            if( type.getMessageClass().equals( messageClass ) ) {
                foundType = type;
                break;
            }
        }
        
        return foundType;
    }
}
