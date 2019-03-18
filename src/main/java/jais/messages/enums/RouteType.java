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
 * @author Jonathan Machen
 */
public enum RouteType {

    UNDEFINED( 0, "Undefined (default)" ),
    MANDATORY( 1, "Mandatory" ),
    RECOMMENDED( 2, "Recommended" ),
    ALTERNATIVE( 3, "Alternative" ),
    RECOMMENDED_ICE( 4, "Recommended route through ice" ),
    SHIP_ROUTE_PLAN( 5, "Ship route plan" ),
    CANCEL_ROUTE( 31, "Cancel route identified by message linkage" );

    private final int _code;
    private final String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    RouteType( int code, String description ) {
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
    public static RouteType getForCode( int code ) {
       for( RouteType type : RouteType.values() ) {
           if( type.getCode() == code ) return type;
       }
       
       return null;
    }
}
