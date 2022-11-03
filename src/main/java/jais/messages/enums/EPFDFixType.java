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

import lombok.Getter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public enum EPFDFixType {

    DEFAULT(0, "Undefined"),
    GPS(1, "GPS"),
    GLONASS(2, "GLONASS"),
    COMBINED_GPS_GLONASS(3, "Combined GPS/GLONASS"),
    LORAN_C(4, "Loran-C"),
    CHAYKA(5, "Chayka"),
    INTEGRATED_NAVIGATION_SYSTEM(6, "Integrated navigation system"),
    SURVEYED(7, "Surveyed"),
    GALILEO(8, "Galileo"),
    UNDEFINED(15, "Undefined");

    private final int code;
    private final String description;

    /**
     * 
     * @param code the NMEA defined numeric code for the EPFDFixType
     * @param description the NMEA defined description for the EPFDFixType
     */
    EPFDFixType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 
     * @param code the numeric code we wish to use for EPFDFixType retrieval
     * @return the EPFDFixType, if there is a match, null if there isn't
     */
    public static EPFDFixType getForCode(int code) {
        for (EPFDFixType type : EPFDFixType.values()) {
            if (type.getCode() == code)
                return type;
        }

        return null;
    }
}
