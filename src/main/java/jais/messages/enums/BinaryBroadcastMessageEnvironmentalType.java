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
public enum BinaryBroadcastMessageEnvironmentalType {
    SITE_LOCATION(0, "Site location"),
    STATION_ID(1, "Station ID"),
    WIND(2, "Wind"),
    WATER_LEVEL(3, "Water level"),
    CURRENT_FLOW_2D(4, "Current flow (2D)"),
    CURRENT_FLOW_3D(5, "Current flow (3D)"),
    HORIZONTAL_CURRENT_FLOW(6, "Horizontal current flow"),
    SEA_STATE(7, "Sea state"),
    SALINITY(8, "Salinity"),
    WEATHER(9, "Weather"),
    AIR_GAP_OR_AIR_DRAFT(10, "Air gap / Air draft"),
    RESERVED(15, "Reserved");

    private final int code;
    private final String description;

    /**
     *
     * @param code the report type code
     * @param description the report type description
     */
    BinaryBroadcastMessageEnvironmentalType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     *
     * @param code the environmental report type code
     * @return the matching type, or {@link #RESERVED} for unknown values
     */
    public static BinaryBroadcastMessageEnvironmentalType getForCode(int code) {
        for (BinaryBroadcastMessageEnvironmentalType type : BinaryBroadcastMessageEnvironmentalType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }

        return RESERVED;
    }
}
