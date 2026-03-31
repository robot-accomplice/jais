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

package jais.messages.binarybroadcast;

import jais.AISSentence;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryBroadcastMessage;
import jais.messages.binarybroadcast.environmental.EnvironmentalSensorReport;
import jais.messages.enums.BinaryBroadcastMessageEnvironmentalType;
import jais.messages.enums.BinaryBroadcastMessageType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289Environmental extends BinaryBroadcastMessage {

    private static final int REPORT_SIZE_BITS = 112;

    private final List<EnvironmentalSensorReport> reports = new ArrayList<>();

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this sentence was composed
     */
    public IMO289Environmental(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     * 
     * @return
     */
    @Override
    public BinaryBroadcastMessageType getSubType() {
        return BinaryBroadcastMessageType.IMO289_ENVIRONMENTAL;
    }

    /**
     *
     */
    @Override
    public void decode() {
        reports.clear();
        BitSet data = getData();
        if (data == null) {
            super.decode();
            data = getData();
        }
        if (data == null) {
            return;
        }

        final int reportCount = data.length() / REPORT_SIZE_BITS;
        for (int offset = 0; offset < (reportCount * REPORT_SIZE_BITS); offset += REPORT_SIZE_BITS) {
            BitSet reportBits = data.get(offset, offset + REPORT_SIZE_BITS);
            int reportType = AISMessageDecoder.decodeUnsignedInt(reportBits, 0, 3);
            int utcDay = AISMessageDecoder.decodeUnsignedInt(reportBits, 4, 8);
            int utcHour = AISMessageDecoder.decodeUnsignedInt(reportBits, 9, 13);
            int utcMinute = AISMessageDecoder.decodeUnsignedInt(reportBits, 14, 19);
            int siteId = AISMessageDecoder.decodeUnsignedInt(reportBits, 20, 26);
            BitSet sensorData = reportBits.get(27, REPORT_SIZE_BITS);

            reports.add(new EnvironmentalSensorReport(
                    BinaryBroadcastMessageEnvironmentalType.getForCode(reportType),
                    utcDay,
                    utcHour,
                    utcMinute,
                    siteId,
                    sensorData));
        }
    }

    /**
     *
     * @return an immutable view of the decoded environmental sensor reports
     */
    public List<EnvironmentalSensorReport> getReports() {
        return Collections.unmodifiableList(reports);
    }
}
