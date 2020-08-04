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
import jais.messages.enums.BinaryBroadcastMessageType;
import jais.messages.enums.FieldMap;
import java.util.BitSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class BinaryBroadcastMessage extends AISMessageBase {

    private final static Logger LOG = LogManager.getLogger(BinaryBroadcastMessage.class );

    private int _dac; // designated area code
    private int _fid; // functional id
    private BitSet _data;

    /**
     *
     * @param source
     * @param packets
     */
    public BinaryBroadcastMessage( String source, AISPacket... packets ) {
        super( source, packets );
    }

    /**
     *
     * @param source
     * @param messageType
     * @param packets
     */
    public BinaryBroadcastMessage( String source, AISMessageType messageType, AISPacket... packets ) {
        super( source, messageType, packets );
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
    public BinaryBroadcastMessageType getSubType() {
        return BinaryBroadcastMessageType.UNKNOWN;
    }

    /**
     *
     * @throws jais.exceptions.AISException
     */
    @Override
    public void decode() throws AISException {
        super.decode();

        for( BinaryBroadcastMessageBaseFieldMap field : BinaryBroadcastMessageBaseFieldMap.values() ) {
            switch( field ) {
                case DAC:
                    if( _bits.size() >= field.getStartBit() )
                        _dac = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case FID:
                    if( _bits.size() >= field.getStartBit() )
                        _fid = AISMessageDecoder.decodeUnsignedInt( _bits, field.getStartBit(), field.getEndBit() );
                    break;
                case DATA:
                    // store the undecoded portion of the BitSet in the data 
                    // field for later decoding by subtype
                    if( _bits.length() > field.getStartBit() )
                        _data = _bits.get( field.getStartBit(), _bits.size() - 1 );
                    break;
                case DESTINATION_MMSI:
                    break;
                case RETRANSMIT:
                    break;
                case SEQUENCE_NUMBER:
                    break;
                case SPARE:
                    break;
            }
        }
    }

    /**
     *
     */
    private enum BinaryBroadcastMessageBaseFieldMap implements FieldMap {

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
        BinaryBroadcastMessageBaseFieldMap( int startBit, int endBit ) {
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
