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

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import org.locationtech.spatial4j.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class BaseStationReport extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(BaseStationReport.class);

    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private int _second;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private EPFDFixType _epfd;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param source  String denoting the source of the packet
     * @param packets AISPacket[] from which the message is composed
     */
    public BaseStationReport(String source, AISPacket... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source  String denoting the source of the packet
     * @param type    AISMessageType
     * @param packets AISPacket[] from which the message is composed
     */
    public BaseStationReport(String source, AISMessageType type, AISPacket... packets) {
        super(source, type, packets);
    }

    /**
     *
     * @return int
     */
    public int getYear() {
        return _year;
    }

    /**
     *
     * @return int
     */
    public int getMonth() {
        return _month;
    }

    /**
     *
     * @return int
     */
    public int getDay() {
        return _day;
    }

    /**
     *
     * @return int
     */
    public int getHour() {
        return _hour;
    }

    /**
     *
     * @return int
     */
    public int getMinute() {
        return _minute;
    }

    /**
     *
     * @return int
     */
    public int getSecond() {
        return _second;
    }

    /**
     *
     * @return boolean
     */
    public boolean isAccurate() {
        return _accurate;
    }

    /**
     *
     * @return float
     */
    public float getLon() {
        return _lon;
    }

    /**
     *
     * @return float
     */
    public float getLat() {
        return _lat;
    }

    /**
     *
     * @return EPFDFixType
     */
    public EPFDFixType getEpfd() {
        return _epfd;
    }

    /**
     *
     * @return boolean
     */
    public boolean usingRaim() {
        return _raim;
    }

    /**
     *
     * @return int
     */
    public int getRadio() {
        return _radio;
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
        if (_position == null) {
            _position = CTX.getShapeFactory().pointXY(_lon, _lat);
        }

        return _position;
    }

    /**
     * @throws AISException if decoding fails
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (BaseReportFieldMap field : BaseReportFieldMap.values()) {
            switch (field) {
                case YEAR:
                    if (_bits.size() >= field.getStartBit())
                        _year = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case MONTH:
                    if (_bits.size() >= field.getStartBit())
                        _month = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case DAY:
                    if (_bits.size() >= field.getStartBit())
                        _day = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case HOUR:
                    if (_bits.size() >= field.getStartBit())
                        _hour = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                case MINUTE:
                    if (_bits.size() >= field.getStartBit())
                        _minute = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case SECOND:
                    if (_bits.size() >= field.getStartBit())
                        _second = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case ACCURACY:
                    if (_bits.size() >= field.getStartBit())
                        _accurate = _bits.get(field.getStartBit());
                    break;
                case LON:
                    if (_bits.size() >= field.getStartBit())
                        _lon = AISMessageDecoder.decodeLongitude(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case LAT:
                    if (_bits.size() >= field.getStartBit())
                        _lat = AISMessageDecoder.decodeLatitude(_bits, field.getStartBit(), field.getEndBit());
                    break;
                case EPFD:
                    if (_bits.size() >= field.getStartBit()) {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(),
                                field.getEndBit());
                        _epfd = EPFDFixType.getForCode(epfdCode);
                    }
                    break;
                case RAIM:
                    if (_bits.size() >= field.getStartBit())
                        _raim = _bits.get(field.getStartBit());
                    break;
                case RADIO:
                    if (_bits.size() >= field.getStartBit())
                        _radio = AISMessageDecoder.decodeUnsignedInt(_bits, field.getStartBit(), field.getEndBit());
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Encountered unhandled field type of : {}",
                                field);
            }
        }
    }

    /**
     *
     */
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

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit int
         * @param endBit   int
         */
        BaseReportFieldMap(int startBit, int endBit) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return int
         */
        @Override
        public int getStartBit() {
            return _startBit;
        }

        /**
         *
         * @return int
         */
        @Override
        public int getEndBit() {
            return _endBit;
        }
    }
}
