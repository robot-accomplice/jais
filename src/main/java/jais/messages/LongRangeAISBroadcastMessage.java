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
public class LongRangeAISBroadcastMessage extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(LongRangeAISBroadcastMessage.class);

    private boolean accurate;
    private boolean raim;
    private NavigationStatus navStatus;
    private float lon;
    private float lat;
    private int speed;
    private int course;
    private boolean gnss;

    /**
     *
     * @param source
     * @param sentences
     */
    public LongRangeAISBroadcastMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     * 
     * @param source
     * @param type
     * @param sentences
     */
    public LongRangeAISBroadcastMessage(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
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
            super.position = CTX.getShapeFactory().pointXY(lon, lat);
        }

        return super.position;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (LongRangeAISBroadcastMessageFieldMap field : LongRangeAISBroadcastMessageFieldMap.values()) {
            switch (field) {
                case ACCURATE:
                    if (bits.size() >= field.getStartBit())
                        accurate = bits.get(field.getStartBit());
                    break;
                case RAIM:
                    if (bits.size() >= field.getStartBit())
                        raim = bits.get(field.getStartBit());
                    break;
                case NAVIGATION_STATUS:
                    if (bits.size() >= field.getStartBit()) {
                        int nsCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                        navStatus = NavigationStatus.getForCode(nsCode);
                    }
                    break;
                case LON:
                    if (bits.size() >= field.getStartBit())
                        lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case LAT:
                    if (bits.size() >= field.getStartBit())
                        lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    break;
                case SPEED:
                    if (bits.size() >= field.getStartBit())
                        speed = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case COURSE:
                    if (bits.size() >= field.getStartBit())
                        course = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case GNSS:
                    if (bits.size() >= field.getStartBit())
                        gnss = !bits.get(field.getStartBit());
                    break;
                default:
                    if (LOG.isDebugEnabled())
                        LOG.debug("Ignoring field: {}", field.name());
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum LongRangeAISBroadcastMessageFieldMap implements FieldMap {

        ACCURATE(38, 38),
        RAIM(39, 39),
        NAVIGATION_STATUS(40, 43),
        LON(44, 61),
        LAT(62, 78),
        SPEED(79, 84),
        COURSE(85, 93),
        GNSS(94, 94),
        SPARE(95, 95);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        LongRangeAISBroadcastMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
