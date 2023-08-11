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
import jais.messages.enums.ShipType;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class ExtendedClassBCSPositionReport extends AISMessageBase {

    private int speed;
    private boolean accurate;
    private double lon;
    private double lat;
    private float courseOverGround;
    private int heading;
    private int second;
    private String shipName;
    private ShipType shipType;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private EPFDFixType epfd;
    private boolean raim;
    private boolean dte;
    private int assigned;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public ExtendedClassBCSPositionReport(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     * 
     * @param source the name of the source of this message
     * @param type the specific type of AISMessage
     * @param sentences the AIS sentences from which this message was composed
     */
    public ExtendedClassBCSPositionReport(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     * 
     * @return a boolean representing whether or not this message contains position information
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     * 
     * @return a geometric point representing the current location of the sender
     */
    @Override
    public Point getPosition() {
        if (super.position == null)
            super.position = CTX.getShapeFactory().pointXY(lon, lat);

        return super.position;
    }

    /**
     * 
     * @return boolean representing whether or not the position is valid
     */
    public boolean isPositionValid() {
        return ((lon >= -180 && lon <= 180) && (lat >= -90 && lat <= 90));
    }

    /**
     * 
     * @return boolean representing whether or not the course is valid
     */
    public boolean isCourseValid() {
        return courseOverGround < 3600;
    }

    /**
     * 
     * @return boolean representing whether or not the speed is valid
     */
    public boolean isSpeedValid() {
        return speed < 1023;
    }

    /**
     * 
     * @return boolean representing whether or not the heading is valid
     */
    public boolean isHeadingValid() {
        return heading < 360;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (ExtendedClassBCSPositionReportFieldMap field : ExtendedClassBCSPositionReportFieldMap.values()) {
            switch (field) {
                case SPEED -> {
                    if (bits.size() >= field.getStartBit())
                        speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case ACCURATE -> {
                    if (bits.size() >= field.getStartBit())
                        accurate = bits.get(field.getStartBit());
                }
                case LON -> {
                    if (bits.size() >= field.getStartBit())
                        lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case LAT -> {
                    if (bits.size() >= field.getStartBit())
                        lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case COURSE -> {
                    if (bits.size() >= field.getStartBit())
                        courseOverGround = AISMessageDecoder.decodeCourse(bits, field.getStartBit(), field.getEndBit());
                }
                case HEADING -> {
                    if (bits.size() >= field.getStartBit())
                        heading = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case SECOND -> {
                    if (bits.size() >= field.getStartBit())
                        second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case SHIP_NAME -> {
                    if (bits.size() >= field.getStartBit())
                        shipName = AISMessageDecoder.decodeToString(bits, field.getStartBit(), field.getEndBit());
                }
                case SHIP_TYPE -> {
                    if (bits.size() >= field.getStartBit()) {
                        int shipCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        shipType = ShipType.getForCode(shipCode);
                    }
                }
                case TO_BOW -> {
                    if (bits.size() >= field.getStartBit())
                        toBow = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_STERN -> {
                    if (bits.size() >= field.getStartBit())
                        toStern = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_PORT -> {
                    if (bits.size() >= field.getStartBit())
                        toPort = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case TO_STARBOARD -> {
                    if (bits.size() >= field.getStartBit())
                        toStarboard = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case EPFD -> {
                    if (bits.size() >= field.getStartBit()) {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        epfd = EPFDFixType.getForCode(epfdCode);
                    }
                }
                case RAIM -> {
                    if (bits.size() >= field.getStartBit())
                        raim = bits.get(field.getStartBit());
                }
                case DTE -> {
                    if (bits.size() >= field.getStartBit())
                        dte = bits.get(field.getStartBit());
                }
                case ASSIGNED -> {
                    if (bits.size() >= field.getStartBit())
                        assigned = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                default -> {
                }
                // ignore field
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum ExtendedClassBCSPositionReportFieldMap implements FieldMap {

        RESERVED(38, 45),
        SPEED(46, 55),
        ACCURATE(56, 56),
        LON(57, 84),
        LAT(85, 111),
        COURSE(112, 123),
        HEADING(124, 132),
        SECOND(133, 138),
        RESERVED2(139, 142),
        SHIP_NAME(143, 262),
        SHIP_TYPE(263, 270),
        TO_BOW(271, 279),
        TO_STERN(280, 288),
        TO_PORT(289, 294),
        TO_STARBOARD(295, 300),
        EPFD(301, 304),
        RAIM(305, 305),
        DTE(306, 306),
        ASSIGNED(307, 307),
        SPARE(308, 311);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        ExtendedClassBCSPositionReportFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
