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
public enum ManeuverType {

    NOT_AVAILABLE(0, "Not available (default)"),
    NO_SPECIAL_MANEUVER(1, "No special maneuver"),
    SPECIAL_MANEUVER(2, "Special maneuver"); // ie, regional passing arrangement

    private final int code;
    private final String description;

    /**
     * 
     * @param code
     * @param description
     */
    ManeuverType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 
     * @param code
     * @return
     */
    public static ManeuverType getForCode(int code) {
        for (ManeuverType type : ManeuverType.values()) {
            if (type.getCode() == code)
                return type;
        }

        return null;
    }

}
