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
    private float lon;
    private float lat;
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
     * @return a boolean indicating whether or not this message has position data
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
     * 
     * @return a boolean indicating whether or not the positional information is valid
     */
    public boolean isPositionValid() {
        return ((lon >= -180 && lon <= 180) && (lat >= -90 && lat <= 90));
    }

    /**
     * 
     * @return a boolean indicating whether or not the course information is valid
     */
    public boolean isCourseValid() {
        return courseOverGround < 3600;
    }

    /**
     * 
     * @return a boolean indicating whether or not the speed information is valid
     */
    public boolean isSpeedValid() {
        return speed < 1023;
    }

    /**
     * 
     * @return a boolean indicating whether or not the heading information is valid
     */
    public boolean isHeadingValid() {
        return heading < 360;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (StandardClassBCSPositionReportFieldMap field : StandardClassBCSPositionReportFieldMap.values()) {
            switch (field) {
                case SPEED:
                    if (bits.size() >= field.getStartBit())
                        this.speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case ACCURACY:
                    if (bits.size() >= field.getStartBit())
                        this.accuracy = bits.get(field.getStartBit());
                    break;
                case LON:
                    if (bits.size() >= field.getStartBit())
                        this.lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case LAT:
                    if (bits.size() >= field.getStartBit())
                        this.lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case COURSE_OVER_GROUND:
                    if (bits.size() >= field.getStartBit())
                        this.courseOverGround = AISMessageDecoder.decodeCourse(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case HEADING:
                    if (bits.size() >= field.getStartBit())
                        this.heading = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case SECOND:
                    if (bits.size() >= field.getStartBit())
                        this.second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case CS:
                    if (bits.size() >= field.getStartBit())
                        this.cs = bits.get(field.getStartBit());
                    break;
                case DISPLAY:
                    if (bits.size() >= field.getStartBit())
                        this.display = bits.get(field.getStartBit());
                    break;
                case DSC:
                    if (bits.size() >= field.getStartBit())
                        this.dsc = bits.get(field.getStartBit());
                    break;
                case MSG22:
                    if (bits.size() >= field.getStartBit())
                        this.msg22 = bits.get(field.getStartBit());
                    break;
                case ASSIGNED:
                    if (bits.size() >= field.getStartBit())
                        this.assigned = bits.get(field.getStartBit());
                    break;
                case RAIM:
                    if (bits.size() >= field.getStartBit())
                        this.raim = bits.get(field.getStartBit());
                    break;
                case BAND:
                    if (bits.size() >= field.getStartBit())
                        this.band = bits.get(field.getStartBit());
                    break;
                case RADIO:
                    if (bits.size() >= field.getStartBit())
                        this.radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                default:
                    // ignore field
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
