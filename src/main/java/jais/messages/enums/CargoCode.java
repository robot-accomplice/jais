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
public enum CargoCode {

    NOT_AVAILABLE(0, "Not available (default)"),
    IMDG_CODE(1, "IMDG code (in packed form)"),
    IGC_CODE(2, "IGC code"),
    BC_CODE(3, "BC code (from 1.1.2011 IMSBC)"),
    MARPOL_ANNEX_I(4, "MARPOL Annex I list of oils (appendix 1)"),
    MARPOL_ANNEX_II(5, "MARPOL Annex II IBC code"),
    REGIONAL(6, "Regional use");

    private final int code;
    private final String description;

    /**
     * 
     * @param code the NMEA defined numeric code for the Cargo
     * @param description the NMEA provided description for the Cargo type
     */
    CargoCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 
     * @param code the numeric code we want to use for CargoCode retrieval
     * @return the CargoCode entry that matches the specified numeric code or null if there is no match
     */
    public static CargoCode getForCode(int code) {
        for (CargoCode type : CargoCode.values()) {
            if (type.getCode() == code)
                return type;
        }

        return null;
    }
}
