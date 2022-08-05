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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class StandardClassBCSPositionReport extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(StandardClassBCSPositionReport.class);

    private int _speed;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private float _course;
    private int _heading;
    private int _second;
    private boolean _cs;
    private boolean _display;
    private boolean _dsc;
    private boolean _band;
    private boolean _msg22;
    private boolean _assigned;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param source
     * @param sentences
     */
    public StandardClassBCSPositionReport(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source
     * @param type
     * @param sentences
     */
    public StandardClassBCSPositionReport(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
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
    public float getLon() {
        return _lon;
    }

    /**
     *
     * @return
     */
    public float getLat() {
        return _lat;
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
            super.position = CTX.getShapeFactory().pointXY(_lon, _lat); // must be in x, y (lon, lat) order
        }

        return super.position;
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
    public int getHeading() {
        return _heading;
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
    public boolean isCs() {
        return _cs;
    }

    /**
     *
     * @return
     */
    public boolean isDisplay() {
        return _display;
    }

    /**
     *
     * @return
     */
    public boolean isDsc() {
        return _dsc;
    }

    /**
     *
     * @return
     */
    public boolean isBand() {
        return _band;
    }

    /**
     *
     * @return
     */
    public boolean isMsg22() {
        return _msg22;
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
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (StandardClassBCSPositionReportFieldMap field : StandardClassBCSPositionReportFieldMap.values()) {
            try {
                switch (field) {
                    case SPEED:
                        if (bits.size() >= field.getStartBit())
                            _speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        break;
                    case ACCURATE:
                        if (bits.size() >= field.getStartBit())
                            _accurate = bits.get(field.getStartBit());
                        break;
                    case LON:
                        if (bits.size() >= field.getStartBit())
                            _lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                        break;
                    case LAT:
                        if (bits.size() >= field.getStartBit())
                            _lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                        break;
                    case COURSE:
                        if (bits.size() >= field.getStartBit())
                            _course = AISMessageDecoder.decodeCourse(bits, field.getStartBit(), field.getEndBit());
                        break;
                    case HEADING:
                        if (bits.size() >= field.getStartBit())
                            _heading = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                        break;
                    case SECOND:
                        if (bits.size() >= field.getStartBit())
                            _second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                    field.getEndBit());
                        break;
                    case CS:
                        if (bits.size() >= field.getStartBit())
                            _cs = bits.get(field.getStartBit());
                        break;
                    case DISPLAY:
                        if (bits.size() >= field.getStartBit())
                            _display = bits.get(field.getStartBit());
                        break;
                    case DSC:
                        if (bits.size() >= field.getStartBit())
                            _dsc = bits.get(field.getStartBit());
                        break;
                    case MSG22:
                        if (bits.size() >= field.getStartBit())
                            _msg22 = bits.get(field.getStartBit());
                        break;
                    case ASSIGNED:
                        if (bits.size() >= field.getStartBit())
                            _assigned = bits.get(field.getStartBit());
                        break;
                    case RAIM:
                        if (bits.size() >= field.getStartBit())
                            _raim = bits.get(field.getStartBit());
                        break;
                    case BAND:
                        if (bits.size() >= field.getStartBit())
                            _band = bits.get(field.getStartBit());
                        break;
                    case RADIO:
                        if (bits.size() >= field.getStartBit())
                            _radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        break;
                    default:
                        if (LOG.isDebugEnabled())
                            LOG.debug("Ignoring field: {}", field.name());
                }
            } catch (AISException ae) {
                LOG.warn("Unable to decode field: {}: {}", field.name(), ae.getMessage());
                if (LOG.isDebugEnabled())
                    LOG.debug(ae.getMessage());
            }
        }
    }

    /**
     *
     */
    private enum StandardClassBCSPositionReportFieldMap implements FieldMap {

        RESERVED1(38, 45),
        SPEED(46, 55),
        ACCURATE(56, 56),
        LON(57, 84),
        LAT(85, 111),
        COURSE(112, 123),
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

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        StandardClassBCSPositionReportFieldMap(int startBit, int endBit) {
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
