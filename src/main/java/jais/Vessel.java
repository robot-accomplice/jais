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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An aggregate representation of a vessel based on PositionReport and StaticAndVoyageRelatedData messages
 * 
 * @author Jonathan Machen
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
    private EPFDFixType _epfd = EPFDFixType.UNDEFINED;
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
    private long _currentMessageTimestamp;
    private long _currentPositionTimestamp;
    private long _previousPositionTimestamp;
    private long _timeSent;

    /**
     * Creates a new Vessel object based on a StandardClassBCSPositionReport
     *
     * @param report a valid StandardClassBCSPositionReport
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
        _timeSent = report.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "New Vessel from StandardClassBCSPositionReport:\n{}", toString() );
        }
    }

    /**
     * Creates a new Vessel object based on an ExtendedClassBCSPositionReport
     *
     * @param report a valid ExtendedClassBCSPositionReport
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
        _timeSent = report.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "New Vessel from ExtendedClassBCSPositionReport:\n{}", toString() );
        }
    }

    /**
     * Creates a new Vessel object based on a concrete class that extends PositionReportBase
     *
     * @param <T> a concrete class that extends PositionReportBase
     * @param report an instance of a concrete class that extends PositionReportBase 
     */
    public <T extends PositionReportBase> Vessel( T report ) {
        _id = new Identifier( report.getMmsi(), report.getSource() );
        _currentMessageTimestamp = report.getTimeReceived();
        _currentPositionTimestamp = report.getTimeReceived();
        _course = report.getCourse();
        _heading = report.getHeading();
        _lat = report.getLat();
        _lon = report.getLon();
        _maneuver = report.getManeuver();
        _radio = report.getRadio();
        _repeat = report.getRepeat();
        _second = report.getSecond();
        _speed = report.getSpeed();
        _status = report.getStatus();
        _turn = report.getTurn();
        _accurate = report.isAccurate();
        _raim = report.isRaim();
        _timeSent = report.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "New Vessel from PositionReportBase:\n{}", toString() );
        }
    }

    /**
     *  Creates a new Vessel object based on a StaticAndVoyageRelatedData object
     * 
     * @param savrd
     */
    public Vessel( StaticAndVoyageRelatedData savrd ) {
        _id = new Identifier( savrd.getMmsi(), savrd.getSource() );
        _currentMessageTimestamp = savrd.getTimeReceived();
        _imo = savrd.getImo();
        _shiptype = savrd.getShiptype();
        _shipname = ( savrd.getShipname() == null ) ? null : AISPacket.str2bArray( savrd.getShipname() );
        _callsign = ( savrd.getCallsign() == null ) ? null : AISPacket.str2bArray( savrd.getCallsign() );
        _destination = ( savrd.getDestination() == null ) ? null : AISPacket.str2bArray( savrd.getDestination() );
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
        _timeSent = savrd.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "New Vessel from StaticAndVoyageRelatedData:\n{}", toString() );
        }
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within a concrete extension of PositionReportBase
     * 
     * @param <T> a concrete class that extends PositionReportBase
     * @param report an instance of a concrete class that extends PositionReportBase 
     */
    public <T extends PositionReportBase> void addUpdatePositionReport( T report ) {
        _currentMessageTimestamp = report.getTimeReceived();
        _previousPositionTimestamp = _currentMessageTimestamp;
        _currentPositionTimestamp = report.getTimeReceived();
        _course = report.getCourse();
        _heading = report.getHeading();
        _lat = report.getLat();
        _lon = report.getLon();
        _maneuver = report.getManeuver();
        _radio = report.getRadio();
        _repeat = report.getRepeat();
        _second = report.getSecond();
        _speed = report.getSpeed();
        _status = report.getStatus();
        _turn = report.getTurn();
        _accurate = report.isAccurate();
        _raim = report.isRaim();
        _timeSent = report.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "Vessel updated with PositionReportBase:\n{}", toString() );
        }
    }

    /**
     * Updates the current instance of Vessel based on the fields contained within an instance of StaticAndVoyageRelatedData
     *
     * @param savrd
     */
    public void addUpdateStaticReport( StaticAndVoyageRelatedData savrd ) {
        _imo = savrd.getImo();
        _shiptype = savrd.getShiptype();
        _shipname = ( savrd.getShipname() == null ) ? null : AISPacket.str2bArray( savrd.getShipname() );
        _callsign = ( savrd.getCallsign() == null ) ? null : AISPacket.str2bArray( savrd.getCallsign() );
        _destination = ( savrd.getDestination() == null ) ? null : AISPacket.str2bArray( savrd.getDestination() );
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
        _timeSent = savrd.getTimeSent();
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "Vessel updated with StaticAndVoyageRelatedData:\n{}", toString() );
        }
    }

    /**
     * Performs a deep copy of the current Vessel object
     *
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Vessel clone() throws CloneNotSupportedException {
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "Cloning Vessel:\n{}", toString() );
        }
        
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
        clone._timeSent = _timeSent;

        return clone;
    }

    /**
     * 
     * @return Vessel.Identifier for this instance of Vessel
     */
    public Identifier getId() {
        return _id;
    }

    /**
     * @return the version value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getVersion() {
        return _version;
    }

    /**
     * @return the IMO value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getImo() {
        return _imo;
    }

    /**
     * @return the callsign value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public String getCallsign() {
        return ( _callsign == null ) ? null : AISPacket.bArray2Str( _callsign );
    }

    /**
     * @return the ship name value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public String getShipname() {
        return ( _shipname == null ) ? null : AISPacket.bArray2Str( _shipname );
    }

    /**
     * @return the destination value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public String getDestination() {
        return ( _destination == null ) ? null : AISPacket.bArray2Str( _destination );
    }

    /**
     * @return the ship type value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public ShipType getShiptype() {
        return _shiptype;
    }

    /**
     * @return the toBow value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getToBow() {
        return _toBow;
    }

    /**
     * @return the toStern value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getToStern() {
        return _toStern;
    }

    /**
     * @return the toPort value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getToPort() {
        return _toPort;
    }

    /**
     * @return the toStarboard value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getToStarboard() {
        return _toStarboard;
    }

    /**
     * @return the EPFD value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public EPFDFixType getEpfd() {
        return _epfd;
    }

    /**
     * @return the month value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getMonth() {
        return _month;
    }

    /**
     * @return the day value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getDay() {
        return _day;
    }

    /**
     * @return the hour value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getHour() {
        return _hour;
    }

    /**
     * @return the minute value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public int getMinute() {
        return _minute;
    }

    /**
     * @return the draught value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public float getDraught() {
        return _draught;
    }

    /**
     * @return the DTE value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public boolean isDte() {
        return _dte;
    }

    /**
     * @return the IMO value of whatever StaticAndVoyageRelatedData message may have been used to populate this object
     */
    public NavigationStatus getStatus() {
        return _status;
    }

    /**
     * @return the turn value of whatever PositionReport message may have been used to populate this object
     */
    public float getTurn() {
        return _turn;
    }

    /**
     * @return the speed value of whatever PositionReport message may have been used to populate this object
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
     * @return the accuracy value of whatever PositionReport message may have been used to populate this object
     */
    public boolean isAccurate() {
        return _accurate;
    }

    /**
     * @return the longitude value of whatever PositionReport message may have been used to populate this object
     */
    public float getLon() {
        return _lon;
    }

    /**
     * @param lon
     */
    public void setLon( float lon ) {
        _lon = lon;
    }

    /**
     * @return the latitude value of whatever PositionReport message may have been used to populate this object
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
     * @return the course value of whatever PositionReport message may have been used to populate this object
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
     * @return the heading value of whatever PositionReport message may have been used to populate this object
     */
    public int getHeading() {
        return _heading;
    }

    /**
     * @return the second value of whatever PositionReport message may have been used to populate this object
     */
    public int getSecond() {
        return _second;
    }

    /**
     * @return the maneuver value of whatever PositionReport message may have been used to populate this object
     */
    public ManeuverType getManeuver() {
        return _maneuver;
    }

    /**
     * @return the RAIM value of whatever PositionReport message may have been used to populate this object
     */
    public boolean isRaim() {
        return _raim;
    }

    /**
     * @return the repeat value of whatever PositionReport message may have been used to populate this object
     */
    public int getRepeat() {
        return _repeat;
    }

    /**
     * @return the radio value of whatever PositionReport message may have been used to populate this object
     */
    public int getRadio() {
        return _radio;
    }

    /**
     * @return the ETA value of whatever PositionReport message may have been used to populate this object
     */
    public ZonedDateTime getEta() {
        return _eta;
    }

    /**
     * @return the timestamp of the most recent update to this Vessel object
     */
    public long getCurrentMessageTimestamp() {
        return _currentMessageTimestamp;
    }

    /**
     * @return the timestamp of the most recent position update for this Vessel object
     */
    public long getCurrentPositionTimestamp() {
        return _currentPositionTimestamp;
    }

    /**
     * @return the timestamp of the most recent position update prior to the most current position update for this Vessel object
     */
    public long getPreviousPositionTimestamp() {
        return _previousPositionTimestamp;
    }
    
    /**
     * 
     * @return 
     */
    public long getTimeSent() {
        return _timeSent;
    }

    /**
     * @return the hash code for this Object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + _id.hashCode();
        return hash;
    }

    /**
     * @param obj
     * @return a boolean representing whether the provided object matches this object
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

        return ( _id.equals( other.getId() ) );
    }
    
    /**
     * A local implementation of toString() which encapsulates all of the object member fields in an approximate JSON format
     * @return 
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "{ " );
        sb.append( "id : { mmsi : " ).append( getId().getMmsi() ).append( ", source : \"" ).append( getId().getSource() ).append( " }" );
        sb.append( ", version : " ).append( getVersion() );
        sb.append( ", imo : " ).append( getImo() );
        sb.append( ", callsign : \"" ).append( getCallsign() ).append( "\"" );
        sb.append( ", shipname : \"" ).append( getShipname() ).append( "\"" );
        sb.append( ", shiptype : \"" ).append( getShiptype() ).append( "\"" );
        sb.append( ", toBow : " ).append( getToBow() );
        sb.append( ", toPort : " ).append( getToPort() );
        sb.append( ", toStern : " ).append( getToStern() );
        sb.append( ", toStarboard : " ).append( getToStarboard() );
        sb.append( ", epfd : " ).append( getEpfd() );
        sb.append( ", month : " ).append( getMonth() );
        sb.append( ", day : " ).append( getDay() );
        sb.append( ", hour : " ).append( getHour() );
        sb.append( ", minute : " ).append( getMinute() );
        sb.append( ", second : " ).append( getSecond() );
        sb.append( ", draught : " ).append( getDraught() );
        sb.append( ", destination : " ).append( getDestination() );
        sb.append( ", dte : " ).append( isDte() );
        sb.append( ", status : " ).append( getStatus() );
        sb.append( ", turn : " ).append( getTurn() );
        sb.append( ", speed : " ).append( getSpeed() );
        sb.append( ", accurate : " ).append( isAccurate() );
        sb.append( ", lon : " ).append( getLon() );
        sb.append( ", lat : " ).append( getLat() );
        sb.append( ", course : " ).append( getCourse() );
        sb.append( ", heading : " ).append( getHeading() );
        sb.append( ", maneuver : " ).append( getManeuver() );
        sb.append( ", raim : " ).append( isRaim() );
        sb.append( ", repeat : " ).append( getRepeat() );
        sb.append( ", radio : " ).append( getRadio() );
        sb.append( ", eta : " ).append( getEta() );
        sb.append( ", currentMessageTimestamp : " ).append( getCurrentMessageTimestamp() );
        sb.append( ", currentPositionTimestamp : " ).append( getCurrentPositionTimestamp() );
        sb.append( ", previousPositionTimestamp : " ).append( getPreviousPositionTimestamp() );
        sb.append( ", timeSent : " ).append( _timeSent );
        sb.append( "}" );
        
        return sb.toString();
    }

    /**
     * A unique vessel identifier based on a combination of the vessel MMSI and the provided data source
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
            Identifier id = ( Identifier )o;
            if( id._mmsi != _mmsi ) return false;
            return id._source.equals( _source );
        }

        /**
         *
         * @return
         */
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + _mmsi;
            hash = 67 * hash + _source.hashCode();
            return hash;
        }
    }
}
