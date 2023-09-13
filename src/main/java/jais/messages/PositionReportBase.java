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
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public abstract class PositionReportBase extends AISMessageBase {

    // bit positions are off spec by 1 because the BitSet counts from 0 rather than
    // 1
    private NavigationStatus status = NavigationStatus.NOT_DEFINED; // bits 38-41
    private int rateOfTurn; // bits 42-49
    private float speed; // bits 50-59, represented in knots
    private boolean accuracy; // bit 60
    private double lon = -91; // bits 61-88
    private double lat = -181; // 89-115
    private float courseOverGround; // bits 116-127, 0.1 degree precision, relative to true north
    private int heading = 511; // bits 128-136, 0-359 degrees, 511 means not available
    private int second; // bits 137-142, timestamp in seconds since epoch
    private ManeuverType maneuver = ManeuverType.NOT_AVAILABLE; // bits 143-144, maneuver indicator
    // spare bits 145-147
    private boolean raim; // bit 148
    private int radio; // bits 149-167, Radio Status

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public PositionReportBase(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param messageType the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public PositionReportBase(String source, AISMessageType messageType, AISSentence... sentences) {
        super(source, messageType, sentences);
    }

    /**
     *
     * @return a boolean indicating whether or not this message type contains positional information
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return a Point representing the current location of the vessel
     */
    @Override
    public Point getPosition() {
        if (super.position == null && AISMessage.isValidPosition(this.lat, this.lon)) {
            super.position = CTX.getShapeFactory().pointLatLon(this.lat, this.lon);
        }

        return super.position;
    }

    /**
     *
     * @deprecated use the static method AISMessage.isValidPosition instead
     * @return a boolean indicating whether or not the position information is valid
     */
    @Deprecated
    public boolean isPositionValid() {
        return AISMessage.isValidPosition(this.lat, this.lon);
    }

    /**
     *
     * @deprecated use the static method AISMessage.isValidCourse instead
     * @return a boolean indicating whether or not the course information is valid
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
        return AISMessage.isValidHeading(heading);
    }

    /**
     * @deprecated use the static method AISMessage.isValidTurn instead
     * @return a boolean indicating whether or not the rate of turn information is valid
     */
    @Deprecated
    public boolean isTurnValid() {
        return AISMessage.isValidTurn(this.rateOfTurn);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (PositionFieldMap field : PositionFieldMap.values()) {
            switch (field) {
                case STATUS -> {
                    if (bits.size() >= field.getStartBit()) {
                        int nsId = AISMessageDecoder.decodeUnsignedInt(super.bits, field.getStartBit(),
                                field.getEndBit());
                        status = NavigationStatus.getForCode(nsId);
                    }
                }
                case RATE_OF_TURN -> {
                    if (bits.size() >= field.getStartBit())
                        rateOfTurn = AISMessageDecoder.decodeRateOfTurn(bits, field.getStartBit(), field.getEndBit());
                }
                case SPEED -> {
                    if (bits.size() >= field.getStartBit())
                        speed = AISMessageDecoder.decodeSpeed(bits, field.getStartBit(), field.getEndBit());
                }
                case ACCURACY -> {
                    if (bits.size() >= field.getStartBit())
                        accuracy = bits.get(field.getEndBit());
                }
                case LON -> {
                    if (bits.size() >= field.getStartBit())
                        lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case LAT -> {
                    if (bits.size() >= field.getStartBit())
                        lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case COURSE_OVER_GROUND -> {
                    if (bits.size() >= field.getStartBit())
                        courseOverGround = AISMessageDecoder.decodeCourse(bits, field.getStartBit(), field.getEndBit());
                }
                case HEADING -> {
                    if (bits.size() >= field.getStartBit())
                        heading = AISMessageDecoder.decodeHeading(bits, field.getStartBit(), field.getEndBit());
                }
                case SECOND -> {
                    if (bits.size() >= field.getStartBit())
                        second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case MANEUVER -> {
                    if (bits.size() >= field.getStartBit()) {
                        int mId = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        maneuver = ManeuverType.getForCode(mId);
                        if (maneuver == null) {
                            maneuver = ManeuverType.NOT_AVAILABLE;
                        }
                    }
                }
                case RAIM -> {
                    if (bits.size() >= field.getStartBit())
                        raim = bits.get(field.getEndBit());
                }
                case RADIO -> {
                    if (bits.size() >= field.getStartBit())
                        radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     * bit position numbers differ from the NMEA spec in that the BitSet in
     * which they are stored indexes from zero rather than one
     */
    @Getter
    protected enum PositionFieldMap implements FieldMap {
        STATUS(38, 41),
        RATE_OF_TURN(42, 49),
        SPEED(50, 59),
        ACCURACY(60, 60),
        LON(61, 88),
        LAT(89, 115),
        COURSE_OVER_GROUND(116, 127),
        HEADING(128, 136),
        SECOND(137, 142),
        MANEUVER(143, 144),
        SPARE(145, 147), // NOT used
        RAIM(148, 148),
        RADIO(149, 167);

        private final int startBit;
        private final int endBit;

        /**
         * s
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        PositionFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
