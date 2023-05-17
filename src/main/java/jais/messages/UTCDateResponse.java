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
import lombok.Getter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class UTCDateResponse extends AISMessageBase {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private boolean accurate;
    private double lon;
    private double lat;
    private EPFDFixType epfd;
    private boolean raim;
    private int radio;

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param sentences the AISSentences from which this message should be composed
     */
    public UTCDateResponse(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source The name of the source of the AISSentence(s)
     * @param type the AISMessageType of the message
     * @param sentences the AISSentences from which this message should be composed
     */
    public UTCDateResponse(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (UTCDateResponseFieldMap field : UTCDateResponseFieldMap.values()) {
            switch (field) {
                case YEAR -> {
                    if (bits.size() >= field.getStartBit())
                        this.year = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case MONTH -> {
                    if (bits.size() >= field.getStartBit())
                        this.month = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case DAY -> {
                    if (bits.size() >= field.getStartBit())
                        this.day = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case HOUR -> {
                    if (bits.size() >= field.getStartBit())
                        this.hour = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case MINUTE -> {
                    if (bits.size() >= field.getStartBit())
                        this.minute = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case SECOND -> {
                    if (bits.size() >= field.getStartBit())
                        this.second = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case ACCURACY -> {
                    if (bits.size() >= field.getStartBit())
                        this.accurate = bits.get(field.getStartBit());
                }
                case LON -> {
                    if (bits.size() >= field.getStartBit())
                        this.lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case LAT -> {
                    if (bits.size() >= field.getStartBit())
                        this.lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case EPFD -> {
                    if (bits.size() >= field.getStartBit()) {
                        int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                        this.epfd = EPFDFixType.getForCode(epfdCode);
                    }
                }
                case RAIM -> {
                    if (bits.size() >= field.getStartBit())
                        this.raim = bits.get(field.getStartBit());
                }
                case RADIO -> {
                    if (bits.size() >= field.getStartBit())
                        this.radio = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    @Getter
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

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        UTCDateResponseFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
