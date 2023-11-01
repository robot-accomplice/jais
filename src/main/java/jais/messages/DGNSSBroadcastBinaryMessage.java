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
import java.util.BitSet;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class DGNSSBroadcastBinaryMessage extends AISMessageBase {

    private double lon;
    private double lat;
    private BitSet data;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS Sentences from which this message was composed
     */
    public DGNSSBroadcastBinaryMessage(String source, AISSentence... sentences) {
        super(source, sentences);
    }

    /**
     *
     * @param source the name of the source of this message
     * @param type the specific type of AISMessage
     * @param sentences the AIS sentences from which this message was composed
     */
    public DGNSSBroadcastBinaryMessage(String source, AISMessageType type, AISSentence... sentences) {
        super(source, type, sentences);
    }

    /**
     *
     * @return an indication that the current message type contains position information
     */
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return a geometric point representing the current position of the vessel
     */
    @Override
    public Point getPosition() {
        if (super.position == null)
            super.position = CTX.getShapeFactory().pointXY(lon, lat);

        return position;
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        for (DGNSSBroadcastBinaryMessageFieldMap field : DGNSSBroadcastBinaryMessageFieldMap.values()) {
            if (bits.size() > field.getEndBit() || field.getEndBit() == -1) {
                switch (field) {
                    case LON -> lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                    case LAT -> lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                    case DATA -> {
                        // store the undecoded portion of the bitArray in the data
                        // field for later decoding by subtype
                        data = new BitSet(bits.size() - field.getStartBit());
                        for (int b = field.getStartBit(); b < bits.size() - 1; b++) {
                            data.set(b, bits.get(field.getStartBit() + b));
                        }
                    }
                }
            }
        }
    }

    /**
     *
     */
    @Getter
    private enum DGNSSBroadcastBinaryMessageFieldMap implements FieldMap {

        LON(40, 57),
        LAT(58, 74),
        SPARE(75, 79),
        DATA(80, -1);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        DGNSSBroadcastBinaryMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
