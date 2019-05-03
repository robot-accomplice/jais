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
public enum TransmitMode {
    
    DEFAULT( 0, "TxA/TxB, RxA/RxB (default" ),
    TXA_RXA_B( 1, "TxA, RxA/RxB" ),
    TXB_RXA_B( 2, "TxB, RxA/RxB" ),
    RESERVED( 3, "Reserved for Future Use" );
    
    private final int _code;
    private final String _description;

    /**
     * 
     * @param code
     * @param description 
     */
    TransmitMode( int code, String description ) {
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
    public static TransmitMode getForCode( int code ) {
       for( TransmitMode type : TransmitMode.values() ) {
           if( type.getCode() == code ) return type;
       }
       
       return null;
    }
}
