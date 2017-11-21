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

package jais.io;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class ConnectionTools {
    
    /**
     * 
     * @param time
     * @param unit
     * @return 
     */
    public static String runningTime( long time, TimeUnit unit ) {
        long days = getDays( time, unit );
        long hours = getHours( 
                ( time - ( unit.convert( days, TimeUnit.DAYS ) ) ), 
                unit );
        long mins = getMinutes( 
                ( time - ( 
                        unit.convert( days, TimeUnit.DAYS ) + 
                        unit.convert( hours, TimeUnit.HOURS ) ) ), 
                unit );
        long secs = getSeconds( 
                ( time - ( 
                        unit.convert( days, TimeUnit.DAYS ) + 
                        unit.convert( hours, TimeUnit.HOURS ) + 
                        unit.convert( mins, TimeUnit.MINUTES ) ) ), 
                unit );
        
        return  days + " days, " + hours + " hrs, " + 
                mins + " mins, " + secs + " secs";
    }
    
    /**
     * 
     * @param time
     * @return 
     */
    public static String runningTime( long time ) {
        return runningTime( time, TimeUnit.MINUTES );
    }
    
    /**
     * 
     * @param time
     * @param unit
     * @return 
     */
    public static long getDays( long time, TimeUnit unit ) {
        return TimeUnit.DAYS.convert( time, unit );
    }
    
    /**
     * 
     * @param time
     * @param unit
     * @return 
     */
    public static long getHours( long time, TimeUnit unit ) {
        return TimeUnit.HOURS.convert( time, unit );
    }
    
    /**
     * 
     * @param time
     * @param unit
     * @return 
     */
    public static long getMinutes( long time, TimeUnit unit ) {
        return TimeUnit.MINUTES.convert( time, unit );
    }
    
    /**
     * 
     * @param time
     * @param unit
     * @return 
     */
    public static long getSeconds( long time, TimeUnit unit ) {
        return TimeUnit.SECONDS.convert( time, unit );
    }
}
