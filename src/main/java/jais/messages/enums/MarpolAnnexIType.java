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
public enum MarpolAnnexIType {

    DEFAULT( 0, "N/A (default)" ),
    ASPHALT( 1, "Asphalt solutions" ),
    OILS( 2, "Oils" ),
    DISTILLATES( 3, "Distillates" ),
    GAS_OIL( 4, "Gas oil" ),
    GASOLINE_BLENDING_STOCKS( 5, "Gasoline blending stocks" ),
    GASOLINE( 6, "Gasoline" ),
    JET_FUELS( 7, "Jet fuels" ),
    NAPHTHA( 8, "Naphtha" );

    private final int _code;
    private final String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    MarpolAnnexIType( int code, String description ) {
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
    public static MarpolAnnexIType getForCode( int code ) {
       MarpolAnnexIType t = null;
       
       for( MarpolAnnexIType type : MarpolAnnexIType.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
