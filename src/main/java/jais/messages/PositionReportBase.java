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
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public abstract class PositionReportBase extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(PositionReportBase.class);

    // bit positions are off spec by 1 because the BitSet counts from 0 rather than
    // 1
    private NavigationStatus status = NavigationStatus.NOT_DEFINED; // bits 38-41
    private float turn; // bits 42-49
    private float speed; // bits 50-59, represented in knots
    private boolean accurate; // bit 60
    private float lon; // bits 61-88
    private float lat; // 89-115
    private float course; // bits 116-127, 0.1 degree precision, relative to true north
    private int heading = 511; // bits 128-136, 0-359 degrees, 511 means not available
    private int second; // bits 137-142, timestamp in seconds since epoch
    private ManeuverType maneuver = ManeuverType.NOT_AVAILABLE; // bits 143-144, maneuver indicator
    // spare bits 145-147
    private boolean raim; // bit 148
    private int radio; // bits 149-167, Radio Status

    /**
     * 
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public PositionReportBase(String source, AISSentence... packets) throws AISException {
        super(source, packets);
    }

    /**
     *
     * @param source
     * @param messageType
     * @param packets
     */
    public PositionReportBase(String source, AISMessageType messageType, AISSentence... packets) {
        super(source, messageType, packets);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public Point getPosition() {
        if (super.position == null) {
            super.position = CTX.getShapeFactory().pointXY(lon, lat); // must be in x, y (lon, lat) order
        }

        return super.position;
    }

    /**
     * 
     * @return
     */
    public boolean isPositionValid() {
        return (lon >= -90 && lon <= 90 && lat >= -180 && lat <= 180);
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (PositionFieldMap field : PositionFieldMap.values()) {
            switch (field) {
                case STATUS:
                    if (bits.size() >= field.getStartBit()) {
                        int nsId = AISMessageDecoder.decodeUnsignedInt(super.bits, field.getStartBit(),
                                field.getEndBit());
                        status = NavigationStatus.getForCode(nsId);
                    }
                    break;
                case TURN:
                    if (bits.size() >= field.getStartBit())
                        turn = AISMessageDecoder.decodeTurn(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SPEED:
                    if (bits.size() >= field.getStartBit())
                        speed = AISMessageDecoder.decodeSpeed(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ACCURACY:
                    if (bits.size() >= field.getStartBit())
                        accurate = bits.get(field.getEndBit());
                    break;
                case LON:
                    if (bits.size() >= field.getStartBit())
                        lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case LAT:
                    if (bits.size() >= field.getStartBit())
                        lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case COURSE:
                    if (bits.size() >= field.getStartBit())
                        course = AISMessageDecoder.decodeCourse(bits, field.getStartBit(), field.getEndBit());
                    break;
                case HEADING:
                    if (bits.size() >= field.getStartBit())
                        heading = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SECOND:
                    if (bits.size() >= field.getStartBit())
                        second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MANEUVER:
                    if (bits.size() >= field.getStartBit()) {
                        int mId = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        maneuver = ManeuverType.getForCode(mId);
                        if (maneuver == null) {
                            maneuver = ManeuverType.NOT_AVAILABLE;
                        }
                    }
                    break;
                case RAIM:
                    if (bits.size() >= field.getStartBit())
                        raim = bits.get(field.getEndBit());
                    break;
                case RADIO:
                    if (bits.size() >= field.getStartBit())
                        radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Encountered unhandled field type of : {}", field);
                    break;
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
        TURN(42, 49),
        SPEED(50, 59),
        ACCURACY(60, 60),
        LON(61, 88),
        LAT(89, 115),
        COURSE(116, 127),
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
         * @param startBit
         * @param endBit
         */
        PositionFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
