/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jais.messages.binaryaddressed;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.AISMessageDecoder;
import jais.messages.BinaryAddressedMessageBase;
import jais.messages.enums.FieldMap;
import jais.messages.enums.BinaryAddressedMessageType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class IMO289TextDescription extends BinaryAddressedMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( IMO289TextDescription.class );

    private int _linkageId;
    private String _description;

    /**
     *
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public IMO289TextDescription( AISPacket... packets )
            throws AISException {
        super( BinaryAddressedMessageType.TEXT_DESCRIPTION, packets );
    }

    /**
     *
     * @return
     */
    public int getLinkageId() {
        return _linkageId;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return _description;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( IMO289TextDescriptionFieldMap field
                : IMO289TextDescriptionFieldMap.values() ) {
            switch( field ) {
                case LINKAGE_ID:
                    _linkageId = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case DESCRIPTION:
                    _description = AISMessageDecoder.decodeString( _bits,
                            field.getStartBit(), _bits.size() - 1 );
                    break;
                default:
                    LOG.warn( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum IMO289TextDescriptionFieldMap implements FieldMap {

        LINKAGE_ID( 88, 97 ),
        DESCRIPTION( 98, -1 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private IMO289TextDescriptionFieldMap( int startBit, int endBit ) {
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
