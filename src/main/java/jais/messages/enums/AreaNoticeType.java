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
public enum AreaNoticeType {

    CAUTION_MARINE_MAMMALS_HABITAT( 0, 
            "Caution Area:  Marine mammals habitat" ),
    CAUTION_MARINE_MAMMALS_REDUCE_SPEED( 1, 
            "Caution Area: Marine mammals in area - reduce speed" ), 
    CAUTION_MARINE_MAMMALS_STAY_CLEAR( 2, 
            "Caution Area: Marine mammals in area - stay clear" ),
    CAUTION_MARINE_MAMMALS_REPORT_SIGHTINGS( 3, 
            "Caution Area: Marine mammals in area - report sightings" ),
    CAUTION_PROTECTED_HABITAT_REDUCE_SPEED( 4, 
            "Caution Area: Protected habitat - reduce speed" ),
    CAUTION_PROTECTED_HABITAT_STAY_CLEAR( 5,
            "Caution Area: Protected habitat - stay clear" ),
    CAUTION_PROTECTED_HABITAT_NO_FISHING_OR_ANCHORING( 6, 
            "Caution Area: Protected habitat - no fishing or anchoring" ),
    CAUTION_DERELICTS( 7, 
            "Caution Area: Derelicts (drifting objects)" ),
    CAUTION_TRAFFIC_CONGESTION( 8, 
            "Caution Area: Traffic congestion" ),
    CAUTION_MARINE_EVENT( 9, 
            "Caution Area: Marine event" ),
    CAUTION_DIVERS_DOWN( 10, 
            "Caution Area: Divers down" ),
    CAUTION_SWIM_AREA( 11, 
            "Caution Area: Swim area" ),
    CAUTION_DREDGE_OPERATIONS( 12, 
            "Caution Area: Dredge operations" ),
    CAUTION_SURVEY_OPERATIONS( 13, 
            "Caution Area: Survey operations" ),
    CAUTION_UNDERWATER_OPERATIONS( 14, 
            "Caution Area: Underwater operations" ),
    CAUTION_SEAPLANE_OPERATIONS( 15, 
            "Caution Area: Seaplane operations" ),
            
    ;

    private int _code;
    private String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    AreaNoticeType( int code, String description ) {
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

    
    /**
     * 
     * @param code
     * @return 
     */
    public static AreaNoticeType getForCode( int code ) {
       AreaNoticeType t = null;
       
       for( AreaNoticeType type : AreaNoticeType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
