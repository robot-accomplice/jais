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

import jais.messages.*;
import jais.messages.enums.*;
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

    private Identifier id;
    private int version;
    private int imo;
    private byte[] callSign;
    private byte[] shipName;
    private ShipType shipType = ShipType.NOT_AVAILABLE;
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
    private float rateOfTurn = AISMessage.DEFAULT_RATE_OF_TURN;
    private float speed = AISMessage.DEFAULT_SPEED_OVER_GROUND;
    private boolean accuracy = false;
    private double lon = AISMessage.DEFAULT_LONGITUDE;
    private double lat = AISMessage.DEFAULT_LATITUDE;
    private float courseOverGround = AISMessage.DEFAULT_COURSE_OVER_GROUND;
    private int heading = AISMessage.DEFAULT_HEADING;
    private int second;
    private ManeuverType maneuver = ManeuverType.NOT_AVAILABLE;
    private boolean raim = false;
    private int repeat;
    private int radio;
    private String eta;
    private long currentMessageTimestamp;
    private long currentStaticTimestamp;
    private long previousStaticTimestamp;
    private long currentPositionTimestamp;
    private long previousPositionTimestamp;
    private long timeSent;
    private long messageCount = 1;
    private byte[] currentPositionSource;
    private byte[] currentStaticSource;
    private VesselClass vesselClass = VesselClass.UNSPECIFIED;

    /**
     * No argument constructor for increased flexibility
     */
    public Vessel() {}

    /**
     * Creates a new Vessel object based on a StandardClassBCSPositionReport
     *
     * @param report a valid StandardClassBCSPositionReport
     */
    public Vessel(StandardClassBCSPositionReport report) {
        this.id = new Identifier(report.getMmsi(), report.getSource());
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.speed = report.getSpeed();
        this.timeSent = report.getTimeSent();
        this.vesselClass = VesselClass.B;
        this.messageCount++;
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
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
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
        this.vesselClass = VesselClass.B;
        this.messageCount++;
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
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
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
        this.vesselClass = VesselClass.A;
        this.messageCount++;
    }

    /**
     * Creates a new Vessel object based on a StaticAndVoyageRelatedData object
     *
     * @param savrd The StaticAndVoyageRelatedData object we want to use as a basis for the new Vessel object
     */
    public Vessel(StaticAndVoyageRelatedData savrd) {
        this.id = new Identifier(savrd.getMmsi(), savrd.getSource());
        this.currentMessageTimestamp = savrd.getTimeReceived();
        this.previousStaticTimestamp = this.currentStaticTimestamp;
        this.currentStaticTimestamp = savrd.getTimeReceived();
        this.currentStaticSource = (savrd.getSource() == null) ? null : savrd.getSource().getBytes();
        this.imo = savrd.getImo();
        this.shipType = savrd.getShipType();
        this.shipName = (savrd.getShipName() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipName());
        this.callSign = (savrd.getCallSign() == null) ? null : ByteArrayUtils.str2bArray(savrd.getCallSign());
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
        this.vesselClass = VesselClass.A;
        this.messageCount++;
    }

    /**
     * Creates a new Vessel object based on a StaticDataReport object
     *
     * @param sdr The StaticDataReport object we want to use as a basis for the new Vessel object
     */
    public Vessel(StaticDataReport sdr) {
        this.id = new Identifier(sdr.getMmsi(), sdr.getSource());
        this.currentMessageTimestamp = sdr.getTimeReceived();
        this.currentStaticTimestamp = sdr.getTimeReceived();
        this.currentStaticSource = (sdr.getSource() == null) ? null : sdr.getSource().getBytes();
        this.shipName = (sdr.getShipName() == null) ? null : sdr.getShipName().getBytes();
        this.callSign = (sdr.getCallSign() == null) ? null : sdr.getCallSign().getBytes();
        this.shipType = sdr.getShipType();
        this.toBow = sdr.getToBow();
        this.toPort = sdr.getToPort();
        this.toStarboard = sdr.getToStarboard();
        this.toStern = sdr.getToStern();
        this.vesselClass = VesselClass.B;
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
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
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
        if (this.vesselClass == VesselClass.UNSPECIFIED) this.vesselClass = VesselClass.A;
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StandardClassBCSPositionReport
     * 
     * @param report The StandardClassBCSPositionReport we want to use to update the Vessel object
     */
    public void addUpdatePositionReportClassB(StandardClassBCSPositionReport report) {
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.courseOverGround = report.getCourseOverGround();
        this.heading = report.getHeading();
        this.lat = report.getLat();
        this.lon = report.getLon();
        this.radio = report.getRadio();
        this.repeat = report.getRepeat();
        this.second = report.getSecond();
        this.speed = report.getSpeed();
        if (this.vesselClass == VesselClass.UNSPECIFIED) this.vesselClass = VesselClass.B;
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
        this.previousPositionTimestamp = this.currentMessageTimestamp;
        this.currentMessageTimestamp = report.getTimeReceived();
        this.currentPositionTimestamp = report.getTimeReceived();
        this.currentPositionSource = (report.getSource() == null) ? null : report.getSource().getBytes();
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
        if (this.vesselClass == VesselClass.UNSPECIFIED) this.vesselClass = VesselClass.B;
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StaticAndVoyageRelatedData
     *
     * @param savrd the StaticAndVoyageRelatedData we want to use to update the Vessel object
     */
    public void addUpdateStaticReport(StaticAndVoyageRelatedData savrd) {
        this.currentMessageTimestamp = savrd.getTimeReceived();
        this.currentStaticTimestamp = savrd.getTimeReceived();
        this.currentStaticSource = (savrd.getSource() == null) ? null : savrd.getSource().getBytes();
        this.imo = savrd.getImo();
        this.shipType = savrd.getShipType();
        this.shipName = (savrd.getShipName() == null) ? null : ByteArrayUtils.str2bArray(savrd.getShipName());
        this.callSign = (savrd.getCallSign() == null) ? null : ByteArrayUtils.str2bArray(savrd.getCallSign());
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
        if (this.vesselClass == VesselClass.UNSPECIFIED) this.vesselClass = VesselClass.A;
        this.messageCount++;
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within
     * an instance of StaticDataReport
     *
     * @param sdr the StaticDataReport with which we want to update the Vessel object
     */
    public void addUpdateStaticDataReport(StaticDataReport sdr) {
        this.currentMessageTimestamp = sdr.getTimeReceived();
        this.currentStaticTimestamp = sdr.getTimeReceived();
        this.currentStaticSource = (sdr.getSource() == null) ? null : sdr.getSource().getBytes();
        this.shipName = (sdr.getShipName() == null) ? null : sdr.getShipName().getBytes();
        this.callSign = (sdr.getCallSign() == null) ? null : sdr.getCallSign().getBytes();
        this.shipType = sdr.getShipType();
        this.toBow = sdr.getToBow();
        this.toPort = sdr.getToPort();
        this.toStarboard = sdr.getToStarboard();
        this.toStern = sdr.getToStern();
        if (this.vesselClass == VesselClass.UNSPECIFIED) this.vesselClass = VesselClass.B;
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
        clone.callSign = this.callSign;
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
        clone.currentPositionTimestamp = this.currentPositionTimestamp;
        clone.previousPositionTimestamp = this.previousPositionTimestamp;
        clone.toBow = this.toBow;
        clone.toPort = this.toPort;
        clone.toStarboard = this.toStarboard;
        clone.toStern = this.toStern;
        clone.rateOfTurn = this.rateOfTurn;
        clone.version = this.version;
        clone.timeSent = this.timeSent;
        clone.currentStaticSource = this.currentStaticSource;
        clone.currentPositionSource = this.currentPositionSource;
        clone.currentStaticTimestamp = this.currentStaticTimestamp;
        clone.vesselClass = this.vesselClass;
        clone.messageCount = this.messageCount;

        return clone;
    }

    /**
     * @return the callsign value of whatever StaticAndVoyageRelatedData message may
     *         have been used to populate this object
     */
    public String getCallSign() {
        return (this.callSign == null) ? null : ByteArrayUtils.bArray2Str(this.callSign);
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
     *
     * @return the source of the last position message (if set), otherwise returns null
     */
    public String getLastPositionSource() {
        return (this.currentPositionSource == null) ? null : ByteArrayUtils.bArray2Str(this.currentPositionSource);
    }

    /**
     *
     * @return the source of the last position message (if set), otherwise returns null
     */
    public String getLastStaticSource() {
        return (this.currentStaticSource == null) ? null : ByteArrayUtils.bArray2Str(this.currentStaticSource);
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
        return String.format(
            """
                {
                    "id": {
                        "mmsi": %d,
                        "source": "%s"
                    },
                    "version": %d,
                    "imo": %d,
                    "callSign": "%s",
                    "shipName": "%s",
                    "shipType": "%s",
                    "toBow": %d,
                    "toPort": %d,
                    "toStern": %d,
                    "toStarboard": %d,
                    "epfd": "%s",
                    "month": %d,
                    "day": %d,
                    "hour": %d,
                    "minute": %d,
                    "second": %d,
                    "draught": %f,
                    "destination": "%s",
                    "dte": %b,
                    "navigationStatus": "%s",
                    "turn": %f,
                    "speed": %f,
                    "accuracy": %b,
                    "lon": %f,
                    "lat": %f,
                    "courseOverGround": %f,
                    "heading": %d,
                    "maneuver": %s,
                    "raim": %b,
                    "repeat": %d,
                    "radio": %d,
                    "eta": "%s",
                    "currentMessageTimestamp": %d,
                    "currentPositionTimestamp": %d,
                    "previousPositionTimestamp": %d,
                    "currentStaticTimestamp": %d,
                    "timeSent": %d,
                    "currentPositionSource": "%s",
                    "currentStaticSource": "%s"
                }
            """,
                getId().mmsi(), getId().source(), this.getVersion(), this.getImo(),
                this.getCallSign(), this.getShipName(), this.getShipType().name(), this.getToBow(),
                this.getToPort(), this.getToStern(), this.getToStarboard(), this.getEpfd().name(), this.getMonth(),
                this.getDay(), this.getHour(), this.getMinute(), this.getSecond(), this.getDraught(),
                this.getDestination(), this.isDte(), this.getNavigationStatus().name(), this.getRateOfTurn(), this.getSpeed(),
                this.isAccuracy(), this.getLon(), this.getLat(), this.getCourseOverGround(), this.getHeading(),
                this.getManeuver().name(), this.isRaim(), this.getRepeat(), this.getRadio(), this.getEta(),
                this.getCurrentMessageTimestamp(), this.getCurrentPositionTimestamp(),
                this.getPreviousPositionTimestamp(), this.getCurrentStaticTimestamp(), this.getTimeSent(),
                ByteArrayUtils.bArray2Str(this.getCurrentPositionSource()),
                ByteArrayUtils.bArray2Str(this.getCurrentStaticSource())
        );
    }

    /**
         * A unique vessel identifier based on a combination of the vessel MMSI and the
         * provided data source
         */
        public record Identifier(int mmsi, String source) {

        /**
         * @param mmsi   The MMSI portion of the identifier
         * @param source The data source portion of the identifier
         */
        public Identifier {
        }
    }
}
