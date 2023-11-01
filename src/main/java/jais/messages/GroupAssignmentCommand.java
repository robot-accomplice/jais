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

package jais.messages;

import jais.AISSentence;
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.ShipType;
import jais.messages.enums.StationInterval;
import jais.messages.enums.StationType;
import jais.messages.enums.TransmitMode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class GroupAssignmentCommand extends AISMessageBase {

    private float neLon;
    private float neLat;
    private float swLon;
    private float swLat;
    private StationType stationType;
    private ShipType shipType;
    private TransmitMode txrx;
    private StationInterval interval;
    private int quietTime;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public GroupAssignmentCommand(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public GroupAssignmentCommand(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (GroupAssignmentCommandFieldMap field : GroupAssignmentCommandFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case NE_LON -> neLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    case NE_LAT -> neLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    case SW_LON -> swLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    case SW_LAT -> swLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    case STATION_TYPE -> {
                        int stCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        stationType = StationType.getForCode(stCode);
                    }
                    case TXRX_MODE -> {
                        int txrxCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                        txrx = TransmitMode.getForCode(txrxCode);
                    }
                    case REPORT_INTERVAL -> {
                        int iCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        interval = StationInterval.getForCode(iCode);
                    }
                    case QUIET_TIME -> quietTime = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum GroupAssignmentCommandFieldMap implements FieldMap {

        SPARE(38, 39),
        NE_LON(40, 57),
        NE_LAT(58, 74),
        SW_LON(75, 92),
        SW_LAT(93, 109),
        STATION_TYPE(110, 113),
        SHIP_TYPE(114, 121),
        SPARE2(122, 143),
        TXRX_MODE(144, 145),
        REPORT_INTERVAL(146, 149),
        QUIET_TIME(150, 153),
        SPARE3(154, 159);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        GroupAssignmentCommandFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
