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

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public enum StationType {
    
    DEFAULT( 0, "All types of mobiles (default)" ),
    RESERVED( 1, "Reserved for future use" ),
    CLASS_B( 2, "All types of Class B mobile stations" ),
    SAR( 3, "SAR airborne mobile station" ),
    NAV_AID( 4, "Aid to Navigation station" ),
    CLASS_B_SHIPBORNE( 5, "Class B shipborne mobile station (IEC62287 only)" ),
    REGIONAL6( 6, "Regional use and inland waterways" ),
    REGIONAL7( 7, "Regional use and inland waterways" ),
    REGIONAL8( 8, "Regional use and inland waterways" ),
    REGIONAL9( 9, "Regional use and inland waterways" ),
    RESERVED10( 10, "Reserved for future use" ),
    RESERVED11( 11, "Reserved for future use" ),
    RESERVED12( 12, "Reserved for future use" ),
    RESERVED13( 13, "Reserved for future use" ),
    RESERVED14( 14, "Reserved for future use" ),
    RESERVED15( 15, "Reserved for future use" );
    
    private final int _code;
    private final String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    StationType( int code, String description ) {
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
    public static StationType getForCode( int code ) {
       for( StationType type : StationType.values() ) {
           if( type.getCode() == code ) return type;
       }
       
       return null;
    }
}
