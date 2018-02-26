/*
 * Copyright 2016 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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

/**
 *
 * @author Jonathan Machen
 */
public enum AISMessageType {

    // these are sent every 2 to 10 seconds while underway (depending on speed)
    // and every 3 minutes while at anchor and stationary
    POSITION_REPORT_CLASS_A( 1, "Position Report Class A" ),
    POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE( 2, "Position Report Class A (Assigned Schedule)" ),
    POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION( 3, "Position Report Class A (Response to interrogation)" ),
    BASE_STATION_REPORT( 4, "Base Station Report" ),
    // sent every 6 minutes
    STATIC_AND_VOYAGE_RELATED_DATA( 5, "Static & Voyage Related Data" ),
    
    // local authority 
    BINARY_ADDRESSED_MESSAGE( 6, "Binary Addressed Message" ),
    
    BINARY_ACKNOWLEDGE( 7, "Binary Acknowledge" ),
    
    // encrypted private message, usually military
    BINARY_BROADCAST_MESSAGE( 8, "Binary Broadcast Message" ),
    STANDARD_SAR_AIRCRAFT_POSITION_REPORT( 9, "Standard SAR Aircraft Position Report" ),
    UTC_AND_DATE_INQUIRY( 10, "UTC & Date Inquiry" ),
    UTC_AND_DATE_RESPONSE( 11, "UTC & Date Response" ),
    
    // text messaging, safety-related, traffic control, chatter
    ADDRESSED_SAFETY_RELATED_MESSAGE( 12, "Addressed Safety Related Message" ),
    
    SAFETY_RELATED_ACKNOWLEDGEMENT( 13, "Safety Related Acknowledgement" ),

    // text messaging, safety-related, traffic control, chatter
    SAFETY_RELATED_BROADCAST_MESSAGE( 14, "Safety Related Broadcast Message" ),
    
    INTERROGATION( 15, "Interrogation" ),
    ASSIGNMENT_MODE_COMMAND( 16, "Assignment Mode Command" ),
    DGNSS_BROADCAST_BINARY_MESSAGE( 17, "DGNSS Broadcast Binary Message" ),
    STANDARD_CLASS_B_CS_POSITION_REPORT( 18, "Standard Class B CS Position Report" ),
    EXTENDED_CLASS_B_CS_POSITION_REPORT( 19, "Extended Class B Equipment Position Report" ),
    DATA_LINK_MANAGEMENT_MESSAGE( 20, "Datalink Management Message" ),
    AID_TO_NAVIGATION_REPORT( 21, "Aid-to-Navigation Report" ), 
    CHANNEL_MANAGEMENT( 22, "Channel Management" ), 
    GROUP_ASSIGNMENT_COMMAND( 23, "Group Assignment Command" ), 
    STATIC_DATA_REPORT( 24, "Static Data Report" ), 
    SINGLE_SLOT_BINARY_MESSAGE( 25, "Single Slot Binary Message" ), 
    MULTIPLE_SLOT_BINARY_MESSAGE( 26, "Multiple Slot Binary Message With Communications State" ), 
    POSITION_REPORT_FOR_LONG_RANGE_APPLICATIONS( 27, "Position Report For Long Range Applications" );
    
    /*
     * private variables 
     */
    private final int _id;
    private final String _description;
    
    /**
     * 
     * @param description 
     */
    AISMessageType( int id, String description ) {
        _id = id;
        _description = description;
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
}
