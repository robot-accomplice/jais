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
import lombok.Getter;
import lombok.Setter;

import org.locationtech.spatial4j.shape.Point;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class DGNSSBroadcastBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(DGNSSBroadcastBinaryMessage.class);

    private float lon;
    private float lat;
    private BitSet data;

    /**
     *
     * @param source
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public DGNSSBroadcastBinaryMessage(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
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
        if (super.position == null)
            super.position = CTX.getShapeFactory().pointXY(lon, lat);

        return position;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for (DGNSSBroadcastBinaryMessageFieldMap field : DGNSSBroadcastBinaryMessageFieldMap.values()) {
            switch (field) {
                case LON:
                    if (bits.size() >= field.getStartBit())
                        lon = AISMessageDecoder.decodeLongitude(bits, field.getStartBit(), field.getEndBit());
                case LAT:
                    if (bits.size() >= field.getStartBit())
                        lat = AISMessageDecoder.decodeLatitude(bits, field.getStartBit(), field.getEndBit());
                case DATA:
                    // store the undecoded portion of the bitArray in the data
                    // field for later decoding by subtype
                    if (bits.size() > field.getStartBit()) {
                        data = new BitSet(bits.size() - field.getStartBit());
                        for (int b = field.getStartBit(); b < bits.size() - 1; b++) {
                            data.set(b, bits.get(field.getStartBit() + b));
                        }
                    }
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
    private enum DGNSSBroadcastBinaryMessageFieldMap implements FieldMap {

        LON(40, 57),
        LAT(58, 74),
        SPARE(75, 79),
        DATA(80, -1);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        DGNSSBroadcastBinaryMessageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
