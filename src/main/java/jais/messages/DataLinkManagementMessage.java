/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class DataLinkManagementMessage extends AISMessageBase {

    private final static Logger LOG = LogManager
            .getLogger( DataLinkManagementMessage.class );

    private int _offset1 = -1;
    private int _slots1 = -1;
    private int _timeout1 = -1;
    private int _increment1 = -1;
    private int _offset2 = -1;
    private int _slots2 = -1;
    private int _timeout2 = -1;
    private int _increment2 = -1;
    private int _offset3 = -1;
    private int _slots3 = -1;
    private int _timeout3 = -1;
    private int _increment3 = -1;
    private int _offset4 = -1;
    private int _slots4 = -1;
    private int _timeout4 = -1;
    private int _increment4 = -1;

    /**
     *
     * @param source
     * @param packets
     */
    public DataLinkManagementMessage( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public DataLinkManagementMessage( String source, AISMessageType type, AISPacket... packets ) {
        super( source, type, packets );
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
    public int getSlots1() {
        return _slots1;
    }

    /**
     *
     * @return
     */
    public int getTimeout1() {
        return _timeout1;
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
    public int getOffset2() {
        return _offset2;
    }

    /**
     *
     * @return
     */
    public int getSlots2() {
        return _slots2;
    }

    /**
     *
     * @return
     */
    public int getTimeout2() {
        return _timeout2;
    }

    /**
     *
     * @return
     */
    public int getIncrement2() {
        return _increment2;
    }

    /**
     *
     * @return
     */
    public int getOffset3() {
        return _offset3;
    }

    /**
     *
     * @return
     */
    public int getSlots3() {
        return _slots3;
    }

    /**
     *
     * @return
     */
    public int getTimeout3() {
        return _timeout3;
    }

    /**
     *
     * @return
     */
    public int getIncrement3() {
        return _increment3;
    }

    /**
     *
     * @return
     */
    public int getOffset4() {
        return _offset4;
    }

    /**
     *
     * @return
     */
    public int getSlots4() {
        return _slots4;
    }

    /**
     *
     * @return
     */
    public int getTimeout4() {
        return _timeout4;
    }

    /**
     *
     * @return
     */
    public int getIncrement4() {
        return _increment4;
    }

    /**
     * @throws AISException
     */
    @Override
    public final void decode() throws AISException {
        super.decode();

        for( DataLinkManagementMessageFieldMap field
                : DataLinkManagementMessageFieldMap.values() ) {
            switch( field ) {
                case OFFSET1:
                    _offset1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SLOTS1:
                    _slots1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TIMEOUT1:
                    _timeout1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT1:
                    _increment1 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET2:
                    if( _bits.size() < 70 ) {
                        break;
                    }
                    _offset2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SLOTS2:
                    if( _bits.size() < 70 ) {
                        break;
                    }
                    _slots2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TIMEOUT2:
                    if( _bits.size() < 70 ) {
                        break;
                    }
                    _timeout2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT2:
                    if( _bits.size() < 70 ) {
                        break;
                    }
                    _increment2 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET3:
                    if( _bits.size() < 100 ) {
                        break;
                    }
                    _offset3 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SLOTS3:
                    if( _bits.size() < 100 ) {
                        break;
                    }
                    _slots3 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TIMEOUT3:
                    if( _bits.size() < 100 ) {
                        break;
                    }
                    _timeout3 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT3:
                    if( _bits.size() < 100 ) {
                        break;
                    }
                    _increment3 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case OFFSET4:
                    if( _bits.size() < 130 ) {
                        break;
                    }
                    _offset4 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case SLOTS4:
                    if( _bits.size() < 130 ) {
                        break;
                    }
                    _slots4 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case TIMEOUT4:
                    if( _bits.size() < 130 ) {
                        break;
                    }
                    _timeout4 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case INCREMENT4:
                    if( _bits.size() < 130 ) {
                        break;
                    }
                    _increment4 = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                default:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
            }
        }
    }

    /**
     *
     */
    private enum DataLinkManagementMessageFieldMap implements FieldMap {

        OFFSET1( 40, 51 ),
        SLOTS1( 52, 55 ),
        TIMEOUT1( 56, 58 ),
        INCREMENT1( 59, 69 ),
        OFFSET2( 70, 81 ),
        SLOTS2( 82, 85 ),
        TIMEOUT2( 86, 88 ),
        INCREMENT2( 89, 99 ),
        OFFSET3( 100, 111 ),
        SLOTS3( 112, 115 ),
        TIMEOUT3( 116, 118 ),
        INCREMENT3( 119, 129 ),
        OFFSET4( 130, 141 ),
        SLOTS4( 142, 145 ),
        TIMEOUT4( 146, 148 ),
        INCREMENT4( 149, 159 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private DataLinkManagementMessageFieldMap( int startBit, int endBit ) {
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
