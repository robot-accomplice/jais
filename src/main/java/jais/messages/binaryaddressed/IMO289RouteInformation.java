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
import jais.messages.enums.RouteType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
public class IMO289RouteInformation extends BinaryAddressedMessageBase {

    private static final int WAYPOINT_SIZE_BITS = 55;

    private int linkageId;
    private int senderClassification;
    private RouteType routeType;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int durationMinutes;
    private int numberOfWaypoints;
    private final List<RouteWaypoint> waypoints = new ArrayList<>();

    /**
     *
     * @param source the name of the source of this message
     * @param sentences the AIS sentences from which this message was composed
     */
    public IMO289RouteInformation(String source, AISSentence... sentences) {
        super(source, BinaryAddressedMessageType.ROUTE_INFORMATION, sentences);
    }

    /**
     */
    @Override
    public final void decode() {
        super.decode();

        linkageId = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.LINKAGE_ID.getStartBit(),
                IMO289RouteInformationFieldMap.LINKAGE_ID.getEndBit());
        senderClassification = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.SENDER_CLASSIFICATION.getStartBit(),
                IMO289RouteInformationFieldMap.SENDER_CLASSIFICATION.getEndBit());
        routeType = RouteType.getForCode(AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.ROUTE_TYPE.getStartBit(),
                IMO289RouteInformationFieldMap.ROUTE_TYPE.getEndBit()));
        month = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.MONTH.getStartBit(),
                IMO289RouteInformationFieldMap.MONTH.getEndBit());
        day = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.DAY.getStartBit(),
                IMO289RouteInformationFieldMap.DAY.getEndBit());
        hour = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.HOUR.getStartBit(),
                IMO289RouteInformationFieldMap.HOUR.getEndBit());
        minute = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.MINUTE.getStartBit(),
                IMO289RouteInformationFieldMap.MINUTE.getEndBit());
        durationMinutes = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.DURATION_MINUTES.getStartBit(),
                IMO289RouteInformationFieldMap.DURATION_MINUTES.getEndBit());
        numberOfWaypoints = AISMessageDecoder.decodeUnsignedInt(bits,
                IMO289RouteInformationFieldMap.NUMBER_OF_WAYPOINTS.getStartBit(),
                IMO289RouteInformationFieldMap.NUMBER_OF_WAYPOINTS.getEndBit());

        waypoints.clear();
        for (int i = 0; i < numberOfWaypoints; i++) {
            int startBit = IMO289RouteInformationFieldMap.WAYPOINTS.getStartBit() + (i * WAYPOINT_SIZE_BITS);
            if (bits.length() <= startBit + 54) {
                break;
            }
            int lonEncoded = AISMessageDecoder.decodeSignedInt(bits, startBit, startBit + 27);
            int latEncoded = AISMessageDecoder.decodeSignedInt(bits, startBit + 28, startBit + 54);

            waypoints.add(new RouteWaypoint(
                    decodeWaypointLongitude(lonEncoded),
                    decodeWaypointLatitude(latEncoded)));
        }
    }

    /**
     *
     * @return immutable list of decoded route waypoints
     */
    public List<RouteWaypoint> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }

    private static double decodeWaypointLongitude(int encoded) {
        if (encoded == 0x6791AC0) {
            return 181d;
        }
        return encoded / 600000.0d;
    }

    private static double decodeWaypointLatitude(int encoded) {
        if (encoded == 0x3412140) {
            return 91d;
        }
        return encoded / 600000.0d;
    }

    /**
     *
     */
    @Getter
    public static class RouteWaypoint {
        private final double longitude;
        private final double latitude;

        RouteWaypoint(double longitude, double latitude) {
            this.longitude = longitude;
            this.latitude = latitude;
        }
    }

    /**
     *
     */
    @Getter
    private enum IMO289RouteInformationFieldMap implements FieldMap {

        LINKAGE_ID(88, 97),
        SENDER_CLASSIFICATION(98, 100),
        ROUTE_TYPE(101, 105),
        MONTH(106, 109),
        DAY(110, 114),
        HOUR(115, 119),
        MINUTE(120, 125),
        DURATION_MINUTES(126, 143),
        NUMBER_OF_WAYPOINTS(144, 148),
        WAYPOINTS(149, -1);

        private final int startBit;
        private final int endBit;

        IMO289RouteInformationFieldMap(int startBit, int endBit) {
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
