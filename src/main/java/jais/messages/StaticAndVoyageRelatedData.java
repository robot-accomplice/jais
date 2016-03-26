/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.ShipType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class StaticAndVoyageRelatedData extends AISMessageBase {

    private final static Logger LOG
            = LogManager.getLogger( StaticAndVoyageRelatedData.class );

    public final static DateTimeFormatter ETA_FORMATTER = DateTimeFormat.forPattern( "yyyy/MM/dd HH:mm" );

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

    /**
     *
     * @param packets
     */
    public StaticAndVoyageRelatedData( AISPacket... packets ) {
        super( packets );
    }

    /**
     *
     * @param type
     * @param packets
     */
    public StaticAndVoyageRelatedData( AISMessageType type, AISPacket... packets ) {
        super( type, packets );
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
    public MutableDateTime getETA() {
        StringBuilder eta = new StringBuilder();
        MutableDateTime dt = MutableDateTime.now();
        int year = dt.getYear();
        int month = dt.getMonthOfYear();

        if( _month > 0 ) {
            // properly formatted month
            if( _month < 10 ) {
                eta.append( "0" ).append( _month );
            } else if( _month > 12 ) {
                eta.append( "12" );
                _month = 12; // use this to validate the days later
            } else {
                eta.append( _month );
            }
            eta.append( "/" );

            // assume next year
            if( _month < month ) {
                year++;
            }

            // prepend datetime string with YYYY/
            eta.insert( 0, year ).insert( 4, "/" );

            // recreate the Calendar object based on the validated month
            dt = new MutableDateTime( year, _month, 1, 0, 0, 0, 0, DateTimeZone.UTC );

            // Get the number of days in that month
            int daysInMonth = dt.monthOfYear().getMaximumValue();

            // properly formatted day
            if( _day < 1 ) {
                eta.append( "01" );
            } else if( _day < 10 ) {
                eta.append( "0" ).append( _day );
            } else if( _day >= daysInMonth ) {
                eta.append( daysInMonth );
            } else {
                eta.append( _day );
            }
            eta.append( " " );

            // properly formatted hour
            if( _hour < 1 ) {
                eta.append( "00" );
            } else if( _hour < 10 ) {
                eta.append( "0" ).append( _hour );
            } else if( _hour >= 24 ) {
                eta.append( 00 );
            } else {
                eta.append( _hour );
            }
            eta.append( ":" );

            // properly formatted minute
            if( _minute < 1 || _minute > 59 ) {
                eta.append( "00" );
            } else if( _minute < 10 ) {
                eta.append( "0" ).append( _minute );
            } else {
                eta.append( _minute );
            }
        } else {
            // default to epoch if month is invalid
            eta.append( "1970/01/01 00:00" );
        }

        try {
            dt = MutableDateTime.parse( eta.toString(), ETA_FORMATTER );
        } catch( Exception e ) {
            LOG.warn( "Invalid ETA, setting to epoch" );
            dt = MutableDateTime.parse( "1970/01/01 00:00", ETA_FORMATTER );
        }

        return dt;
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
    public boolean dteReady() {
        return !_dte;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( StaticAndVoyageFieldMap field : StaticAndVoyageFieldMap.values() ) {
            switch( field ) {
                case VERSION:
                    _version = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case IMO:
                    _imo = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case CALL_SIGN:
                    _callsign = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_NAME:
                    _shipname = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SHIP_TYPE:
                    int shipCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _shiptype = ShipType.getForCode( shipCode );
                    if( _shiptype == null ) {
                        LOG.error( "No ShipType for {}", shipCode );
                        _shiptype = ShipType.OTHER_NO_INFO;
                    }
                    break;
                case TO_BOW:
                    _toBow = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_STERN:
                    _toStern = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_PORT:
                    _toPort = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TO_STARBOARD:
                    _toStarboard = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case EPFD:
                    int epfdCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _epfd = EPFDFixType.getForCode( epfdCode );
                    break;
                case ETA_MONTH:
                    _month = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ETA_DAY:
                    _day = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ETA_HOUR:
                    _hour = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ETA_MINUTE:
                    _minute = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DRAUGHT:
                    _draught = AISMessageDecoder.decodeDraught( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DESTINATION:
                    _destination = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DTE:
                    if( field.getStartBit() < _bits.size() ) {
                        _dte = _bits.get( field.getStartBit() );
                    } else {
                        LOG.debug( "Reached end of message before we could retrieve DTE value!" );
                    }
                    break;
                default:
                    LOG.debug( "Encountered unhandled field type of : {}", field );
            }
        }
    }

    /**
     *
     */
    private enum StaticAndVoyageFieldMap implements FieldMap {

        VERSION( 38, 39 ),
        IMO( 40, 69 ),
        CALL_SIGN( 70, 111 ),
        SHIP_NAME( 112, 231 ),
        SHIP_TYPE( 232, 239 ),
        TO_BOW( 240, 248 ),
        TO_STERN( 249, 257 ),
        TO_PORT( 258, 263 ),
        TO_STARBOARD( 264, 269 ),
        EPFD( 270, 273 ),
        ETA_MONTH( 274, 277 ),
        ETA_DAY( 278, 282 ),
        ETA_HOUR( 283, 287 ),
        ETA_MINUTE( 288, 293 ),
        DRAUGHT( 294, 301 ),
        DESTINATION( 302, 421 ),
        DTE( 422, 422 ),
        SPARE( 423, 423 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private StaticAndVoyageFieldMap( int startBit, int endBit ) {
            _startBit = startBit;
            _endBit = endBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getStartBit() {
            return _startBit;
        }

        /**
         *
         * @return
         */
        @Override
        public int getEndBit() {
            return _endBit;
        }
    }
}
