/*
 * Copyright 2016-2019 Jonathan Machen @literal{<jonathan.machen@robotaccomplice.com>}.
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
import jais.messages.enums.NavaidType;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class AidToNavigationReport extends AISMessageBase {

    private NavaidType navaidType;
    private String name;
    private boolean accurate;
    private double lon;
    private double lat;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private EPFDFixType epfd;
    private int second;
    private boolean offPosition;
    private int regional;
    private boolean raim;
    private boolean virtualAid;
    private boolean assigned;
    private String nameExtension;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public AidToNavigationReport(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public AidToNavigationReport(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
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
     *
     * @return boolean
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (AidToNavigationReportFieldMap field : AidToNavigationReportFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {
                switch (field) {
                    case NAVAID_TYPE -> {
                        int navCode = AISMessageDecoder.decodeSignedInt(bits, field.getStartBit(), field.getEndBit());
                        navaidType = NavaidType.getForCode(navCode);
                    }
                    case NAME -> name = AISMessageDecoder.decodeString(bits, field.getStartBit(), field.getEndBit());
                    case ACCURATE -> accurate = bits.get(field.getStartBit());
                    case LON -> lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    case LAT -> lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    case TO_BOW -> toBow = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_STERN -> toStern = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_PORT -> toPort = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case TO_STARBOARD -> toStarboard = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                    case EPFD -> {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        epfd = EPFDFixType.getForCode(epfdCode);
                    }
                    case SECOND -> second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case OFF_POSITION -> offPosition = bits.get(field.getStartBit());
                    case REGIONAL_RESERVED -> regional = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case RAIM -> raim = bits.get(field.getStartBit());
                    case VIRTUAL_AID -> virtualAid = bits.get(field.getStartBit());
                    case NAME_EXTENSION -> nameExtension = AISMessageDecoder.decodeString(bits, field.getStartBit(), bits.size() - 1);
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum AidToNavigationReportFieldMap implements FieldMap {

        NAVAID_TYPE(38, 42),
        NAME(43, 162),
        ACCURATE(163, 163),
        LON(164, 191),
        LAT(192, 218),
        TO_BOW(219, 227),
        TO_STERN(228, 236),
        TO_PORT(237, 242),
        TO_STARBOARD(243, 248),
        EPFD(249, 252),
        SECOND(253, 258),
        OFF_POSITION(259, 259),
        REGIONAL_RESERVED(260, 267),
        RAIM(268, 268),
        VIRTUAL_AID(269, 269),
        ASSIGNED(270, 270),
        SPARE(271, 271),
        NAME_EXTENSION(272, 360);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        AidToNavigationReportFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        @Override
        public int getStartBit() {
            return startBit;
        }

        @Override
        public int getEndBit() {
            return endBit;
        }
    }
}
