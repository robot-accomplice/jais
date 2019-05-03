/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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

package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.BinaryAddressedMessageType;
import jais.messages.enums.FieldMap;
import java.lang.reflect.Constructor;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public abstract class BinaryAddressedMessageBase extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( BinaryAddressedMessageBase.class );

    private int _seqno;
    private int _destMmsi;
    private boolean _retransmit;
    private int _dac; // designated area code
    private int _fid; // functional id
    private BitSet _data;
    private BinaryAddressedMessageType _subType;

    /**
     *
     * @param source
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public BinaryAddressedMessageBase( String source, AISPacket... packets ) throws AISException {
        super( source, AISMessageType.BINARY_ADDRESSED_MESSAGE, packets );
    }

    /**
     *
     * @param source
     * @param subType
     * @param packets
     * @throws jais.exceptions.AISException
     */
    public BinaryAddressedMessageBase( String source, BinaryAddressedMessageType subType, AISPacket... packets )
            throws AISException {
        this( source, packets );
        _subType = subType;
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
    public static Logger getLOG() {
        return LOG;
    }

    /**
     *
     * @return
     */
    public int getSeqno() {
        return _seqno;
    }

    /**
     *
     * @return
     */
    public int getDestMmsi() {
        return _destMmsi;
    }

    /**
     *
     * @return
     */
    public boolean isRetransmit() {
        return _retransmit;
    }

    /**
     *
     * @return
     */
    public int getDac() {
        return _dac;
    }

    /**
     *
     * @return
     */
    public int getFid() {
        return _fid;
    }

    /**
     *
     * @return
     */
    public BitSet getData() {
        return _data;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasSubType() {
        return true;
    }

    /**
     *
     * @return
     */
    public BinaryAddressedMessageType getSubType() {
        if( _subType == null ) {
            _subType = BinaryAddressedMessageType.fetch( _dac, _fid, _bits.size() );
        }

        return _subType;
    }

    /**
     *
     * @param subType
     */
    public void setSubType( BinaryAddressedMessageType subType ) {
        _subType = subType;
    }

    /**
     *
     * @return @throws jais.exceptions.AISException
     */
    @Override
    public BinaryAddressedMessageBase getSubTypeInstance() throws AISException {
        BinaryAddressedMessageBase message;

        if( _subType == null ) {
            decode(); // we need the dac and fid
            getSubType();
        }

        if( _subType != null ) {
            try {
                if( LOG.isDebugEnabled() ) LOG.debug( "Creating a new {} instance.", _subType.getDescription() );

                Constructor con = _subType.getMsgClass().getDeclaredConstructor( AISPacket[].class );
                con.setAccessible( true );

                if( _packets.length == 1 ) {
                    message = ( BinaryAddressedMessageBase ) con.newInstance( ( Object ) _packets );
                } else {
                    message = ( BinaryAddressedMessageBase ) con.newInstance( new Object[]{_packets} );
                }

                message.setType( super.getType() );
                message.setSubType( _subType );
            } catch( ReflectiveOperationException roe ) {
                throw new AISException( "Reflection failure: " + roe.getMessage(), roe );
            } catch( SecurityException se ) {
                throw new AISException( "SecurityException: " + se.getMessage(), se );
            }
        } else {
            throw new AISException( "Unable to determine message subtype for DAC: " + _dac + " and FID: " + _fid );
        }

        return message;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public void decode() throws AISException {
        super.decode();

        for( BinaryAddressedMessageFieldMap field : BinaryAddressedMessageFieldMap.values() ) {
            switch( field ) {
                case SEQUENCE_NUMBER:
                    if( _bits.size() >= field.getStartBit() )
                        _seqno = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case DESTINATION_MMSI:
                    if( _bits.size() >= field.getStartBit() )
                        _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case RETRANSMIT:
                    if( _bits.size() >= field.getStartBit() )
                        _retransmit = _bits.get( field.getStartBit() );
                    break;
                case DAC:
                    if( _bits.size() >= field.getStartBit() )
                        _dac = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case FID:
                    if( _bits.size() >= field.getStartBit() )
                        _fid = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }

    /**
     *
     */
    private enum BinaryAddressedMessageFieldMap implements FieldMap {

        SEQUENCE_NUMBER( 38, 39 ),
        DESTINATION_MMSI( 40, 69 ),
        RETRANSMIT( 70, 70 ),
        SPARE( 71, 71 ),
        DAC( 72, 81 ), // designated area code
        FID( 82, 87 ), // Functional ID
        DATA( 88, -1 ) // -1 means from startBit to end of bitArray
        ;

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        BinaryAddressedMessageFieldMap( int startBit, int endBit ) {
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
