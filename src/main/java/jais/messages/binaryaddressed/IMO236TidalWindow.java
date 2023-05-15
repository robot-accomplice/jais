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
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import lombok.Getter;
import lombok.Setter;
import jais.messages.enums.BinaryAddressedMessageType;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Deprecated
@Getter
@Setter
public class IMO236TidalWindow extends BinaryAddressedMessageBase {

    private int month;
    private int day;
    private String[] tidals;
    private double lat;
    private double lon;
    private int fromHour;
    private int fromMinute;
    private int toHour;
    private int toMinute;
    private int currentDir;
    private float currentSpeed;

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     * 
     */
    public IMO236TidalWindow(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.TIDAL_WINDOW_DEPRECATED, sentences);
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        // here we need to figure out how many elements in an array of Tidal
        // information there are (could be up to three) based on the size of
        // remaining data after we decode the month and day -- may use a public
        // static inner class to represent the tidal information and just store
        // the array
    }

    /**
     *
     */
    @Getter
    private enum IMO236TidalWindowFieldMap implements FieldMap {

        DEFAULT(-1, -1),
        LATITUDE(0, 26),
        LONGITUDE(27, 54);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit the first bit of the target field
         * @param endBit the last bit of the target field
         */
        IMO236TidalWindowFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
