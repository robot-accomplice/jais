/*
 * This software is the sole property of ShipTracks, LLC and is not 
 * licensed for redistribution, modification, or resale by any other party.
 */
package jais.messages;

import jais.messages.enums.EPFDFixType;
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import jais.messages.enums.ShipType;
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
public class CombinedPositionAndStatic {

    private final static Logger LOG = LogManager.getLogger( CombinedPositionAndStatic.class );

    private int _mmsi;
    private String _site;
    private int _version;
    private int _imo;
    private String _callsign;
    private String _shipname;
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
    private String _destination;
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
     */
    public CombinedPositionAndStatic() {
    }
    
    /**
     * 
     * @param report 
     */
    public CombinedPositionAndStatic( StandardClassBCSPositionReport report ) {
        _mmsi = report.getMmsi();
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
    public CombinedPositionAndStatic( ExtendedClassBCSPositionReport report ) {
        _mmsi = report.getMmsi();
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
        _shipname = report.getShipName();
        _shiptype = report.getShipType();
    }

    /**
     * 
     * @param prb 
     */
    public CombinedPositionAndStatic( PositionReportBase prb ) {
        _mmsi = prb.getMmsi();
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
    public CombinedPositionAndStatic( StaticAndVoyageRelatedData savrd ) {
        _mmsi = savrd.getMmsi();
        _timeReceived = savrd.getTimeReceived();
        _imo = savrd.getImo();
        _shipname = savrd.getShipname();
        _shiptype = savrd.getShiptype();
        _callsign = savrd.getCallsign();
        _destination = savrd.getDestination();
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
        _shipname = savrd.getShipname();
        _shiptype = savrd.getShiptype();
        _callsign = savrd.getCallsign();
        _destination = savrd.getDestination();
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
    public CombinedPositionAndStatic clone() throws CloneNotSupportedException {
        CombinedPositionAndStatic clone = (CombinedPositionAndStatic )super.clone();
        
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
        clone._mmsi = _mmsi;
        clone._month = _month;
        clone._radio = _radio;
        clone._raim = _raim;
        clone._repeat = _repeat;
        clone._second = _second;
        clone._shipname = _shipname;
        clone._shiptype = _shiptype;
        clone._site = _site;
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
    public int getMmsi() {
        return _mmsi;
    }
    
    /**
     * 
     * @param site 
     */
    public void setSite( String site ) {
        _site = site;
    }
    
    /**
     * 
     * @return 
     */
    public String getSite() {
        return _site;
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
        return _callsign;
    }

    /**
     * 
     * @return 
     */
    public String getShipname() {
        return _shipname;
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
        return _destination;
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
}
