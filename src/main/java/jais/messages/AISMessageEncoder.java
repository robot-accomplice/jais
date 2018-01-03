/*
 * Copyright 2017 Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}.
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
package jais.messages;

import java.util.BitSet;

/**
 *
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class AISMessageEncoder {
    
    /**
     * 
     * @param value
     * @param bits
     * @param startAt 
     */
    public static void encodeInt( int value, BitSet bits, int startAt ) {
        while( value != 0 ) {
            if( value % 2L != 0 ) bits.set( startAt );
            ++startAt;
            value = value >>> 1;
        }
    }
    
    /**
     * 
     * @param speed
     * @param bits
     * @param startAt 
     */
    public static void encodeSpeed( float speed, BitSet bits, int startAt ) {
        encodeInt( ( int )( speed * 10.f ), bits, startAt );
    }
    
    /**
     * 
     * @param course
     * @param bits
     * @param startAt 
     */
    public static void encodeCourse( float course, BitSet bits, int startAt ) {
        
    }
    
    /**
     * 
     * @param turn
     * @param bits
     * @param startAt 
     */
    public static void encodeTurn( double turn, BitSet bits, int startAt ) {
        encodeInt( ( int )( Math.sqrt( turn ) * 4.733 ), bits, startAt );
    }
    
    /**
     * 
     * @param lon
     * @param bits
     * @param startAt 
     */
    public static void encodeLongitude( float lon, BitSet bits, int startAt ) {
        encodeInt( ( int )( lon * ( 60 * 10000 ) ), bits, startAt );
    }
    
    /**
     * 
     * @param lat
     * @param bits
     * @param startAt 
     */
    public static void encodeLatitude( float lat, BitSet bits, int startAt ) {
        encodeInt( ( int )( lat * ( 60 * 1000 ) ), bits, startAt );
    }
}
