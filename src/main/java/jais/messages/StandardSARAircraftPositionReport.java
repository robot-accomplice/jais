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
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class StandardSARAircraftPositionReport extends AISMessageBase {

    private int alt;
    private int speed;
    private boolean accurate;
    private int lon;
    private int lat;
    private float course;
    private int second;
    private boolean dte;
    private boolean assigned;
    private boolean raim;
    private int radio;

    /**
     *
     * @param source
     * @param sentences
     */
    public StandardSARAircraftPositionReport(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source
     * @param messageType
     * @param sentences
     */
    public StandardSARAircraftPositionReport(String source, AISMessageType messageType,
            AISSentence... sentences) {
        super(source, messageType, sentences);
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
            super.position = CTX.getShapeFactory().pointXY(this.lat, this.lon);
        }

        return super.position;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (SSARAircraftPositionReportFieldMap field : SSARAircraftPositionReportFieldMap.values()) {
            if (bits.size() > field.getEndBit()) {

                switch (field) {
                    case ALT -> this.alt = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case SPEED -> this.speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case ACCURACY -> this.accurate = bits.get(field.getStartBit());
                    case LON -> this.lon = AISMessageDecoder.decodeSignedInt(bits, field.getStartBit(), field.getEndBit());
                    case LAT -> this.lat = AISMessageDecoder.decodeSignedInt(bits, field.getStartBit(), field.getEndBit());
                    case COURSE -> this.course = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case SECOND -> this.second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case DTE -> this.dte = bits.get(field.getStartBit());
                    case ASSIGNED -> this.assigned = bits.get(field.getStartBit());
                    case RAIM -> this.raim = bits.get(field.getStartBit());
                    case RADIO -> this.radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    case REGIONAL, SPARE -> {}
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum SSARAircraftPositionReportFieldMap implements FieldMap {

        ALT(38, 49),
        SPEED(50, 59),
        ACCURACY(60, 60),
        LON(61, 88),
        LAT(89, 115),
        COURSE(116, 127),
        SECOND(128, 133),
        REGIONAL(134, 141), // reserved
        DTE(142, 142),
        SPARE(143, 145),
        ASSIGNED(146, 146),
        RAIM(147, 147),
        RADIO(148, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit   '
         */
        SSARAircraftPositionReportFieldMap(int startBit, int endBit) {
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
