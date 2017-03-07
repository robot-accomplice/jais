/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Jon
 */
public class Vessel implements Cloneable {

    private final static Logger LOG = LogManager.getLogger( Vessel.class );

    private Identifier _id;
    private int _version;
    private int _imo;
    private byte [] _callsign;
    private byte [] _shipname;
    private ShipType _shiptype = ShipType.OTHER_NO_INFO;
    private int _toBow;
    private int _toStern;
    private int _toPort;
    private int _toStarboard;
    private EPFDFixType _epfd;
    private int _month = 1;
    private int _day = 1;
    private int _hour;
    private int _minute;
    private float _draught;
    private byte [] _destination;
    private boolean _dte;
    private NavigationStatus _status = NavigationStatus.NOT_DEFINED;
    private float _turn;
    private float _speed;
    private boolean _accurate = false;
    private float _lon;
    private float _lat;
    private float _course;
    private int _heading = 511;
    private int _second;
    private ManeuverType _maneuver = ManeuverType.NOT_AVAILABLE;
    private boolean _raim = false;
    private int _repeat = 0;
    private int _radio;
    private final static DateTimeFormatter ETA_FORMATTER = DateTimeFormat.forPattern( "yyyy/MM/dd HH:mm" );
    private MutableDateTime _eta = MutableDateTime.parse( "1970/01/01 00:00", ETA_FORMATTER );
    private DateTime _timeReceived;

    /**
     *
     * @param report
     */
    public Vessel( StandardClassBCSPositionReport report ) {
        _id = new Identifier( report.getMmsi(), report.getSource() );
        _timeReceived = report.getTimeReceived();
        _course = report.getCourse();
        _heading = report.getHeading();
        _lat = report.getLat();
        _lon = report.getLon();
        _radio = report.getRadio();
        _repeat = report.getRepeat();
        _speed = report.getSpeed();
    }

    /**
     *
     * @param report
     */
    public Vessel( ExtendedClassBCSPositionReport report ) {
        _id = new Identifier( report.getMmsi(), report.getSource() );
        _timeReceived = report.getTimeReceived();
        _course = report.getCourse();
        _heading = report.getHeading();
        _lat = report.getLat();
        _lon = report.getLon();
        _repeat = report.getRepeat();
        _speed = report.getSpeed();
        _toBow = report.getToBow();
        _toStern = report.getToStern();
        _toPort = report.getToPort();
        _toStarboard = report.getToStarboard();
        _second = report.getSecond();
        _shipname = AISPacket.str2bArray( report.getShipName() );
        _shiptype = report.getShipType();
    }

    /**
     *
     * @param prb
     */
    public Vessel( PositionReportBase prb ) {
        _id = new Identifier( prb.getMmsi(), prb.getSource() );
        _timeReceived = prb.getTimeReceived();
        _course = prb.getCourse();
        _heading = prb.getHeading();
        _lat = prb.getLat();
        _lon = prb.getLon();
        _maneuver = prb.getManeuver();
        _radio = prb.getRadio();
        _repeat = prb.getRepeat();
        _second = prb.getSecond();
        _speed = prb.getSpeed();
        _status = prb.getStatus();
        _turn = prb.getTurn();
        _accurate = prb.isAccurate();
        _raim = prb.isRaim();
    }

    /**
     *
     * @param savrd
     */
    public Vessel( StaticAndVoyageRelatedData savrd ) {
        _id = new Identifier( savrd.getMmsi(), savrd.getSource() );
        _timeReceived = savrd.getTimeReceived();
        _imo = savrd.getImo();
        _shipname = AISPacket.str2bArray( savrd.getShipname() );
        _shiptype = savrd.getShiptype();
        _callsign = AISPacket.str2bArray( savrd.getCallsign() );
        _destination = AISPacket.str2bArray( savrd.getDestination() );
        _draught = savrd.getDraught();
        _eta = savrd.getETA();
        _epfd = savrd.getEpfd();
        _day = savrd.getDay();
        _month = savrd.getMonth();
        _hour = savrd.getHour();
        _minute = savrd.getMinute();
        _repeat = savrd.getRepeat();
        _toBow = savrd.getToBow();
        _toPort = savrd.getToPort();
        _toStarboard = savrd.getToStarboard();
        _toStern = savrd.getToStern();
        _version = savrd.getVersion();
    }

    /**
     *
     * @param prb
     */
    public void addUpdatePositionReport( PositionReportBase prb ) {
        _timeReceived = prb.getTimeReceived();
        _course = prb.getCourse();
        _heading = prb.getHeading();
        _lat = prb.getLat();
        _lon = prb.getLon();
        _maneuver = prb.getManeuver();
        _radio = prb.getRadio();
        _repeat = prb.getRepeat();
        _second = prb.getSecond();
        _speed = prb.getSpeed();
        _status = prb.getStatus();
        _turn = prb.getTurn();
        _accurate = prb.isAccurate();
        _raim = prb.isRaim();
    }

