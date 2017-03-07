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
public class AssignmentModeCommand extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger( AssignmentModeCommand.class );

    private int _mmsi1;
    private int _offset1;
    private int _increment1;
    private int _mmsi2;
    private int _offset2;
    private int _increment2;

    /**
     *
     * @param source
     * @param packets
     */
    public AssignmentModeCommand( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param type
     * @param packets
     */
    public AssignmentModeCommand( String source, AISMessageType type, AISPacket... packets ) {
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
                    if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring field: {}", field.name() );
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
        AssignmentModeCommandFieldMap( int startBit, int endBit ) {
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
