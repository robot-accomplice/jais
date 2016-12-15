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
import jais.messages.enums.AISMessageType;
import jais.messages.enums.FieldMap;
import jais.messages.enums.MMSIType;
import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Jonathan Machen
 */
public abstract class AISMessageBase implements AISMessage {

    private final static Logger LOG = LogManager.getLogger( AISMessageBase.class );

    private int _mmsi;
    private MMSIType _mmsiType;
    private int _repeat; // bits 6-7

    public final static SpatialContext CTX = SpatialContext.GEO;
    protected AISPacket[] _packets;
    protected String _compositeMsg;
    protected AISMessageType _messageType;
    protected BitSet _bits;
    protected Point _position;
    protected Map<String, Object> _decodedFieldMap = new HashMap<>();

    /**
     *
     * @param packets
     */
    public AISMessageBase( AISPacket... packets ) {
        _packets = packets;
    }

    /**
     *
     * @param messageType
     * @param packets
     */
    public AISMessageBase( AISMessageType messageType, AISPacket... packets ) {
        _messageType = messageType;
        _packets = packets;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append( "AISMessage:{messageType:" ).append( _messageType.name() );

        sb.append( ",packets:[" );
        for( AISPacket packet : _packets ) {
            sb.append( packet.toString() );
        }
        sb.append( "]," ).append( "}" );

        return sb.toString();
    }

    /**
     *
     * @return
     */
    @Override
    public DateTime getTimeReceived() {
        return _packets[0].getTimeReceived();
    }

    /**
     * Type of AIS message pulled from bits 0 - 6
     *
     * @return
     */
    @Override
    public AISMessageType getType() {
        return _messageType;
    }

    /**
     *
     * @param type
     */
    @Override
    public void setType( AISMessageType type ) {
        _messageType = type;
    }

    /**
     *
     * @return
     */
    @Override
    public AISPacket[] getPackets() {
        return _packets;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMmsi() {
        return _mmsi;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasValidMmsi() {
        return isValidMmsi( _mmsi );
    }

    /**
     *
     * @param mmsi
     * @return
     */
    public static boolean isValidMmsi( long mmsi ) {
        boolean valid = ( ( mmsi < 800000000 ) && ( mmsi > 199999999 ) );

        if( !valid ) {
            LOG.warn( "MMSI: {} is not valid!", mmsi );
        }

        return valid;
    }

    /**
     *
     * @param imo
     * @return
     */
    public static boolean isValidImo( String imo ) {
        if( imo.toLowerCase().startsWith( "imo" ) ) {
            imo = imo.substring( 3 );
        }

        return isValidImo( Long.parseLong( imo.trim() ) );
    }

    /**
     *
     * @param imo
     * @return
     */
    public static boolean isValidImo( long imo ) {
//      per http://catb.org/gpsd/AIVDM.html
//      IMO Numbers are made up of letters “IMO” and seven decimal digits.
//      The digits to be checked are multiplied from left to right by 7, 6, 5, 4, 3, and 2.
//      Products are added up.
//      The sum is divided by 10.
//      The remaining digit is the check digit.
//      Example: IMO 9074729 (Pacific Frontier, Hong Kong)
//      9 - 0 - 7 - 4 - 7 - 2 - 9
//      7 - 6 - 5 - 4 - 3 - 2   ^ check digit
//      63+0+35+16+21+4=139
//      MOD(139,10) = 9 => This IMO is valid.

        LOG.info( "Validating IMO: {}", imo );

        boolean valid = ( Long.toString( imo ).length() == 7 );

        if( valid ) {
            int d = 0;
            Integer[] digits = new Integer[7];
            for( char c : Long.toString( imo ).toCharArray() ) {
                digits[d] = Integer.valueOf( "" + c );
                LOG.debug( "Digit at position: {} is {}",
                        d, digits[d] );
                d++;
            }

            digits[0] *= 7;
            digits[1] *= 6;
            digits[2] *= 5;
            digits[3] *= 4;
            digits[4] *= 3;
            digits[5] *= 2;

            int sum = 0;

            for( int i = 0; i < 6; i++ ) {
                sum += digits[i];
            }

            LOG.debug( "Sum of products is : {}", sum );

            valid = ( sum % 10 == digits[6] );

            LOG.info( "Modulus of sum divided by 10 is: {} vs {}",
                    sum % 10, digits[6] );
        }

        return valid;
    }

    /**
     *
     * @return
     */
    @Override
    public MMSIType getMMSIType() {
        if( _mmsiType == null || _mmsiType == MMSIType.UNKNOWN ) {
            _mmsiType = MMSIType.forMMSI( _mmsi );
        }

        return _mmsiType;
    }

    /**
     *
     * @return
     */
    public String getCountryOfOrigin() {
        return getMMSIType().name();
    }

    /**
     *
     * @return
     */
    @Override
    public int getRepeat() {
        return _repeat;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasSubType() {
        return false;
    }

    /**
     *
     * @return @throws jais.exceptions.AISException
     */
    @Override
    public AISMessage getSubTypeInstance() throws AISException {
        return this;
    }

    /**
     *
     * @return
     */
    @Override
    public FieldMap[] getFieldMap() {
        return AISFieldMap.values();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasPosition() {
        return false;
    }

    /**
     *
     * @return
     */
    @Override
    public Point getPosition() {
        return _position;
    }

    /**
     *
     * @throws AISException
     */
    @Override
    public void decode() throws AISException {
        _decodedFieldMap.put( "time_received", getTimeReceived() );

        StringBuilder compositeMsg = new StringBuilder();

        for( AISPacket ap : getPackets() ) {
            compositeMsg.append( ap.getRawMessage() );
        }

        _compositeMsg = compositeMsg.toString();
        _bits = AISMessageDecoder.stringToBitSet( _compositeMsg );

        for( AISFieldMap field : AISFieldMap.values() ) {
            switch( field ) {
                case TYPE:
                    if( _decodedFieldMap.get( "message_type" ) == null ) {
                        AISMessageType mType
                                = AISMessageDecoder.decodeMessageType( _bits );
                        _messageType = mType;
                    } else {
                        // hopefully already set
                    }
                    break;
                case REPEAT:
                    _repeat = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
                case MMSI:
                    _mmsi = AISMessageDecoder.decodeUnsignedInt( _bits,
                            field.getStartBit(), field.getEndBit() );
                    break;
            }
        }
    }

    /**
     *
     */
    public static enum AISFieldMap implements FieldMap {

        TYPE( 0, 5 ),
        REPEAT( 6, 7 ),
        MMSI( 8, 37 );

        private final int _startBit;
        private final int _endBit;

        /**
         *
         * @param startBit
         * @param endBit
         */
        private AISFieldMap( int startBit, int endBit ) {
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
