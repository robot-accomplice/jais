/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import com.spatial4j.core.shape.Point;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class StandardSARAircraftPositionReport extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( StandardSARAircraftPositionReport.class );

    private int _alt;
    private int _speed;
    private boolean _accurate;
    private int _lon;
    private int _lat;
    private float _course;
    private int _second;
    private boolean _dte;
    private boolean _assigned;
    private boolean _raim;
    private int _radio;

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public StandardSARAircraftPositionReport( AISPacket... packets )
            throws AISException {
        super( packets );
    }

    /**
     *
     * @param messageType
     * @param packets
     */
    public StandardSARAircraftPositionReport( AISMessageType messageType,
            AISPacket... packets ) {
        super( messageType, packets );
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
            _position = CTX.makePoint( _lat, _lon );
        }

        return _position;
    }

    /**
     *
     * @return
     */
    public int getAlt() {
        return _alt;
    }

    /**
     *
     * @return
     */
    public int getSpeed() {
        return _speed;
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
    public int getLon() {
        return _lon;
    }

    /**
     *
     * @return
     */
    public int getLat() {
        return _lat;
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
     * @return
     */
    public int getSecond() {
        return _second;
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
    public boolean isAssigned() {
        return _assigned;
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
    public int getRadio() {
        return _radio;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( SSARAircraftPositionReportFieldMap field
                : SSARAircraftPositionReportFieldMap.values() ) {
            switch( field ) {
                case ALT:
                    _alt = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SPEED:
                    _speed = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case ACCURACY:
                    _accurate = _bits.get( field.getStartBit() );
                    break;
                case LON:
                    _lon = AISMessageDecoder.decodeSignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case LAT:
                    _lat = AISMessageDecoder.decodeSignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case COURSE:
                    _course = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SECOND:
                    _second = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DTE:
                    _dte = _bits.get( field.getStartBit() );
                    break;
                case ASSIGNED:
                    _assigned = _bits.get( field.getStartBit() );
                    break;
                case RAIM:
                    _raim = _bits.get( field.getStartBit() );
                    break;
                case RADIO:
                    _radio = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }

    /**
     *
     */
    private enum SSARAircraftPositionReportFieldMap implements FieldMap {

        ALT( 38, 49 ),
        SPEED( 50, 59 ),
        ACCURACY( 60, 60 ),
        LON( 61, 88 ),
        LAT( 89, 115 ),
        COURSE( 116, 127 ),
        SECOND( 128, 133 ),
        REGIONAL( 134, 141 ), // reserved
        DTE( 142, 142 ),
        SPARE( 143, 145 ),
        ASSIGNED( 146, 146 ),
        RAIM( 147, 147 ),
        RADIO( 148, 167 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit '
         */
        SSARAircraftPositionReportFieldMap( int startBit, int endBit ) {
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
