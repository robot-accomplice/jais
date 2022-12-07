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

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jais.messages.ExtendedClassBCSPositionReport;
import jais.messages.PositionReportBase;
import jais.messages.StandardClassBCSPositionReport;
import jais.messages.StaticAndVoyageRelatedData;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import jais.messages.enums.ShipType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * An aggregate representation of a vessel based on PositionReport and
 * StaticAndVoyageRelatedData messages
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
@Getter
@Setter
public class Vessel implements Cloneable {

    private final static DateTimeFormatter ETA_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")
            .withZone(ZoneOffset.UTC.normalized());

    private Identifier id;
    private int version;
    private int imo;
    private byte[] callsign;
    private byte[] shipName;
    private ShipType shipType = ShipType.OTHER_NO_INFO;
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
    private NavigationStatus navigationStatus = NavigationStatus.NOT_DEFINED;
    private float rateOfTurn;
    private float speed = 102.3f;
    private boolean accuracy = false;
    private float lon = -181f;
    private float lat = -91f;
    private float courseOverGround = 360f;
    private int heading = 511;
    private int second;
    private ManeuverType maneuver = ManeuverType.NOT_AVAILABLE;
    private boolean raim = false;
    private int repeat;
    private int radio;
    private ZonedDateTime eta = ZonedDateTime.parse("1970/01/01 00:00", ETA_FORMATTER);
    private long currentMessageTimestamp;
    private long currentPositionTimestamp;
    private long previousPositionTimestamp;
    private long timeSent;
    private long messageCount = 1;

    /**
     * Creates a new Vessel object based on a StandardClassBCSPositionReport
     *
     * @param report a valid StandardClassBCSPositionReport
     */
    public Vessel(StandardClassBCSPositionReport report) {
        this.id = new Identifier(report.getMmsi(), report.getSource());
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.speed = report.getSpeed();
        this.timeSent = report.getTimeSent();
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
        this.courseOverGround = report.getCourseOverGround();
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
        this.shipName = ByteArrayUtils.str2bArray(report.getShipName());
        this.shipType = report.getShipType();
        this.timeSent = report.getTimeSent();
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
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.maneuver = report.getManeuver();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.navigationStatus = report.getStatus();
        this.rateOfTurn = report.getRateOfTurn();
        this.accuracy = report.isAccuracy();
        this.raim = report.isRaim();
        this.timeSent = report.getTimeSent();
    }

