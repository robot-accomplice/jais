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

package jais.messages.binarybroadcast.environmental;

import jais.messages.enums.BinaryBroadcastMessageEnvironmentalType;
import lombok.Getter;

import java.util.BitSet;

/**
 * Represents one decoded report block from an IMO289 environmental broadcast.
 */
@Getter
public class EnvironmentalSensorReport {

    private final BinaryBroadcastMessageEnvironmentalType type;
    private final int utcDay;
    private final int utcHour;
    private final int utcMinute;
    private final int siteId;
    private final BitSet sensorData;

    /**
     *
     * @param type the report type
     * @param utcDay the report UTC day
     * @param utcHour the report UTC hour
     * @param utcMinute the report UTC minute
     * @param siteId the transmitting sensor site id
     * @param sensorData the raw 85-bit sensor payload for the report
     */
    public EnvironmentalSensorReport(BinaryBroadcastMessageEnvironmentalType type,
            int utcDay, int utcHour, int utcMinute, int siteId, BitSet sensorData) {
        this.type = type;
        this.utcDay = utcDay;
        this.utcHour = utcHour;
        this.utcMinute = utcMinute;
        this.siteId = siteId;
        this.sensorData = sensorData;
    }
}
