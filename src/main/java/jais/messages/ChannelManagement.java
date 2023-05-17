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
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class ChannelManagement extends AISMessageBase {

    private int channelA;
    private int channelB;
    private int txrx;
    private boolean highPower;
    private float neLon;
    private float neLat;
    private float swLon;
    private float swLat;
    private int destMmsi1;
    private int destMmsi2;
    private boolean addressed;
    private boolean bandA;
    private boolean bandB;
    private int zoneSize;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public ChannelManagement(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source the name of the source of this message
     * @param type the type of AISMessage
     * @param sentences the AIS sentences from which this message was composed
     */
    public ChannelManagement(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (ChannelManagementFieldMap field : ChannelManagementFieldMap.values()) {
            switch (field) {
                case NE_LON -> {
                    if (bits.size() >= field.getStartBit())
                        this.neLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case NE_LAT -> {
                    if (bits.size() >= field.getStartBit())
                        this.neLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case SW_LON -> {
                    if (bits.size() >= field.getStartBit())
                        this.swLon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                }
                case SW_LAT -> {
                    if (bits.size() >= field.getStartBit())
                        this.swLat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                }
                case DEST_MMSI1 -> {
                    if (bits.size() >= field.getStartBit())
                        this.destMmsi1 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case DEST_MMSI2 -> {
                    if (bits.size() >= field.getStartBit())
                        this.destMmsi2 = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
                case ADDRESSED -> {
                    if (bits.size() >= field.getStartBit())
                        this.addressed = bits.get(field.getStartBit());
                }
                case CHANNEL_A_BAND -> {
                    if (bits.size() >= field.getStartBit())
                        this.bandA = bits.get(field.getStartBit());
                }
                case CHANNEL_B_BAND -> {
                    if (bits.size() >= field.getStartBit())
                        this.bandB = bits.get(field.getEndBit());
                }
                case ZONE_SIZE -> {
                    if (bits.size() >= field.getStartBit())
                        this.zoneSize = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                }
            }
        }
    }

    /**
     *
     */
    private enum ChannelManagementFieldMap implements FieldMap {

        SPARE(38, 39),
        CHANNEL_A(40, 51),
        CHANNEL_B(52, 63),
        TXRX_MODE(64, 67),
        HIGH_POWER(68, 68),
        NE_LON(69, 86),
        NE_LAT(87, 103),
        SW_LON(104, 121),
        SW_LAT(122, 138),
        DEST_MMSI1(69, 98),
        DEST_MMSI2(104, 133),
        ADDRESSED(139, 139),
        CHANNEL_A_BAND(140, 140),
        CHANNEL_B_BAND(141, 141),
        ZONE_SIZE(142, 144),
        SPARE2(145, 167);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        ChannelManagementFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }

        /**
         *
         * @return the start bit index
         */
        @Override
        public int getStartBit() {
            return this.startBit;
        }

        /**
         *
         * @return the end bit index
         */
        @Override
        public int getEndBit() {
            return this.endBit;
        }
    }
}
