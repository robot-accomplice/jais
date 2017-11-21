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
public enum NavigationStatus {
    
    UNDER_WAY_USING_ENGINE( 0, "Under way using engine" ),
    AT_ANCHOR( 1, "At anchor" ),
    NOT_UNDER_COMMAND( 2, "Not under command" ),
    RESTRICTED_MANEUVERABILITY( 3, "Restricted maneuverability" ),
    CONSTRAINED_BY_HER_DRAUGHT( 4, "Constrained by her draught" ),
    MOORED( 5, "Moored" ),
    AGROUND( 6, "Aground" ),
    ENGAGED_IN_FISHING( 7, "Engaged in fishing" ),
    UNDER_WAY_SAILING( 8, "Under way sailing" ),
    RESERVED_FOR_WIG( 9, "Reserved for future amendment of Navigational Status for WIG" ),
    RESERVED_FOR_FUTURE_10( 10, "Reserved for future use - Type 10" ),
    RESERVED_FOR_FUTURE_11( 11, "Reserved for future use - Type 11" ),
    RESERVED_FOR_FUTURE_12( 12, "Reserved for future use - Type 12" ),
    RESERVED_FOR_FUTURE_13( 13, "Reserved for future use - Type 13" ),
    RESERVED_FOR_FUTURE_14( 14, "Reserved for future use - Type 14" ),
    NOT_DEFINED( 15, "Not defined (default)" );
    
    private final int _code;
    private final String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    NavigationStatus( int code, String description ) {
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
    public String getdescription() {
        return _description;
    }
  
    
    /**
     * 
     * @param code
     * @return 
     */
    public static NavigationStatus getForCode( int code ) {
        NavigationStatus navStatus = null;

        for( NavigationStatus ns : NavigationStatus.values() ) {
            if( ns.getCode() == code ) {
                navStatus = ns;
                break;
            }
        }
            
        return navStatus;
    }
}
