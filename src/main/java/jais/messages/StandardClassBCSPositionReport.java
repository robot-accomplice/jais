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
import jais.messages.enums.FieldMap;
import org.locationtech.spatial4j.shape.Point;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class StandardClassBCSPositionReport extends AISMessageBase {

    private int speed;
    private boolean accuracy;
    private double lon;
    private double lat;
    private float courseOverGround;
    private int heading;
    private int second;
    private boolean cs;
    private boolean display;
    private boolean dsc;
    private boolean band;
    private boolean msg22;
    private boolean assigned;
    private boolean raim;
    private int radio;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public StandardClassBCSPositionReport(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public StandardClassBCSPositionReport(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     *
     * @return a boolean indicating whether this message has position data
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return a Point representing the position of the vessel
     */
    @Override
    public Point getPosition() {
        if (super.position == null) {
            super.position = CTX.getShapeFactory().pointXY(this.lon, this.lat); // must be in x, y (lon, lat) order
        }

        return super.position;
    }

    /**
     * @deprecated use the static method AISMessage.isValidPosition instead
     * @return a boolean indicating whether the positional information is valid
     */
    @Deprecated
    public boolean isPositionValid() {
        return AISMessage.isValidPosition(this.lat, this.lon);
    }

    /**
     * @deprecated use the static method AISMessage.isValidCourse instead
     * @return a boolean indicating whether the course information is valid
     */
    @Deprecated
    public boolean isCourseValid() {
        return AISMessage.isValidCourse(this.courseOverGround);
    }

    /**
     * @deprecated use the static method AISMessage.isValidSpeed instead
     * @return a boolean indicating whether or not the speed information is valid
     */
    @Deprecated
    public boolean isSpeedValid() {
        return AISMessage.isValidSpeed(this.speed);
    }

    /**
     * @deprecated use the static method AISMessage.isValidHeading instead
     * @return a boolean indicating whether or not the heading information is valid
     */
    @Deprecated
    public boolean isHeadingValid() {
        return AISMessage.isValidHeading(this.heading);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (StandardClassBCSPositionReportFieldMap field : StandardClassBCSPositionReportFieldMap.values()) {
            switch (field) {
                case SPEED -> {
                    if (bits.size() >= field.getStartBit())
                        this.speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case ACCURACY -> {
                    if (bits.size() >= field.getStartBit())
                        this.accuracy = bits.get(field.getStartBit());
                }
                case LON -> {
                    if (bits.size() >= field.getStartBit())
                        this.lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case LAT -> {
                    if (bits.size() >= field.getStartBit())
                        this.lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case COURSE_OVER_GROUND -> {
                    if (bits.size() >= field.getStartBit())
                        this.courseOverGround = AISMessageDecoder.decodeCourse(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case HEADING -> {
                    if (bits.size() >= field.getStartBit())
                        this.heading = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case SECOND -> {
                    if (bits.size() >= field.getStartBit())
                        this.second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
                case CS -> {
                    if (bits.size() >= field.getStartBit())
                        this.cs = bits.get(field.getStartBit());
                }
                case DISPLAY -> {
                    if (bits.size() >= field.getStartBit())
                        this.display = bits.get(field.getStartBit());
                }
                case DSC -> {
                    if (bits.size() >= field.getStartBit())
                        this.dsc = bits.get(field.getStartBit());
                }
                case MSG22 -> {
                    if (bits.size() >= field.getStartBit())
                        this.msg22 = bits.get(field.getStartBit());
                }
                case ASSIGNED -> {
                    if (bits.size() >= field.getStartBit())
                        this.assigned = bits.get(field.getStartBit());
                }
                case RAIM -> {
                    if (bits.size() >= field.getStartBit())
                        this.raim = bits.get(field.getStartBit());
                }
                case BAND -> {
                    if (bits.size() >= field.getStartBit())
                        this.band = bits.get(field.getStartBit());
                }
                case RADIO -> {
                    if (bits.size() >= field.getStartBit())
                        this.radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                }
            }

        }
    }

    /**
     *
     */
    @Getter
    private enum StandardClassBCSPositionReportFieldMap implements FieldMap {

        RESERVED1(38, 45),
        SPEED(46, 55),
        ACCURACY(56, 56),
        LON(57, 84),
        LAT(85, 111),
        COURSE_OVER_GROUND(112, 123),
        HEADING(124, 132),
        SECOND(133, 138),
        RESERVED2(139, 140),
        CS(141, 141),
        DISPLAY(142, 142),
        DSC(143, 143),
        BAND(144, 144),
        MSG22(145, 145),
        ASSIGNED(146, 146),
        RAIM(147, 147),
        RADIO(148, 167);

        private final int startBit;
        private final int endBit;

        /**
         * @param startBit the index of the first bit to include in the decoding of this field
         * @param endBit   the index of the last bit to include in the decoding of this field
         */
        StandardClassBCSPositionReportFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
