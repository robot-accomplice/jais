/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
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
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import java.util.BitSet;
import org.slf4j.*;

/**
 *
 * @author Jonathan Machen
 */
public class SingleSlotBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LoggerFactory.getLogger( SingleSlotBinaryMessage.class );

    private boolean _addressed;
    private boolean _structured;
    private int _destMmsi;
    private int _dac;
    private int _fid;
    private BitSet _data;

    /**
     *
     * @param source
     * @param packets
     */
    public SingleSlotBinaryMessage( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public SingleSlotBinaryMessage( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
    }

    /**
     *
     * @return
     */
    public boolean isAddressed() {
        return _addressed;
    }

    /**
     *
     * @return
     */
    public boolean isStructured() {
        return _structured;
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
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( SingleSlotBinaryMessageFieldMap field : SingleSlotBinaryMessageFieldMap.values() ) {
            switch( field ) {
                case ADDRESSED:
                    _addressed = _bits.get( field.getStartBit() );
                    break;
                case STRUCTURED:
                    _structured = _bits.get( field.getStartBit() );
                    break;
                case DESTINATION_MMSI:
                    if( _addressed ) {
                        _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case DAC:
                    if( _structured ) {
                        _dac = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case FID:
                    if( _structured ) {
                        _fid = AISMessageDecoder.decodeUnsignedInt( _bits,
                                field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case DATA:
                    if( _addressed ) {
                        _data = new BitSet( _bits.size() - 70 );
                        _data = _bits.get( 70, 70 );
                    } else if( _structured ) {
                        _data = _bits.get( 56, _bits.size() - 56 );
                    } else {
                        _data = _bits.get( 40, _bits.size() - 40 );
                    }
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum SingleSlotBinaryMessageFieldMap implements FieldMap {

        ADDRESSED( 38, 38 ),
        STRUCTURED( 39, 39 ),
        DESTINATION_MMSI( 40, 70 ), // as many as 30 bits
        DAC( 40, 59 ), // as many as 10 bits
        FID( 50, 55 ), // as many as 6 bits
        DATA( 40, -1 );             // as many as 128 bits

//      from gpsd.berlios.de/AIVDM.html#_type_25_single_slot_binary_message
//      If the addressed flag is on, 30 bits of data at offset 40 are interpreted 
//      as a destination MMSI. Otherwise that field span becomes part of the 
//      message payload, with the first 16 bits used as an Application ID if 
//      the structured flag is on.  If the structured flag is on, a 16-bit 
//      application identifier is extracted; this field is to be interpreted 
//      as a 10 bit DAC and 6-bit FID as in message types 6 and 8. Otherwise 
//      that field span becomes part of the message payload.
//      The data fields are not, in contrast to message type 26, followed by a 
//      radio status block. Note: Type 25 is extremely rare. As of April 2011 it 
//      has not been observed even in long-duration samples from AISHub.        
        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        SingleSlotBinaryMessageFieldMap( int startBit, int endBit ) {
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
