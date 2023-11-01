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
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class BaseStationReport extends AISMessageBase {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private boolean accurate;
    private double lon;
    private double lat;
    private EPFDFixType epfd;
    private boolean raim;
    private int radio;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public BaseStationReport(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public BaseStationReport(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     *
     * @return boolean
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return Point
     */
    @Override
    public Point getPosition() {
        if (position == null) {
            position = CTX.getShapeFactory().pointXY(lon, lat);
        }

        return position;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (BaseReportFieldMap field : BaseReportFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case YEAR -> year = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MONTH -> month = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DAY -> day = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case HOUR -> hour = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case MINUTE -> minute = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case SECOND -> second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case ACCURACY -> accurate = bits.get(field.getStartBit());
                    case LON -> lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    case LAT -> lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    case EPFD -> {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        epfd = EPFDFixType.getForCode(epfdCode);
                    }
                    case RAIM -> raim = bits.get(field.getStartBit());
                    case RADIO -> radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum BaseReportFieldMap implements FieldMap {

        YEAR(38, 51),
        MONTH(52, 55),
        DAY(56, 60),
        HOUR(61, 65),
        MINUTE(66, 71),
        SECOND(72, 77),
        ACCURACY(78, 78),
        LON(79, 106),
        LAT(107, 133),
        EPFD(134, 137),
        SPARE(138, 147),
        RAIM(148, 148),
        RADIO(149, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        BaseReportFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
