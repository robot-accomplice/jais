/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AssignmentModeCommand extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(
            AssignmentModeCommand.class );

    private int _mmsi1;
    private int _offset1;
    private int _increment1;
    private int _mmsi2;
    private int _offset2;
    private int _increment2;

    /**
     *
     * @param packets
     */
    public AssignmentModeCommand( AISPacket... packets ) {
        super( packets );
    }

    /**
     *
     * @param type
     * @param packets
     */
    public AssignmentModeCommand( AISMessageType type, AISPacket... packets ) {
        super( type, packets );
    }

    /**
     *
     * @return
     */
    public int getSourceMmsi() {
        return super.getMmsi();
    }

    /**
     *
     * @return
     */
    public int getMmsi1() {
        return _mmsi1;
    }

    /**
     *
     * @return
     */
    public int getOffset1() {
        return _offset1;
    }

    /**
     *
     * @return
     */
    public int getIncrement1() {
        return _increment1;
    }

    /**
     *
     * @return
     */
    public int getMmsi2() {
        return _mmsi2;
    }

    /**
     *
     * @return
     */
    public int getOffset2() {
        return _offset2;
    }

    /**
     *
     * @return
     */
    public int getIncrement2() {
        return _increment2;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( AssignmentModeCommandFieldMap field : AssignmentModeCommandFieldMap.values() ) {
            switch( field ) {
                case MMSI1:
                    _mmsi1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET1:
                    _offset1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT1:
                    _increment1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI2:
                    _mmsi2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET2:
                    _offset2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT2:
                    _increment2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum AssignmentModeCommandFieldMap implements FieldMap {

        SPARE( 38, 39 ),
        MMSI1( 40, 69 ),
        OFFSET1( 70, 81 ),
        INCREMENT1( 82, 91 ),
        MMSI2( 92, 121 ),
        OFFSET2( 122, 133 ),
        INCREMENT2( 134, 143 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private AssignmentModeCommandFieldMap( int startBit, int endBit ) {
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
