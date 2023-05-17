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
public enum BinaryBroadcastMessageType {
    UNKNOWN(),
    FAIRWAY_CLOSED(),
    IMO236_EXTENDED_SHIP_STATIC_AND_VOYAGE_RELATED_DATA(),
    IMO236_METEOROLOGICAL_AND_HYDROLOGICAL_DATA(),
    IMO289_AREA_NOTICE(),
    IMO289_ENVIRONMENTAL(),
    IMO289_EXTENDED_SHIP_STATIC_AND_VOYAGE_RELATED_DATA(),
    IMO289_MARINE_TRAFFIC_SIGNAL(),
    IMO289_METEOROLOGICAL_AND_HYDROLOGICAL_DATA(),
    IMO289_ROUTE_INFORMATION(),
    IMO289_TEXT_DESCRIPTION(),
    IMO289_WEATHER_OBSERVATION_REPORT_FROM_SHIP(),
    VTS_GENERATED_SYNTHETIC_TARGETS()
}
