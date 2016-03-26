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
import com.spatial4j.core.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machenathan Machen
 */
public class BaseStationReport extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( BaseStationReport.class );

    private int _year;
    private int _month;
    private int _day;
    private int _hour;
    private int _minute;
    private int _second;
    private boolean _accurate;
    private float _lon;
    private float _lat;
    private EPFDFixType _epfd;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param packets
     */
    public BaseStationReport( AISPacket... packets ) {
        super( packets );
    }

    /**
     *
     * @param type
     * @param packets
     */
    public BaseStationReport( AISMessageType type, AISPacket... packets ) {
        super( type, packets );
    }

    /**
     *
     * @return
     */
    public int getYear() {
        return _year;
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
    public int getSecond() {
        return _second;
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
     *
     * @return
     */
    public float getLat() {
        return _lat;
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
    public boolean usingRaim() {
        return _raim;
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
    @Override
    public boolean hasPosition() {
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public Point getPosition() {
        if( _position == null ) {
            _position = CTX.makePoint( _lon, _lat );
        }

        return _position;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( BaseReportFieldMap field : BaseReportFieldMap.values() ) {

            switch( field ) {
                case YEAR:
                    _year = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MONTH:
                    _month = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DAY:
                    _day = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case HOUR:
                    _hour = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                case MINUTE:
                    _minute = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SECOND:
                    _second = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ACCURACY:
                    _accurate = _bits.get( field.getStartBit() );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeLongitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeLatitude( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case EPFD:
                    int epfdCode = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    _epfd = EPFDFixType.getForCode( epfdCode );
                    break;
                case RAIM:
                    _raim = _bits.get( field.getStartBit() );
                    break;
                case RADIO:
                    _radio = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    LOG.debug( "Encountered unhandled field type of : {}",
                            field );
            }
        }
    }

    /**
     *
     */
    private enum BaseReportFieldMap implements FieldMap {

        YEAR( 38, 51 ),
        MONTH( 52, 55 ),
        DAY( 56, 60 ),
        HOUR( 61, 65 ),
        MINUTE( 66, 71 ),
        SECOND( 72, 77 ),
        ACCURACY( 78, 78 ),
        LON( 79, 106 ),
        LAT( 107, 133 ),
        EPFD( 134, 137 ),
        SPARE( 138, 147 ),
        RAIM( 148, 148 ),
        RADIO( 149, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private BaseReportFieldMap( int startBit, int endBit ) {
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