    /**
     * Creates a new Vessel object based on a StaticAndVoyageRelatedData object
     *
     * @param savrd The StaticAndVoyageRelatedData object we want to use as a basis for the new Vessel object
     */
    public Vessel(StaticAndVoyageRelatedData savrd) {
        this.id = new Identifier(savrd.getMmsi(), savrd.getSource());
        this.currentMessageTimestamp = savrd.getTimeReceived();
        this.imo = savrd.getImo();
        this.shipType = savrd.getShipType();
        this.shipName = (savrd.getShipName() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipName());
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
        this.messageCount++;
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
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.maneuver = report.getManeuver();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.navigationStatus = report.getStatus();
        this.rateOfTurn = report.getRateOfTurn();
        this.accuracy = report.isAccuracy();
        this.raim = report.isRaim();
        this.timeSent = report.getTimeSent();
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StandardClassBCSPositionReport
     * 
     * @param report The StandardClassBCSPositionReport we want to use to update the Vessel object
     */
    public void addUpdatePositionReportClassB(StandardClassBCSPositionReport report) {
        this.currentMessageTimestamp = report.getTimeReceived();
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an
     * instance of ExtendedClassBCSPositionReport
     * 
     * @param report the ExtendedClassBCSPositionReport we want to use to update the Vessel object
     */
    public void addUpdatePositionReportClassB(ExtendedClassBCSPositionReport report) {
        this.currentMessageTimestamp = report.getTimeReceived();
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.toBow = report.getToBow();
        this.toStern = report.getToStern();
        this.toPort = report.getToPort();
        this.toStarboard = report.getToStarboard();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        this.epfd = report.getEpfd();
        this.shipName = (report.getShipName() == null) ? null : ByteArrayUtils.str2bArray(report.getShipName());
        this.shipType = report.getShipType();
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StaticAndVoyageRelatedData
     *
     * @param savrd the StaticAndVoyageRelatedData we want to use to update the Vessel object
     */
    public void addUpdateStaticReport(StaticAndVoyageRelatedData savrd) {
        this.imo = savrd.getImo();
        this.shipType = savrd.getShipType();
        this.shipName = (savrd.getShipName() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipName());
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
        this.messageCount++;
    }

    /**
     * Performs a deep copy of the current Vessel object
     *
     * @return a clone of the current Vessel object
     * @throws CloneNotSupportedException if the clone operation cannot be completed
     */
    @Override
    public Vessel clone() throws CloneNotSupportedException {
        Vessel clone = (Vessel) super.clone();

        clone.id = this.id;
        clone.accuracy = this.accuracy;
        clone.callsign = this.callsign;
        clone.courseOverGround = this.courseOverGround;
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
        clone.shipName = this.shipName;
        clone.shipType = this.shipType;
        clone.speed = this.speed;
        clone.navigationStatus = this.navigationStatus;
        clone.currentMessageTimestamp = this.currentMessageTimestamp;
        clone.previousPositionTimestamp = this.previousPositionTimestamp;
        clone.currentPositionTimestamp = this.currentPositionTimestamp;
        clone.toBow = this.toBow;
        clone.toPort = this.toPort;
        clone.toStarboard = this.toStarboard;
        clone.toStern = this.toStern;
        clone.rateOfTurn = this.rateOfTurn;
        clone.version = this.version;
        clone.timeSent = this.timeSent;

        return clone;
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
    public String getShipName() {
        return (this.shipName == null) ? null : ByteArrayUtils.bArray2Str(this.shipName);
    }

    /**
     * @return the destination value of whatever StaticAndVoyageRelatedData message
     *         may have been used to populate this object
     */
    public String getDestination() {
        return (this.destination == null) ? null : ByteArrayUtils.bArray2Str(this.destination);
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
     * @param obj the object we wish to compare to the current Vessel
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
     * @return a String representation of the current Vessel
     */
    @Override
    public final String toString() {

        return "{ " +
                "id : { mmsi : " + getId().getMmsi() + ", source : \"" + getId().getSource() +
                " }" +
                ", version : " + getVersion() +
                ", imo : " + getImo() +
                ", callsign : \"" + getCallsign() + "\"" +
                ", shipName : \"" + getShipName() + "\"" +
                ", shiptype : \"" + getShipType() + "\"" +
                ", toBow : " + getToBow() +
                ", toPort : " + getToPort() +
                ", toStern : " + getToStern() +
                ", toStarboard : " + getToStarboard() +
                ", epfd : " + getEpfd() +
                ", month : " + getMonth() +
                ", day : " + getDay() +
                ", hour : " + getHour() +
                ", minute : " + getMinute() +
                ", second : " + getSecond() +
                ", draught : " + getDraught() +
                ", destination : " + getDestination() +
                ", dte : " + isDte() +
                ", navigationStatus : " + getNavigationStatus() +
                ", turn : " + getRateOfTurn() +
                ", speed : " + getSpeed() +
                ", accuracy : " + isAccuracy() +
                ", lon : " + getLon() +
                ", lat : " + getLat() +
                ", courseOverGround : " + getCourseOverGround() +
                ", heading : " + getHeading() +
                ", maneuver : " + getManeuver() +
                ", raim : " + isRaim() +
                ", repeat : " + getRepeat() +
                ", radio : " + getRadio() +
                ", eta : " + getEta() +
                ", currentMessageTimestamp : " + getCurrentMessageTimestamp() +
                ", currentPositionTimestamp : " + getCurrentPositionTimestamp() +
                ", previousPositionTimestamp : " + getPreviousPositionTimestamp() +
                ", timeSent : " + this.timeSent +
                "}";
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
         * @param mmsi The MMSI portion of the identifier
         * @param source The data source portion of the identifier
         */
        public Identifier(int mmsi, String source) {
            this.mmsi = mmsi;
            this.source = source;
        }
    }
}
