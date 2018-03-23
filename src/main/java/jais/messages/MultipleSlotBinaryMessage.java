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
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.FieldMap;
import jais.messages.enums.AISMessageType;
import java.nio.charset.Charset;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class MultipleSlotBinaryMessage extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( MultipleSlotBinaryMessage.class );

    private boolean _addressed;
    private boolean _structured;
    private int _destMmsi;
    private int _dac;
    private int _fid;
    private BitSet _data;
    private int _radio;

    /**
     *
     * @param source
     * @param packets
     */
    public MultipleSlotBinaryMessage( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public MultipleSlotBinaryMessage( String source, AISMessageType type, AISPacket... packets ) {
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
     *
     * @return
     */
    public int getRadio() {
        return _radio;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public final void decode( Charset charset ) throws AISException {
        super.decode( charset );

        for( MultipleSlotBinaryMessageFieldMap field : MultipleSlotBinaryMessageFieldMap.values() ) {
            switch( field ) {
                case ADDRESSED:
                    if( _bits.size() >= field.getStartBit() )
                        _addressed = _bits.get( field.getStartBit() );
                    break;
                case STRUCTURED:
                    if( _bits.size() >= field.getStartBit() )
                        _structured = _bits.get( field.getStartBit() );
                    break;
                case DESTINATION_MMSI:
                    if( _addressed ) {
                        if( _bits.size() >= field.getStartBit() )
                            _destMmsi = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case DAC:
                    if( _structured ) {
                        if( _bits.size() >= field.getStartBit() )
                            _dac = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case FID:
                    if( _structured ) {
                        if( _bits.size() >= field.getStartBit() )
                            _fid = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    }
                    break;
                case DATA:
                    if( _addressed && _bits.length() >= 160 ) {
                        _data = _bits.get( 70, ( _bits.size() - 90 ) );
                    } else if( _structured && _bits.length() >= 166 ) {
                        _data = _bits.get( 56, ( _bits.size() - 76 ) );
                    } else if( _bits.length() > 61 ) {
                        _data = _bits.get( 40, ( _bits.size() - 61 ) );
                    } else {
                        throw new AISException( "Invalid bit count.  BitVector size: " + _bits.size() + ", BitVector length: " + _bits.length() );
                    }
                    break;
                case RADIO:
                    _radio = AISMessageDecoder.decodeUnsignedInt( _bits,
                            _bits.size() - 21, _bits.size() + 1 );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum MultipleSlotBinaryMessageFieldMap implements FieldMap {

        ADDRESSED( 38, 38 ),
        STRUCTURED( 39, 39 ),
        DESTINATION_MMSI( 40, 70 ),
        DAC( 40, 50 ), // depends on the value of structured
        FID( 50, 56 ), // depends on the value of structured
        DATA( -1, -1 ), // could be anywhere from 0-1004 bits
        RADIO( -1, 20 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        MultipleSlotBinaryMessageFieldMap( int startBit, int endBit ) {
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
