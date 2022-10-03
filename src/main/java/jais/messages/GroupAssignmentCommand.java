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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class GroupAssignmentCommand extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(GroupAssignmentCommand.class);

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
     * @param source
     * @param sentences
     */
    public GroupAssignmentCommand(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source
     * @param type
     * @param sentences
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
            switch (field) {
                case NE_LON:
                    if (bits.size() >= field.getStartBit())
                        neLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case NE_LAT:
                    if (bits.size() >= field.getStartBit())
                        neLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SW_LON:
                    if (bits.size() >= field.getStartBit())
                        swLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SW_LAT:
                    if (bits.size() >= field.getStartBit())
                        swLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case STATION_TYPE:
                    if (bits.size() >= field.getStartBit()) {
                        int stCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        stationType = StationType.getForCode(stCode);
                    }
                    break;
                case TXRX_MODE:
                    if (bits.size() >= field.getStartBit()) {
                        int txrxCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        txrx = TransmitMode.getForCode(txrxCode);
                    }
                    break;
                case REPORT_INTERVAL:
                    if (bits.size() >= field.getStartBit()) {
                        int iCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        interval = StationInterval.getForCode(iCode);
                    }
                    break;
                case QUIET_TIME:
                    if (bits.size() >= field.getStartBit())
                        quietTime = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Ignoring field: {}", field.name());
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
         * @param startBit
         * @param endBit
         */
        GroupAssignmentCommandFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
