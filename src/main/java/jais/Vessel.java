/*
 * Copyright 2016-2019 Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}.
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
package jais;

import jais.messages.ExtendedClassBCSPositionReport;
import jais.messages.PositionReportBase;
import jais.messages.StandardClassBCSPositionReport;
import jais.messages.StaticAndVoyageRelatedData;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import jais.messages.enums.ShipType;
import lombok.Data;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An aggregate representation of a vessel based on PositionReport and
 * StaticAndVoyageRelatedData messages
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Data
public class Vessel implements Cloneable {

    private final static Logger LOG = LogManager.getLogger(Vessel.class);
    private final static DateTimeFormatter ETA_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            .withZone(ZoneOffset.UTC.normalized());

    private Identifier id;
    private int version;
    private int imo;
    private byte[] callsign;
    private byte[] shipname;
    private ShipType shiptype = ShipType.OTHER_NO_INFO;
    private int toBow;
    private int toStern;
    private int toPort;
    private int toStarboard;
    private EPFDFixType epfd = EPFDFixType.UNDEFINED;
    private int month = 1;
    private int day = 1;
    private int hour;
    private int minute;
    private float draught;
    private byte[] destination;
    private boolean dte;
    private NavigationStatus status = NavigationStatus.NOT_DEFINED;
    private float turn;
    private float speed;
    private boolean accurate = false;
    private float lon;
    private float lat;
    private float course;
    private int heading = 511;
    private int second;
    private ManeuverType maneuver = ManeuverType.NOT_AVAILABLE;
    private boolean raim = false;
    private int repeat = 0;
    private int radio;
    private ZonedDateTime eta = ZonedDateTime.parse("1970/01/01 00:00", ETA_FORMATTER);
    private long currentMessageTimestamp;
    private long currentPositionTimestamp;
    private long previousPositionTimestamp;
    private long timeSent;

    /**
     * Creates a new Vessel object based on a StandardClassBCSPositionReport
     *
     * @param report a valid StandardClassBCSPositionReport
     */
    public Vessel(StandardClassBCSPositionReport report) {
        this.id = new Identifier(report.getMmsi(), report.getSource());
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.course = report.getCourse();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.speed = report.getSpeed();
        this.timeSent = report.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("New Vessel from StandardClassBCSPositionReport:\n{}", toString());
    }

    /**
     * Creates a new Vessel object based on an ExtendedClassBCSPositionReport
     *
     * @param report a valid ExtendedClassBCSPositionReport
     */
    public Vessel(ExtendedClassBCSPositionReport report) {
        this.id = new Identifier(report.getMmsi(), report.getSource());
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.course = report.getCourse();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.repeat = report.getRepeat();
        this.speed = report.getSpeed();
        this.toBow = report.getToBow();
        this.toStern = report.getToStern();
        this.toPort = report.getToPort();
        this.toStarboard = report.getToStarboard();
        this.second = report.getSecond();
        this.shipname = ByteArrayUtils.str2bArray(report.getShipName());
        this.shiptype = report.getShipType();
        this.timeSent = report.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("New Vessel from ExtendedClassBCSPositionReport:\n{}", toString());
    }

    /**
     * Creates a new Vessel object based on a concrete class that extends
     * PositionReportBase
     *
     * @param <T>    a concrete class that extends PositionReportBase
     * @param report an instance of a concrete class that extends PositionReportBase
     */
    public <T extends PositionReportBase> Vessel(T report) {
        this.id = new Identifier(report.getMmsi(), report.getSource());
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.course = report.getCourse();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.maneuver = report.getManeuver();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.status = report.getStatus();
        this.turn = report.getTurn();
        this.accurate = report.isAccurate();
        this.raim = report.isRaim();
        this.timeSent = report.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("New Vessel from PositionReportBase:\n{}", toString());
    }

    /**
     * Creates a new Vessel object based on a StaticAndVoyageRelatedData object
     *
     * @param savrd
     */
    public Vessel(StaticAndVoyageRelatedData savrd) {
        this.id = new Identifier(savrd.getMmsi(), savrd.getSource());
        this.currentMessageTimestamp = savrd.getTimeReceived();
        this.imo = savrd.getImo();
        this.shiptype = savrd.getShiptype();
        this.shipname = (savrd.getShipname() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipname());
        this.callsign = (savrd.getCallsign() == null) ? null : ByteArrayUtils.str2bArray(savrd.getCallsign());
        this.destination = (savrd.getDestination() == null) ? null : ByteArrayUtils.str2bArray(savrd.getDestination());
        this.draught = savrd.getDraught();
        this.eta = savrd.getETA();
        this.epfd = savrd.getEpfd();
        this.day = savrd.getDay();
        this.month = savrd.getMonth();
        this.hour = savrd.getHour();
        this.minute = savrd.getMinute();
        this.repeat = savrd.getRepeat();
        this.toBow = savrd.getToBow();
        this.toPort = savrd.getToPort();
        this.toStarboard = savrd.getToStarboard();
        this.toStern = savrd.getToStern();
        this.version = savrd.getVersion();
        this.timeSent = savrd.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("New Vessel from StaticAndVoyageRelatedData:\n{}", toString());
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within a
     * concrete extension of PositionReportBase
     *
     * @param <T>    a concrete class that extends PositionReportBase
     * @param report an instance of a concrete class that extends PositionReportBase
     */
    public <T extends PositionReportBase> void addUpdatePositionReport(T report) {
        this.currentMessageTimestamp = report.getTimeReceived();
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.currentPositionTimestamp = report.getTimeReceived();
        this.course = report.getCourse();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.maneuver = report.getManeuver();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.status = report.getStatus();
        this.turn = report.getTurn();
        this.accurate = report.isAccurate();
        this.raim = report.isRaim();
        this.timeSent = report.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("Vessel updated with PositionReportBase:\n{}", toString());
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StaticAndVoyageRelatedData
     *
     * @param savrd
     */
    public void addUpdateStaticReport(StaticAndVoyageRelatedData savrd) {
        this.imo = savrd.getImo();
        this.shiptype = savrd.getShiptype();
        this.shipname = (savrd.getShipname() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipname());
        this.callsign = (savrd.getCallsign() == null) ? null : ByteArrayUtils.str2bArray(savrd.getCallsign());
        this.destination = (savrd.getDestination() == null) ? null : ByteArrayUtils.str2bArray(savrd.getDestination());
        this.draught = savrd.getDraught();
        this.eta = savrd.getETA();
        this.epfd = savrd.getEpfd();
        this.day = savrd.getDay();
        this.month = savrd.getMonth();
        this.hour = savrd.getHour();
        this.minute = savrd.getMinute();
        this.repeat = savrd.getRepeat();
        this.toBow = savrd.getToBow();
        this.toPort = savrd.getToPort();
        this.toStarboard = savrd.getToStarboard();
        this.toStern = savrd.getToStern();
        this.version = savrd.getVersion();
        this.timeSent = savrd.getTimeSent();

        if (LOG.isDebugEnabled())
            LOG.debug("Vessel updated with StaticAndVoyageRelatedData:\n{}", toString());
    }

    /**
     * Performs a deep copy of the current Vessel object
     *
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Vessel clone() throws CloneNotSupportedException {
        if (LOG.isDebugEnabled())
            LOG.debug("Cloning Vessel:\n{}", toString());

        Vessel clone = (Vessel) super.clone();

        clone.id = this.id;
        clone.accurate = this.accurate;
        clone.callsign = this.callsign;
        clone.course = this.course;
        clone.day = this.day;
        clone.destination = this.destination;
        clone.draught = this.draught;
        clone.dte = this.dte;
        clone.epfd = this.epfd;
        clone.eta = this.eta;
        clone.heading = this.heading;
        clone.hour = this.hour;
        clone.imo = this.imo;
        clone.lat = this.lat;
        clone.lon = this.lon;
        clone.maneuver = this.maneuver;
        clone.minute = this.minute;
        clone.month = this.month;
        clone.radio = this.radio;
        clone.raim = this.raim;
        clone.repeat = this.repeat;
        clone.second = this.second;
        clone.shipname = this.shipname;
        clone.shiptype = this.shiptype;
        clone.speed = this.speed;
        clone.status = this.status;
        clone.currentMessageTimestamp = this.currentMessageTimestamp;
        clone.previousPositionTimestamp = this.previousPositionTimestamp;
        clone.currentPositionTimestamp = this.currentPositionTimestamp;
        clone.toBow = this.toBow;
        clone.toPort = this.toPort;
        clone.toStarboard = this.toStarboard;
        clone.toStern = this.toStern;
        clone.turn = this.turn;
        clone.version = this.version;
        clone.timeSent = this.timeSent;

        return clone;
    }

    /**
     *
     * @return Vessel.Identifier for this instance of Vessel
     */
    public Identifier getId() {
        return this.id;
    }

    /**
     * @return the version value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * @return the IMO value of whatever StaticAndVoyageRelatedData message may have
     *         been used to populate this object
     */
    public int getImo() {
        return this.imo;
    }

    /**
     * @return the callsign value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public String getCallsign() {
        return (this.callsign == null) ? null : ByteArrayUtils.bArray2Str(this.callsign);
    }

    /**
     * @return the ship name value of whatever StaticAndVoyageRelatedData message
     *         may have been used to populate this object
     */
    public String getShipname() {
        return (this.shipname == null) ? null : ByteArrayUtils.bArray2Str(this.shipname);
    }

    /**
     * @return the destination value of whatever StaticAndVoyageRelatedData message
     *         may have been used to populate this object
     */
    public String getDestination() {
        return (this.destination == null) ? null : ByteArrayUtils.bArray2Str(this.destination);
    }

    /**
     * @return the ship type value of whatever StaticAndVoyageRelatedData message
     *         may have been used to populate this object
     */
    public ShipType getShiptype() {
        return this.shiptype;
    }

    /**
     * @return the toBow value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getToBow() {
        return this.toBow;
    }

    /**
     * @return the toStern value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getToStern() {
        return this.toStern;
    }

    /**
     * @return the toPort value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getToPort() {
        return this.toPort;
    }

    /**
     * @return the toStarboard value of whatever StaticAndVoyageRelatedData message
     *         may have been used to populate this object
     */
    public int getToStarboard() {
        return this.toStarboard;
    }

    /**
     * @return the EPFD value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public EPFDFixType getEpfd() {
        return this.epfd;
    }

    /**
     * @return the month value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getMonth() {
        return this.month;
    }

    /**
     * @return the day value of whatever StaticAndVoyageRelatedData message may have
     *         been used to populate this object
     */
    public int getDay() {
        return this.day;
    }

    /**
     * @return the hour value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getHour() {
        return this.hour;
    }

    /**
     * @return the minute value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public int getMinute() {
        return this.minute;
    }

    /**
     * @return the draught value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public float getDraught() {
        return this.draught;
    }

    /**
     * @return the DTE value of whatever StaticAndVoyageRelatedData message may have
     *         been used to populate this object
     */
    public boolean isDte() {
        return this.dte;
    }

    /**
     * @return the IMO value of whatever StaticAndVoyageRelatedData message may have
     *         been used to populate this object
     */
    public NavigationStatus getStatus() {
        return this.status;
    }

    /**
     * @return the turn value of whatever PositionReport message may have been used
     *         to populate this object
     */
    public float getTurn() {
        return this.turn;
    }

    /**
     * @return the speed value of whatever PositionReport message may have been used
     *         to populate this object
     */
    public float getSpeed() {
        return this.speed;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * @return the accuracy value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public boolean isAccurate() {
        return this.accurate;
    }

    /**
     * @return the longitude value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public float getLon() {
        return this.lon;
    }

    /**
     * @param lon
     */
    public void setLon(float lon) {
        this.lon = lon;
    }

    /**
     * @return the latitude value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public float getLat() {
        return this.lat;
    }

    /**
     *
     * @param lat
     */
    public void setLat(float lat) {
        this.lat = lat;
    }

    /**
     * @return the course value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public float getCourse() {
        return this.course;
    }

    /**
     *
     * @param course
     */
    public void setCourse(float course) {
        this.course = course;
    }

    /**
     * @return the heading value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public int getHeading() {
        return this.heading;
    }

    /**
     * @return the second value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public int getSecond() {
        return this.second;
    }

    /**
     * @return the maneuver value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public ManeuverType getManeuver() {
        return this.maneuver;
    }

    /**
     * @return the RAIM value of whatever PositionReport message may have been used
     *         to populate this object
     */
    public boolean isRaim() {
        return this.raim;
    }

    /**
     * @return the repeat value of whatever PositionReport message may have been
     *         used to populate this object
     */
    public int getRepeat() {
        return this.repeat;
    }

    /**
     * @return the radio value of whatever PositionReport message may have been used
     *         to populate this object
     */
    public int getRadio() {
        return this.radio;
    }

    /**
     * @return the ETA value of whatever PositionReport message may have been used
     *         to populate this object
     */
    public ZonedDateTime getEta() {
        return this.eta;
    }

    /**
     * @return the timestamp of the most recent update to this Vessel object
     */
    public long getCurrentMessageTimestamp() {
        return this.currentMessageTimestamp;
    }

    /**
     * @return the timestamp of the most recent position update for this Vessel
     *         object
     */
    public long getCurrentPositionTimestamp() {
        return this.currentPositionTimestamp;
    }

    /**
     * @return the timestamp of the most recent position update prior to the most
     *         current position update for this Vessel object
     */
    public long getPreviousPositionTimestamp() {
        return this.previousPositionTimestamp;
    }

    /**
     *
     * @return
     */
    public long getTimeSent() {
        return this.timeSent;
    }

    /**
     * @return the hash code for this Object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.id.hashCode();
        return hash;
    }

    /**
     * @param obj
     * @return a boolean representing whether the provided object matches this
     *         object
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Vessel other = (Vessel) obj;

        return (this.id.equals(other.getId()));
    }

    /**
     * A local implementation of toString() which encapsulates all of the object
     * member fields in an approximate JSON format
     * 
     * @return
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("{ ");
        sb.append("id : { mmsi : ").append(getId().getMmsi()).append(", source : \"").append(getId().getSource())
                .append(" }");
        sb.append(", version : ").append(getVersion());
        sb.append(", imo : ").append(getImo());
        sb.append(", callsign : \"").append(getCallsign()).append("\"");
        sb.append(", shipname : \"").append(getShipname()).append("\"");
        sb.append(", shiptype : \"").append(getShiptype()).append("\"");
        sb.append(", toBow : ").append(getToBow());
        sb.append(", toPort : ").append(getToPort());
        sb.append(", toStern : ").append(getToStern());
        sb.append(", toStarboard : ").append(getToStarboard());
        sb.append(", epfd : ").append(getEpfd());
        sb.append(", month : ").append(getMonth());
        sb.append(", day : ").append(getDay());
        sb.append(", hour : ").append(getHour());
        sb.append(", minute : ").append(getMinute());
        sb.append(", second : ").append(getSecond());
        sb.append(", draught : ").append(getDraught());
        sb.append(", destination : ").append(getDestination());
        sb.append(", dte : ").append(isDte());
        sb.append(", status : ").append(getStatus());
        sb.append(", turn : ").append(getTurn());
        sb.append(", speed : ").append(getSpeed());
        sb.append(", accurate : ").append(isAccurate());
        sb.append(", lon : ").append(getLon());
        sb.append(", lat : ").append(getLat());
        sb.append(", course : ").append(getCourse());
        sb.append(", heading : ").append(getHeading());
        sb.append(", maneuver : ").append(getManeuver());
        sb.append(", raim : ").append(isRaim());
        sb.append(", repeat : ").append(getRepeat());
        sb.append(", radio : ").append(getRadio());
        sb.append(", eta : ").append(getEta());
        sb.append(", currentMessageTimestamp : ").append(getCurrentMessageTimestamp());
        sb.append(", currentPositionTimestamp : ").append(getCurrentPositionTimestamp());
        sb.append(", previousPositionTimestamp : ").append(getPreviousPositionTimestamp());
        sb.append(", timeSent : ").append(this.timeSent);
        sb.append("}");

        return sb.toString();
    }

    /**
     * A unique vessel identifier based on a combination of the vessel MMSI and the
     * provided data source
     */
    @Data
    public static class Identifier {

        private final int mmsi;
        private final String source;

        /**
         *
         * @param mmsi
         * @param source
         */
        public Identifier(int mmsi, String source) {
            this.mmsi = mmsi;
            this.source = source;
        }
    }
}
