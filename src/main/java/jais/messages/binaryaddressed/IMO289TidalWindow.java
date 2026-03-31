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

package jais.messages.binaryaddressed;

import jais.AISSentence;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289TidalWindow extends BinaryAddressedMessageBase {

    private static final int PREDICTION_SIZE_BITS = 88;
    private static final int MAX_PREDICTIONS = 3;

    private int month;
    private int day;
    private final List<TidalPrediction> predictions = new ArrayList<>();

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289TidalWindow(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.TIDAL_WINDOW, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        month = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289TidalWindowFieldMap.MONTH.getStartBit(),
                IMO289TidalWindowFieldMap.MONTH.getEndBit());
        day = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289TidalWindowFieldMap.DAY.getStartBit(),
                IMO289TidalWindowFieldMap.DAY.getEndBit());

        predictions.clear();
        for (int i = 0; i < MAX_PREDICTIONS; i++) {
            int startBit = IMO289TidalWindowFieldMap.PREDICTIONS.getStartBit() + (i * PREDICTION_SIZE_BITS);
            if (bits.length() <= startBit + 1) {
                break;
            }

            predictions.add(new TidalPrediction(
                    decodeLowResolutionLongitude(bits, startBit, startBit + 24),
                    decodeLowResolutionLatitude(bits, startBit + 25, startBit + 48),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 49, startBit + 53),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 54, startBit + 59),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 60, startBit + 64),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 65, startBit + 70),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 71, startBit + 79),
                    AISMessageDecoder.decodeUnsignedInt(bits, startBit + 80, startBit + 87) / 10f));
        }
    }

    /**
     *
     * @return immutable list of tidal predictions
     */
    public List<TidalPrediction> getPredictions() {
        return Collections.unmodifiableList(predictions);
    }

    private static double decodeLowResolutionLongitude(java.util.BitSet bits, int startBit, int endBit) {
        int encoded = AISMessageDecoder.decodeSignedInt(bits, startBit, endBit);
        if (encoded == 0x6791AC0) {
            return 181d;
        }
        return encoded / 60000.0d;
    }

    private static double decodeLowResolutionLatitude(java.util.BitSet bits, int startBit, int endBit) {
        int encoded = AISMessageDecoder.decodeSignedInt(bits, startBit, endBit);
        if (encoded == 0x3412140) {
            return 91d;
        }
        return encoded / 60000.0d;
    }

    /**
     *
     */
    @Getter
    public static class TidalPrediction {
        private final double longitude;
        private final double latitude;
        private final int fromHour;
        private final int fromMinute;
        private final int toHour;
        private final int toMinute;
        private final int currentDirection;
        private final float currentSpeed;

        TidalPrediction(double longitude, double latitude, int fromHour, int fromMinute,
                int toHour, int toMinute, int currentDirection, float currentSpeed) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.fromHour = fromHour;
            this.fromMinute = fromMinute;
            this.toHour = toHour;
            this.toMinute = toMinute;
            this.currentDirection = currentDirection;
            this.currentSpeed = currentSpeed;
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289TidalWindowFieldMap implements FieldMap {

        MONTH(88, 91),
        DAY(92, 96),
        PREDICTIONS(97, -1);

        private final int startBit;
        private final int endBit;

        IMO289TidalWindowFieldMap(int startBit, int endBit) {
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
