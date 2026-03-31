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

import jais.messages.BinaryBroadcastMessage;
import jais.messages.binarybroadcast.FairwayClosed;
import jais.messages.binarybroadcast.IMO236ExtendedShipStaticAndVoyageRelatedData;
import jais.messages.binarybroadcast.IMO236MeteorologicalAndHydrologicalData;
import jais.messages.binarybroadcast.IMO289AreaNotice;
import jais.messages.binarybroadcast.IMO289Environmental;
import jais.messages.binarybroadcast.IMO289ExtendedShipStaticAndVoyageRelatedData;
import jais.messages.binarybroadcast.IMO289MarineTrafficSignal;
import jais.messages.binarybroadcast.IMO289MeteorologicalAndHydrologicalData;
import jais.messages.binarybroadcast.IMO289RouteInformation;
import jais.messages.binarybroadcast.IMO289TextDescription;
import jais.messages.binarybroadcast.IMO289WeatherObservationReportFromShip;
import jais.messages.binarybroadcast.VTSGeneratedSyntheticTargets;
import lombok.Getter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public enum BinaryBroadcastMessageType {
    UNKNOWN(-1, -1, -1, BinaryBroadcastMessage.class, "Unknown binary broadcast message"),
    FAIRWAY_CLOSED(1, 13, -1, FairwayClosed.class, "Fairway closed"),
    IMO236_EXTENDED_SHIP_STATIC_AND_VOYAGE_RELATED_DATA(1, 15, -1,
            IMO236ExtendedShipStaticAndVoyageRelatedData.class,
            "Extended ship static and voyage related data (deprecated)"),
    IMO236_METEOROLOGICAL_AND_HYDROLOGICAL_DATA(1, 11, 352,
            IMO236MeteorologicalAndHydrologicalData.class,
            "Meteorological and hydrological data (deprecated)"),
    IMO289_AREA_NOTICE(1, 22, -1, IMO289AreaNotice.class, "Area notice"),
    IMO289_ENVIRONMENTAL(1, 26, -1, IMO289Environmental.class, "Environmental"),
    IMO289_EXTENDED_SHIP_STATIC_AND_VOYAGE_RELATED_DATA(1, 24, -1,
            IMO289ExtendedShipStaticAndVoyageRelatedData.class,
            "Extended ship static and voyage related data"),
    IMO289_MARINE_TRAFFIC_SIGNAL(1, 19, -1, IMO289MarineTrafficSignal.class, "Marine traffic signal"),
    IMO289_METEOROLOGICAL_AND_HYDROLOGICAL_DATA(1, 31, -1,
            IMO289MeteorologicalAndHydrologicalData.class,
            "Meteorological and hydrological data"),
    IMO289_ROUTE_INFORMATION(1, 27, -1, IMO289RouteInformation.class, "Route information"),
    IMO289_TEXT_DESCRIPTION(1, 29, -1, IMO289TextDescription.class, "Text description"),
    IMO289_WEATHER_OBSERVATION_REPORT_FROM_SHIP(1, 33, -1,
            IMO289WeatherObservationReportFromShip.class,
            "Weather observation report from ship"),
    VTS_GENERATED_SYNTHETIC_TARGETS(1, 17, -1, VTSGeneratedSyntheticTargets.class,
            "VTS generated synthetic targets");

    private final int dac;
    private final int fid;
    private final int length;
    private final Class<? extends BinaryBroadcastMessage> msgClass;
    private final String description;

    /**
     *
     * @param dac the designated area code
     * @param fid the functional id
     * @param length the expected message length or -1 if variable
     * @param msgClass the concrete message class
     * @param description a human-readable description
     */
    BinaryBroadcastMessageType(int dac, int fid, int length,
            Class<? extends BinaryBroadcastMessage> msgClass, String description) {
        this.dac = dac;
        this.fid = fid;
        this.length = length;
        this.msgClass = msgClass;
        this.description = description;
    }

    /**
     *
     * @param dac the designated area code
     * @param fid the functional id
     * @param length the decoded message length in bits
     * @return the matching subtype or {@link #UNKNOWN} if no match can be found
     */
    public static BinaryBroadcastMessageType fetch(int dac, int fid, int length) {
        for (BinaryBroadcastMessageType type : BinaryBroadcastMessageType.values()) {
            if (type.getDac() == dac && type.getFid() == fid
                    && (type.getLength() == length || type.getLength() == -1)) {
                return type;
            }
        }

        return UNKNOWN;
    }
}
