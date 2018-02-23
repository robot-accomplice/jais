/*
 * Copyright 2016 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jon
 */
public class Vessel implements Cloneable {

    private final static Logger LOG = LogManager.getLogger( Vessel.class );
    private final static DateTimeFormatter ETA_FORMATTER = DateTimeFormatter.ofPattern( "yyyy/MM/dd HH:mm" ).withZone( ZoneOffset.UTC.normalized() );

    private Identifier _id;
    private int _version;
    private int _imo;
    private byte[] _callsign;
    private byte[] _shipname;
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
    private byte[] _destination;
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
    private ZonedDateTime _eta = ZonedDateTime.parse( "1970/01/01 00:00", ETA_FORMATTER );
    private ZonedDateTime _currentMessageTimestamp;
    private ZonedDateTime _currentPositionTimestamp;
    private ZonedDateTime _previousPositionTimestamp;

    /**
     *
     * @param report
     */
    public Vessel( StandardClassBCSPositionReport report ) {
        _id = new Identifier( report.getMmsi(), report.getSource() );
        _currentMessageTimestamp = report.getTimeReceived();
        _currentPositionTimestamp = report.getTimeReceived();
        _course = report.getCourse();
        _heading = report.getHeading();
        _lat = report.getLat();
        _lon = report.getLon();
        _radio = report.getRadio();
        _repeat = report.getRepeat();
        _speed = report.getSpeed();
        
        LOG.debug( this.toString() );
    }

    /**
     *
     * @param report
     */
    public Vessel( ExtendedClassBCSPositionReport report ) {
        _id = new Identifier( report.getMmsi(), report.getSource() );
        _currentMessageTimestamp = report.getTimeReceived();
        _currentPositionTimestamp = report.getTimeReceived();
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
        
        LOG.debug( this.toString() );
    }

    /**
     *
     * @param prb
     */
    public Vessel( PositionReportBase prb ) {
        _id = new Identifier( prb.getMmsi(), prb.getSource() );
        _currentMessageTimestamp = prb.getTimeReceived();
        _currentPositionTimestamp = prb.getTimeReceived();
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
        
        LOG.debug( this.toString() );
    }

    /**
     *
     * @param savrd
     */
    public Vessel( StaticAndVoyageRelatedData savrd ) {
        _id = new Identifier( savrd.getMmsi(), savrd.getSource() );
        _currentMessageTimestamp = savrd.getTimeReceived();
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
        
        LOG.debug( this.toString() );
    }

    /**
     *
     * @param prb
     */
    public void addUpdatePositionReport( PositionReportBase prb ) {
        _currentMessageTimestamp = prb.getTimeReceived();
        _previousPositionTimestamp = _currentMessageTimestamp;
        _currentPositionTimestamp = prb.getTimeReceived();
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
        
        LOG.debug( this.toString() );
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
        
        LOG.debug( this.toString() );
    }

    /**
     * Performs a deep copy of the current object
     *
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Vessel clone() throws CloneNotSupportedException {
        Vessel clone = ( Vessel ) super.clone();

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
        clone._currentMessageTimestamp = _currentMessageTimestamp;
        clone._previousPositionTimestamp = _previousPositionTimestamp;
        clone._currentPositionTimestamp = _currentPositionTimestamp;
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
        if( _callsign == null )
            return null;
        return AISPacket.bArray2Str( _callsign );
    }

    /**
     *
     * @return
     */
    public String getShipname() {
        if( _shipname == null )
            return null;
        return AISPacket.bArray2Str( _shipname );
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        if( _destination == null )
            return null;
        return AISPacket.bArray2Str( _destination );
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
    public ZonedDateTime getEta() {
        return _eta;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getCurrentMessageTimestamp() {
        return _currentMessageTimestamp;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getCurrentPositionTimestamp() {
        return _currentPositionTimestamp;
    }

    /**
     *
     * @return
     */
    public ZonedDateTime getPreviousPositionTimestamp() {
        return _previousPositionTimestamp;
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
        if( obj == null ) {
            return false;
        }
        if( getClass() != obj.getClass() ) {
            return false;
        }
        final Vessel other = ( Vessel ) obj;

        return( _id.equals( other.getId() ) );
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "{ " );
        sb.append( "id : { mmsi : " ).append( _id.getMmsi() ).append( ", source : \"" ).append( _id.getSource() ).append( " }" );
        sb.append( ", version : " ).append( _version );
        sb.append( ", imo : " ).append( _imo );
        sb.append( ", callsign : \"" ).append( _callsign ).append( "\"" );
        sb.append( ", shipname : \"" ).append( _shipname ).append( "\"" );
        sb.append( ", shiptype : \"" ).append( _shiptype ).append( "\"" );
        sb.append( ", toBow : " ).append( _toBow );
        sb.append( ", toPort : " ).append( _toPort );
        sb.append( ", toStern : " ).append( _toStern );
        sb.append( ", toStarboard : " ).append( _toStarboard );
        sb.append( ", epfd : " ).append( _epfd );
        sb.append( ", month : " ).append( _month );
        sb.append( ", day : " ).append( _day );
        sb.append( ", hour : " ).append( _hour );
        sb.append( ", minute : " ).append( _minute );
        sb.append( ", second : " ).append( _second );
        sb.append( ", draught : " ).append( _draught );
        sb.append( ", destination : " ).append( _destination );
        sb.append( ", dte : " ).append( _dte );
        sb.append( ", status : " ).append( _status );
        sb.append( ", turn : " ).append( _turn );
        sb.append( ", speed : " ).append( _speed );
        sb.append( ", accurate : " ).append( _accurate );
        sb.append( ", lon : " ).append( _lon );
        sb.append( ", lat : " ).append( _lat );
        sb.append( ", course : " ).append( _course );
        sb.append( ", heading : " ).append( _heading );
        sb.append( ", maneuver : " ).append( _maneuver );
        sb.append( ", raim : " ).append( _raim );
        sb.append( ", repeat : " ).append( _repeat );
        sb.append( ", radio : " ).append( _radio );
        sb.append( ", eta : " ).append( _eta );
        sb.append( ", currentMessageTimestamp : " ).append( _currentMessageTimestamp );
        sb.append( ", currentPositionTimestamp : " ).append( _currentPositionTimestamp );
        sb.append( ", previousPositionTimestamp : " ).append( _previousPositionTimestamp );
        sb.append( "}" );
        
        return sb.toString();
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
            if( o == null )
                return false;
            if( !( o instanceof Identifier ) )
                return false;
            Identifier id = ( Identifier ) o;
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
            hash = 67 * hash + Objects.hashCode( _source );
            return hash;
        }
    }
}
