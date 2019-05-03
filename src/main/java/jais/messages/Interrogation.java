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
import jais.messages.enums.FieldMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class Interrogation extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( Interrogation.class );

    private int _mmsi1;     // interrogated mmsi
    private int _type1_1;    // first message type
    private int _offset1_1;
    private int _type1_2;
    private int _offset1_2;
    private int _mmsi2;
    private int _type2_1;
    private int _offset2_1;

    /**
     *
     * @param source
     * @param packets
     */
    public Interrogation( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public Interrogation( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
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
    public int getType1_1() {
        return _type1_1;
    }

    /**
     *
     * @return
     */
    public int getOffset1_1() {
        return _offset1_1;
    }

    /**
     *
     * @return
     */
    public int getType1_2() {
        return _type1_2;
    }

    /**
     *
     * @return
     */
    public int getOffset1_2() {
        return _offset1_2;
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
    public int getType2_1() {
        return _type2_1;
    }

    /**
     *
     * @return
     */
    public int getOffset2_1() {
        return _offset2_1;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( InterrogationFieldMap field : InterrogationFieldMap.values() ) {
            switch( field ) {
                case MMSI1:
                    if( _bits.size() >= field.getStartBit() )
                        _mmsi1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case TYPE1_1:
                    if( _bits.size() >= field.getStartBit() )
                        _type1_1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET1_1:
                    if( _bits.size() >= field.getStartBit() )
                        _offset1_1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case TYPE1_2:
                    if( _bits.size() >= field.getStartBit() )
                        _type1_2 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET1_2:
                    if( _bits.size() >= field.getStartBit() )
                        _offset1_2 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI2:
                    if( _bits.size() >= field.getStartBit() )
                        _mmsi2 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case TYPE2_1:
                    if( _bits.size() >= field.getStartBit() )
                        _type2_1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET2_1:
                    if( _bits.size() >= field.getStartBit() )
                        _offset2_1 = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum InterrogationFieldMap implements FieldMap {

        SPARE1( 38, 39 ),
        MMSI1( 40, 69 ),
        TYPE1_1( 70, 75 ),
        OFFSET1_1( 76, 87 ),
        SPARE2( 88, 89 ),
        TYPE1_2( 90, 95 ),
        OFFSET1_2( 96, 107 ),
        SPARE3( 108, 109 ),
        MMSI2( 110, 139 ),
        TYPE2_1( 140, 145 ),
        OFFSET2_1( 146, 157 ),
        SPARE4( 158, 159 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        InterrogationFieldMap( int startBit, int endBit ) {
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