    /**
     *
     * @param savrd
     */
    public void addUpdateStaticReport( StaticAndVoyageRelatedData savrd ) {
        _imo = savrd.getImo();
        _shipname = AISPacket.str2bArray( savrd.getShipname() );
        _shiptype = savrd.getShiptype();
        _callsign = AISPacket.str2bArray( savrd.getCallsign() );
        _destination = AISPacket.str2bArray( savrd.getDestination() );
        _draught = savrd.getDraught();
        _eta = savrd.getETA();
        _epfd = savrd.getEpfd();
        _day = savrd.getDay();
        _month = savrd.getMonth();
        _hour = savrd.getHour();
        _minute = savrd.getMinute();
        _repeat = savrd.getRepeat();
        _toBow = savrd.getToBow();
        _toPort = savrd.getToPort();
        _toStarboard = savrd.getToStarboard();
        _toStern = savrd.getToStern();
        _version = savrd.getVersion();
    }

    /**
     * Performs a deep copy of the current object
     *
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Vessel clone() throws CloneNotSupportedException {
        Vessel clone = ( Vessel )super.clone();

        clone._id = _id;
        clone._accurate = _accurate;
        clone._callsign = _callsign;
        clone._course = _course;
        clone._day = _day;
        clone._destination = _destination;
        clone._draught = _draught;
        clone._dte = _dte;
        clone._epfd = _epfd;
        clone._eta = _eta;
        clone._heading = _heading;
        clone._hour = _hour;
        clone._imo = _imo;
        clone._lat = _lat;
        clone._lon = _lon;
        clone._maneuver = _maneuver;
        clone._minute = _minute;
        clone._month = _month;
        clone._radio = _radio;
        clone._raim = _raim;
        clone._repeat = _repeat;
        clone._second = _second;
        clone._shipname = _shipname;
        clone._shiptype = _shiptype;
        clone._speed = _speed;
        clone._status = _status;
        clone._timeReceived = _timeReceived;
        clone._toBow = _toBow;
        clone._toPort = _toPort;
        clone._toStarboard = _toStarboard;
        clone._toStern = _toStern;
        clone._turn = _turn;
        clone._version = _version;

        return clone;
    }
    
    /**
     * 
     * @return 
     */
    public Identifier getId() {
        return _id;
    }

    /**
     *
     * @return
     */
    public int getVersion() {
        return _version;
    }

    /**
     *
     * @return
     */
    public int getImo() {
        return _imo;
    }

    /**
     *
     * @return
     */
    public String getCallsign() {
        if( _callsign == null ) {
            return null;
        }
        
        return AISPacket.bArray2Str( _callsign );
    }

    /**
     *
     * @return
     */
    public String getShipname() {
        if( _shipname == null ) {
            return null;
        }
        
        return AISPacket.bArray2Str( _shipname );
    }

    /**
     *
     * @return
     */
    public ShipType getShiptype() {
        return _shiptype;
    }

    /**
     *
     * @return
     */
    public int getToBow() {
        return _toBow;
    }

    /**
     *
     * @return
     */
    public int getToStern() {
        return _toStern;
    }

    /**
     *
     * @return
     */
    public int getToPort() {
        return _toPort;
    }

    /**
     *
     * @return
     */
    public int getToStarboard() {
        return _toStarboard;
    }

    /**
     *
     * @return
     */
    public EPFDFixType getEpfd() {
        return _epfd;
    }

    /**
     *
     * @return
     */
    public int getMonth() {
        return _month;
    }

    /**
     *
     * @return
     */
    public int getDay() {
        return _day;
    }

    /**
     *
     * @return
     */
    public int getHour() {
        return _hour;
    }

    /**
     *
     * @return
     */
    public int getMinute() {
        return _minute;
    }

    /**
     *
     * @return
     */
    public float getDraught() {
        return _draught;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        if( _destination == null ) {
            return null;
        }
        return AISPacket.bArray2Str( _destination );
    }

    /**
     *
     * @return
     */
    public boolean isDte() {
        return _dte;
    }

    /**
     *
     * @return
     */
    public NavigationStatus getStatus() {
        return _status;
    }

