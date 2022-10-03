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

import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import jais.AISSentence;
import jais.messages.enums.EPFDFixType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class UTCDateResponse extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(UTCDateResponse.class);

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
     * @param source
     * @param packets
     */
    public UTCDateResponse(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public UTCDateResponse(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     *
     * @return
     */
    public int getYear() {
        return _year;
    }

    /**
     *
     * @return
     */
    public int getMonth() {
        return _month;
    }

    /**
     *
     * @return
     */
    public int getDay() {
        return _day;
    }

    /**
     *
     * /**
     *
     * @return
     */
    public int getHour() {
        return _hour;
    }

    /**
     *
     * @return
     */
    public int getMinute() {
        return _minute;
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
    public EPFDFixType getEpfd() {
        return _epfd;
    }

    /**
     *
     * @return
     */
    public boolean usingRaim() {
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
     */
    @Override
    public final void decode() {
        super.decode();

        for (UTCDateResponseFieldMap field : UTCDateResponseFieldMap.values()) {
            switch (field) {
                case YEAR:
                    if (bits.size() >= field.getStartBit())
                        _year = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MONTH:
                    if (bits.size() >= field.getStartBit())
                        _month = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case DAY:
                    if (bits.size() >= field.getStartBit())
                        _day = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case HOUR:
                    if (bits.size() >= field.getStartBit())
                        _hour = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case MINUTE:
                    if (bits.size() >= field.getStartBit())
                        _minute = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SECOND:
                    if (bits.size() >= field.getStartBit())
                        _second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ACCURACY:
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
                case EPFD:
                    if (bits.size() >= field.getStartBit()) {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        if (LOG.isDebugEnabled())
                            LOG.debug("Retrieving EPFDFixType for code: {}", epfdCode);
                        _epfd = EPFDFixType.getForCode(epfdCode);
                    }
                    break;
                case RAIM:
                    if (bits.size() >= field.getStartBit())
                        _raim = bits.get(field.getStartBit());
                    break;
                case RADIO:
                    if (bits.size() >= field.getStartBit())
                        _radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Encountered unhandled field type of : {}", field);
            }
        }
    }

    /**
     *
     */
    private enum UTCDateResponseFieldMap implements FieldMap {

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
         * @param startBit
         * @param endBit
         */
        UTCDateResponseFieldMap(int startBit, int endBit) {
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
