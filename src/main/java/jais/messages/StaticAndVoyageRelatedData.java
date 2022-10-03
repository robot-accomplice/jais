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
import jais.ByteArrayUtils;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.ShipType;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class StaticAndVoyageRelatedData extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(StaticAndVoyageRelatedData.class);

    private final static DateTimeFormatter ETA_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            .withZone(ZoneOffset.UTC.normalized());

    private int version;
    private int imo;
    private byte[] callsign;
    private byte[] shipName;
    private ShipType shipType = ShipType.OTHER_NO_INFO;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private EPFDFixType epfd;
    private int month = 1;
    private int day = 1;
    private int hour;
    private int minute;
    private float draught;
    private byte[] destination;
    private boolean dte;

    /**
     *
     * @param source
     * @param packets
     */
    public StaticAndVoyageRelatedData(String source, AISSentence... packets) {
        super(source, packets);
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public StaticAndVoyageRelatedData(String source, AISMessageType type, AISSentence... packets) {
        super(source, type, packets);
    }

    /**
     *
     * @return
     */
    public String getCallsign() {
        return ByteArrayUtils.bArray2Str(this.callsign);
    }

    /**
     *
     * @return
     */
    public String getShipName() {
        return ByteArrayUtils.bArray2Str(this.shipName);
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getETA() {
        StringBuilder eta = new StringBuilder();
        ZonedDateTime dt = ZonedDateTime.now().toInstant().atZone(ZoneOffset.UTC.normalized());
        int year = dt.getYear();
        int month = dt.getMonthValue();

        if (this.month > 0) {
            // properly formatted month
            if (this.month < 10) {
                eta.append("0").append(this.month);
            } else if (this.month > 12) {
                eta.append("12");
                month = 12; // use this to validate the days later
            } else {
                eta.append(this.month);
            }
            eta.append("/");

            // assume next year
            if (this.month < month) {
                year++;
            }

            // prepend datetime string with YYYY/
            eta.insert(0, year).insert(4, "/");

            // recreate the Calendar object based on the validated month
            dt = dt.withMonth(month).withYear(year);

            // Get the number of days in that month
            int daysInMonth = dt.getMonth().maxLength();

            // properly formatted day
            if (this.day < 1) {
                eta.append("01");
            } else if (this.day < 10) {
                eta.append("0").append(this.day);
            } else eta.append(Math.min(this.day, daysInMonth));
            eta.append(" ");

            // properly formatted hour
            if (this.hour < 1) {
                eta.append("00");
            } else if (this.hour < 10) {
                eta.append("0").append(this.hour);
            } else if (this.hour >= 24) {
                eta.append("00");
            } else {
                eta.append(this.hour);
            }
            eta.append(":");

            // properly formatted minute
            if (this.minute < 1 || minute > 59) {
                eta.append("00");
            } else if (this.minute < 10) {
                eta.append("0").append(this.minute);
            } else {
                eta.append(this.minute);
            }
        } else {
            // default to epoch if month is invalid
            eta.append("1970/01/01 00:00");
        }

        try {
            dt = ZonedDateTime.parse(eta.toString(), ETA_FORMATTER);
        } catch (Exception e) {
            LOG.warn("Invalid ETA, setting to epoch");
            dt = ZonedDateTime.parse("1970/01/01 00:00", ETA_FORMATTER);
        }

        return dt;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        if (this.destination == null)
            return null;
        return ByteArrayUtils.bArray2Str(this.destination);
    }

    /**
     *
     * @return
     */
    public boolean dteReady() {
        return !this.dte;
    }

    /**
     *
     */
    @Override
    public final void decode() {
        super.decode();

        for (StaticAndVoyageFieldMap field : StaticAndVoyageFieldMap.values()) {
            switch (field) {
                case VERSION:
                    if (bits.size() >= field.getStartBit())
                        this.version = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case IMO:
                    if (bits.size() >= field.getStartBit())
                        this.imo = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case CALL_SIGN:
                    if (bits.size() >= field.getStartBit())
                        this.callsign = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case SHIP_NAME:
                    if (bits.size() >= field.getStartBit())
                        this.shipName = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case SHIP_TYPE:
                    int shipCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    if (bits.size() >= field.getStartBit())
                        this.shipType = ShipType.getForCode(shipCode);
                    if (this.shipType == null) {
                        LOG.debug("No ShipType for {}", shipCode);
                        this.shipType = ShipType.OTHER_NO_INFO;
                    }
                    break;
                case TO_BOW:
                    if (bits.size() >= field.getStartBit())
                        toBow = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TO_STERN:
                    if (bits.size() >= field.getStartBit())
                        toStern = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TO_PORT:
                    if (bits.size() >= field.getStartBit())
                        toPort = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case TO_STARBOARD:
                    if (bits.size() >= field.getStartBit())
                        toStarboard = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case EPFD:
                    int epfdCode = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    if (bits.size() >= field.getStartBit())
                        epfd = EPFDFixType.getForCode(epfdCode);
                    break;
                case ETA_MONTH:
                    if (bits.size() >= field.getStartBit())
                        month = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ETA_DAY:
                    if (bits.size() >= field.getStartBit())
                        day = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ETA_HOUR:
                    if (bits.size() >= field.getStartBit())
                        hour = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case ETA_MINUTE:
                    if (bits.size() >= field.getStartBit())
                        minute = AISMessageDecoder.decodeUnsignedInt(bits, field.getStartBit(), field.getEndBit());
                    break;
                case DRAUGHT:
                    if (bits.size() >= field.getStartBit())
                        draught = AISMessageDecoder.decodeDraught(bits, field.getStartBit(), field.getEndBit());
                    break;
                case DESTINATION:
                    if (bits.size() >= field.getStartBit())
                        destination = AISMessageDecoder.decodeToByteArray(bits, field.getStartBit(),
                                field.getEndBit());
                    break;
                case DTE:
                    if (field.getStartBit() < bits.size()) {
                        dte = bits.get(field.getStartBit());
                    } else {
                        if (LOG.isDebugEnabled())
                            LOG.debug("Reached end of message before we could retrieve DTE value!");
                    }
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
    @Getter
    private enum StaticAndVoyageFieldMap implements FieldMap {

        VERSION(38, 39),
        IMO(40, 69),
        CALL_SIGN(70, 111),
        SHIP_NAME(112, 231),
        SHIP_TYPE(232, 239),
        TO_BOW(240, 248),
        TO_STERN(249, 257),
        TO_PORT(258, 263),
        TO_STARBOARD(264, 269),
        EPFD(270, 273),
        ETA_MONTH(274, 277),
        ETA_DAY(278, 282),
        ETA_HOUR(283, 287),
        ETA_MINUTE(288, 293),
        DRAUGHT(294, 301),
        DESTINATION(302, 421),
        DTE(422, 422),
        SPARE(423, 423);

        private final int startBit;
        private final int endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        StaticAndVoyageFieldMap(int startBit, int endBit) {
            this.startBit = startBit;
            this.endBit = endBit;
        }
    }
}