    /**
     *
     * @return
     */
    public float getTurn() {
        return _turn;
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
        return _speed;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed( float speed ) {
        _speed = speed;
    }

    /**
     *
     * @return
     */
    public boolean isAccurate() {
        return _accurate;
    }

    /**
     *
     * @return
     */
    public float getLon() {
        return _lon;
    }

    /**
     * work
     *
     * @param lon
     */
    public void setLon( float lon ) {
        _lon = lon;
    }

    /**
     *
     * @return
     */
    public float getLat() {
        return _lat;
    }

    /**
     *
     * @param lat
     */
    public void setLat( float lat ) {
        _lat = lat;
    }

    /**
     *
     * @return
     */
    public float getCourse() {
        return _course;
    }

    /**
     *
     * @param course
     */
    public void setCourse( float course ) {
        _course = course;
    }

    /**
     *
     * @return
     */
    public int getHeading() {
        return _heading;
    }

    /**
     *
     * @return
     */
    public int getSecond() {
        return _second;
    }

    /**
     *
     * @return
     */
    public ManeuverType getManeuver() {
        return _maneuver;
    }

    /**
     *
     * @return
     */
    public boolean isRaim() {
        return _raim;
    }

    /**
     *
     * @return
     */
    public int getRepeat() {
        return _repeat;
    }

    /**
     *
     * @return
     */
    public int getRadio() {
        return _radio;
    }

    /**
     *
     * @return
     */
    public MutableDateTime getEta() {
        return _eta;
    }

    /**
     *
     * @return
     */
    public DateTime getTimeReceived() {
        return _timeReceived;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode( _id );
        return hash;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Vessel other = (Vessel) obj;

        if (!_id.equals(other.getId())) {
            return false;
        }
        if (_version != other.getVersion()) {
            return false;
        }
        if (_imo != other.getImo()) {
            return false;
        }
        if (_shiptype != other.getShiptype()) {
            return false;
        }
        if (_toBow != other.getToBow()) {
            return false;
        }
        if (_toStern != other.getToStern()) {
            return false;
        }
        if (_toPort != other.getToPort()) {
            return false;
        }
        if (_toStarboard != other.getToStarboard()) {
            return false;
        }
        if (_epfd != other.getEpfd()) {
            return false;
        }
        if (_month != other.getMonth()) {
            return false;
        }
        if (_day != other.getDay()) {
            return false;
        }
        if (_hour != other.getHour()) {
            return false;
        }
        if (_minute != other.getMinute()) {
            return false;
        }
        if (Float.floatToIntBits(_draught) != Float.floatToIntBits(other.getDraught())) {
            return false;
        }
        if (_dte != other.isDte()) {
            return false;
        }
        if (_status != other.getStatus()) {
            return false;
        }
        if (Float.floatToIntBits(_turn) != Float.floatToIntBits(other.getTurn())) {
            return false;
        }
        if (Float.floatToIntBits(_speed) != Float.floatToIntBits(other.getSpeed())) {
            return false;
        }
        if (_accurate != other.isAccurate()) {
            return false;
        }
        if (Float.floatToIntBits(_lon) != Float.floatToIntBits(other.getLon())) {
            return false;
        }
        if (Float.floatToIntBits(_lat) != Float.floatToIntBits(other.getLat())) {
            return false;
        }
        if (Float.floatToIntBits(_course) != Float.floatToIntBits(other.getCourse())) {
            return false;
        }
        if (_heading != other.getHeading()) {
            return false;
        }
        if (_second != other.getSecond()) {
            return false;
        }
        if (_maneuver != other.getManeuver()) {
            return false;
        }
        if (_raim != other.isRaim()) {
            return false;
        }
        if (_repeat != other.getRepeat()) {
            return false;
        }
        if (_radio != other.getRadio()) {
            return false;
        }
        if (!Objects.equals(getCallsign(), other.getCallsign())) {
            return false;
        }
        if (!Objects.equals(getShipname(), other.getShipname())) {
            return false;
        }
        if (!Objects.equals(getDestination(), other.getDestination())) {
            return false;
        }
        return Objects.equals(getEta(), other.getEta()) && Objects.equals(_timeReceived, other.getTimeReceived());
    }
    
    /**
     * 
     */
    public static class Identifier {
        
        private final int _mmsi;
        private final String _source;
        
        /**
         * 
         * @param mmsi
         * @param source 
         */
        public Identifier( int mmsi, String source ) {
            _mmsi = mmsi;
            _source = source;
        }

        /**
         *
         * @return
         */
        public int getMmsi() {
            return _mmsi;
        }

        /**
         *
         * @return
         */
        public String getSource() {
            return _source;
        }

        /**
         *
         * @param o
         * @return
         */
        @Override
        public boolean equals( Object o ) {
            if( o == null ) return false;
            if( !( o instanceof Identifier ) ) return false;
            Identifier id = ( Identifier )o;
            return ( id.getMmsi() == _mmsi && id.getSource().equals( _source ) );
        }

        /**
         * 
         * @return 
         */
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + this._mmsi;
            return hash;
        }        
    }
}
