/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
public enum NavaidType {
    
    DEFAULT( 0, "Type of Aid to Navigation not specified." ),
    REFERENCE_POINT( 1, "Reference point" ),
    RACON( 2, "RACON (radar transponder marking a navigation hazard." ),
    FIXED_STRUCTURE( 3, "Fixed structure off shore, such as oil platforms, " +
            "wind farms, rigs. (Note: This code should identify an " +
            "obstruction that is fitted with an Aid-to-Navigation AIS " +
            "station.)" ),
    SPARE( 4, "Spare, reserved for future use." ),
    LIGHT_WITHOUT_SECTORS( 5, "Light, without sectors" ),
    LIGHT_WITH_SECTORS( 6, "Light, with sectors" ),
    LEADING_LIGHT_FRONT( 7, "Leading Light Front" ),
    LEADING_LIGHT_REAR( 8, "Leading Light Rear" ),
    BEACON_CARDINAL_N( 9, "Beacon, Cardinal N" ),
    BEACON_CARDINAL_E( 10, "Beacon, Cardinal E" ),
    BEACON_CARDINAL_S( 11, "Beacon, Cardinal S" ),
    BEACON_CARDINAL_W( 12, "Beacon, Cardinal W" ),
    BEACON_PORT_HAND( 13, "Beacon, Port hand" ),
    BEACON_STARBOARD_HAND( 14, "Starboard hand" ),
    BEACON_PREFFERED_CHANNEL_PORT_HAND( 15, "Beacon, "
            + "Preferred Channel port hand." ),
    BEACON_PREFERRED_CHANNEL_STARBOARD_HAND( 16, "Beacon, "
            + "Preferred Channel starboard hand." ),
    BEACON_ISOLATED_DANGER( 17, "Beacon, Isolated danger" ),
    BEACON_SAFE_WATER( 18, "Beacon, Safe water" ),
    BEACON_SPECIAL_MARK( 19, "Beacon, Special mark" ),
    CARDINAL_MARK_N( 20, "Cardinal Mark N" ),
    CARDINAL_MARK_E( 21, "Cardinal Mark E" ),
    CARDINAL_MARK_S( 22, "Cardinal Mark S" ),
    CARDINAL_MARK_W( 23, "Cardinal Mark W" ),
    PORT_HAND_MARK( 24, "Port hand Mark" ),
    STARBOARD_HAND_MARK( 25, "Starboard hand Mark" ),
    PREFERRED_CHANNEL_PORT_HAND( 26, "Preferred Channel Port hand" ),
    PREFERRED_CHANNEL_STARBOARD_HAND( 27, "Preferred Channel starboard hand" ),
    ISOLATED_DANGER( 28, "Isolated danger" ),
    SAFE_WATER( 29, "Safe Water" ),
    SPECIAL_MARK( 30, "Special Mark" ),
    LIGHT_VESSEL_LANBY_RIGS( 31, "Light Vessel/LANBY/Rigs" );
    
    private final int _code;
    private final String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    NavaidType( int code, String description ) {
        _code = code;
        _description = description;
    }
    
    /**
     * 
     * @return 
     */
    public int getCode() {
       return _code; 
    }
    
    /**
     * 
     * @return 
     */
    public String getDescription() {
        return _description;
    }
    
    /***
     * 
     * @param code
     * @return 
     */
    public static NavaidType getForCode( int code ) {
        NavaidType type = null;
        
        for( NavaidType t : NavaidType.values() ) {
            if( t.getCode() == code ) {
                type = t;
                break;
            }
        }
        
        return type;
    }
}
