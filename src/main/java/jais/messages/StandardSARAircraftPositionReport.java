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
import org.locationtech.spatial4j.shape.Point;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class StandardSARAircraftPositionReport extends AISMessageBase {

    private int _alt;
    private int _speed;
    private boolean _accurate;
    private int _lon;
    private int _lat;
    private float _course;
    private int _second;
    private boolean _dte;
    private boolean _assigned;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param source
     * @param sentences
     * @throws jais.exceptions.AISException
     */
    public StandardSARAircraftPositionReport(String source, AISSentence... sentences)
            throws AISException {
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
            super.position = CTX.getShapeFactory().pointXY(_lat, _lon);
        }

        return super.position;
    }

    /**
     *
     * @return
     */
    public int getAlt() {
        return _alt;
    }

    /**
     *
     * @return
     */
    public int getSpeed() {
        return _speed;
    }

    /**
     *
     * @return
     */
    public boolean isAccurate() {
        return _accurate;
    }

    /**
     *
     * @return
     */
    public int getLon() {
        return _lon;
    }

    /**
     *
     * @return
     */
    public int getLat() {
        return _lat;
    }

    /**
     *
     * @return
     */
    public float getCourse() {
        return _course;
    }

    /**
     *
     * @return
     */
    public int getSecond() {
        return _second;
    }

    /**
     *
     * @return
     */
    public boolean isDte() {
        return _dte;
    }

    /**
     *
     * @return
     */
    public boolean isAssigned() {
        return _assigned;
    }

    /**
     *
     * @return
     */
    public boolean isRaim() {
        return _raim;
    }

    /**
     *
     * @return
     */
    public int getRadio() {
        return _radio;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (SSARAircraftPositionReportFieldMap field : SSARAircraftPositionReportFieldMap.values()) {
            switch (field) {
                case ALT:
                    if (bits.size() >= field.getStartBit())
                        _alt = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SPEED:
                    if (bits.size() >= field.getStartBit())
                        _speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ACCURACY:
                    if (bits.size() >= field.getStartBit())
                        _accurate = bits.get(field.getStartBit());
                    break;
                case LON:
                    if (bits.size() >= field.getStartBit())
                        _lon = AISMessageDecoder.decodeSignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case LAT:
                    if (bits.size() >= field.getStartBit())
                        _lat = AISMessageDecoder.decodeSignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case COURSE:
                    if (bits.size() >= field.getStartBit())
                        _course = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SECOND:
                    if (bits.size() >= field.getStartBit())
                        _second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case DTE:
                    if (bits.size() >= field.getStartBit())
                        _dte = bits.get(field.getStartBit());
                    break;
                case ASSIGNED:
                    if (bits.size() >= field.getStartBit())
                        _assigned = bits.get(field.getStartBit());
                    break;
                case RAIM:
                    if (bits.size() >= field.getStartBit())
                        _raim = bits.get(field.getStartBit());
                    break;
                case RADIO:
                    if (bits.size() >= field.getStartBit())
                        _radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case REGIONAL:
                    break;
                case SPARE:
                    break;
            }
        }
    }

    /**
     *
     */
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

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit   '
         */
        SSARAircraftPositionReportFieldMap(int startBit, int endBit) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getStartBit() {
            return _startBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getEndBit() {
            return _endBit;
        }
    }
}
