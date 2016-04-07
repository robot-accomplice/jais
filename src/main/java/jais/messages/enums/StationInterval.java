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
public enum StationInterval {

    DEFAULT( 0, "Autonomous" ),
    TEN_MINUTES( 1, "10 Minutes" ),
    SIX_MINUTES( 2, "6 Minutes" ),
    THREE_MINUTES( 3, "3 Minutes" ),
    ONE_MINUTE( 4, "1 Minute" ),
    THIRTY_SECONDS( 5, "30 Seconds" ),
    FIFTEEN_SECONDS( 6, "15 Seconds" ),
    TEN_SECONDS( 7, "10 Seconds" ),
    FIVE_SECONDS( 8, "5 Seconds" ),
    NEXT_SHORTER( 9, "Next Shorter Reporting Interval" ),
    NEXT_LONGER( 10, "Next Longer Reporting Interval" ),
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
    StationInterval( int code, String description ) {
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
    public static StationInterval getForCode( int code ) {
       StationInterval t = null;
       
       for( StationInterval type : StationInterval.values() ) {
           if( type.getCode() == code ) {
               t = type;
               break;
           }
       }
       
       return t;
    }
}
