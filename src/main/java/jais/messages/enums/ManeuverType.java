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
public enum ManeuverType {
    
    NOT_AVAILABLE( 0, "Not available (default)" ),
    NO_SPECIAL_MANEUVER( 1, "No special maneuver" ),
    SPECIAL_MANEUVER( 2, "Special maneuver" );  // ie, regional passing arrangement

    private final int _code;
    private final String _description;
    
    /**
     * 
     * @param code
     * @param description 
     */
    ManeuverType( int code, String description ) {
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
    public static ManeuverType getForCode( int code ) {
        ManeuverType mType = null;
        
        for( ManeuverType type : ManeuverType.values() ) {
            if( type.getCode() == code ) {
                mType = type;
                break;
            }
        }
        
        return mType;
    }
    
}
