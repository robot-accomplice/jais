/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
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
public enum EPFDFixType {
    
    DEFAULT( 0, "Undefined" ),
    GPS( 1, "GPS" ),
    GLONASS( 2, "GLONASS" ),
    COMBINED_GPS_GLONASS( 3, "Combined GPS/GLONASS" ),
    LORAN_C( 4, "Loran-C" ),
    CHAYKA( 5, "Chayka" ),
    INTEGRATED_NAVIGATION_SYSTEM( 6, "Integrated navigation system" ),
    SURVEYED( 7, "Surveyed" ),
    GALILEO( 8, "Galileo" ),
    UNDEFINED( 15, "Undefined" );
    
    private final int _code;
    private final String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    EPFDFixType( int code, String description ) {
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
    public static EPFDFixType getForCode( int code ) {
        EPFDFixType typeForCode = null;
        
        for( EPFDFixType type : EPFDFixType.values() ) {
            if( type.getCode() == code ) {
                typeForCode = type;
                break;
            }
        }
        
        return typeForCode;
    }
}
