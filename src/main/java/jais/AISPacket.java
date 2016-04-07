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

package jais;

import jais.exceptions.AISPacketException;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Jonathan Machen
 */
public class AISPacket {

    private final static Logger LOG = LogManager.getLogger( AISPacket.class );

    private final static int CHAR_RANGE_A_MIN = 48;
    private final static int CHAR_RANGE_A_MAX = 87;
    private final static int CHAR_RANGE_B_MIN = 96;
    private final static int CHAR_RANGE_B_MAX = 119;

    private static final double CHANNEL_A_FREQUENCY_IN_MHZ = 161.975;
    private static final double CHANNEL_B_FREQUENCY_IN_MHZ = 162.025;
    private static final String PREAMBLE = "!AIVD[O|M]{1}";

    private String _rawPacket;
    private String _source;
    private String _type;
    private int _fragmentCount = 1;
    private int _fragmentNumber = 1;
    private int _sequentialMessageId = -1;
    private char _radioChannelCode;
    private String _rawMessage;
    private int _fillBits;
    private String _checksum;
    private DateTime _timeReceived = DateTime.now();
    private String[] _packetParts;
    private final HashMap<String, Object> _packetMap = new HashMap<>();

    /**
     *
     * @param rawPacket
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket ) throws AISPacketException {
        this( rawPacket, "UNKNOWN" );
    }

    /**
     *
     * @param rawPacket
     * @param source
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket, String source ) throws AISPacketException {
        LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", new Object[]{rawPacket, source} );
        _rawPacket = rawPacket;
        _source = source;
    }

    /**
     * validate the contents of the packet and break it into its constituent
     * parts
     *
     * @throws jais.exceptions.AISPacketException
     */
    public final void process() throws AISPacketException {
        LOG.debug( "Processing new raw packet: {}", _rawPacket );

        if( _rawPacket == null ) {
            throw new AISPacketException( "Raw packet is null" );
        } else if( _rawPacket.isEmpty() ) {
            throw new AISPacketException( "Raw packet is empty" );
        }

        if( _packetParts == null ) {
            _packetParts = AISPacket.fastSplit( _rawPacket, ',' );
        }

        if( _packetParts == null || _packetParts.length < 6 ) {
            throw new AISPacketException( "Raw packet contains no message (inadequate number of comma-separated values)." );
        }

        try {
            switch( _packetParts.length ) {
                case 10:
                    LOG.debug( "Unrecognized field at position 10: {}", _packetParts[9] );
                case 9:
                    LOG.debug( "Unrecognized field at position  9: {}", _packetParts[8] );
                case 8:
                    LOG.debug( "Unrecognized field at position  8: {}", _packetParts[7] );
                case 7:
                    try {
                        _fillBits = Integer.parseInt( _packetParts[6].substring( 0, _packetParts[6].indexOf( "*" ) ) );
                        _checksum = _packetParts[6].substring( _packetParts[6].indexOf( "*" ) + 1 );
                    } catch( NumberFormatException nfe ) {
                        LOG.debug( "Failed to set fill bits and/or checksum due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 6:
                    if( _packetParts[5].isEmpty() ) {
                        throw new AISPacketException( "Raw message is empty." );
                    }
                    _rawMessage = _packetParts[5];
                case 5:
                    _radioChannelCode = ( _packetParts[4].length() > 0 ) ? _packetParts[4].charAt( 0 ) : 'Z';
                case 4:
                    try {
                        _sequentialMessageId = ( _packetParts[3].isEmpty() ) ? -1
                                : Integer.parseInt( _packetParts[3] ); // default to -1 if no message ID is present
                    } catch( NumberFormatException nfe ) {
                        LOG.debug( "Failed to set sequential message ID due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 3:
                    try {
                        _fragmentNumber = Integer.parseInt( _packetParts[2] );
                    } catch( NumberFormatException nfe ) {
                        LOG.debug( "Failed to set fragment number due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 2:
                    try {
                        _fragmentCount = Integer.parseInt( _packetParts[1] );
                    } catch( NumberFormatException nfe ) {
                        LOG.debug( "Failed to set fragment count due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 1:
                    _type = _packetParts[0].substring( _packetParts[0].indexOf( "!" ) );
                    break;
                default:
                    throw new AISPacketException( "Packet is corrupt and has no message body." );
            }
        } catch( Throwable t ) {
            throw new AISPacketException( "Encountered a malformed AISPacket: \""
                    + _rawPacket + "\" - " + t.getMessage(), t );
        }
    }

    /**
     * Checks the validity of the current AIS packet
     *
     * @return
     */
    public final boolean isValid() {
        try {
            if( _packetParts == null ) {
                process();
            }

            // validate preamble
            if( _packetParts.length == 0 ) {
                LOG.warn( "Packet is empty!" );
                return false;
            } else if( _packetParts.length != 7 ) {   // validate csv length
                LOG.warn( "Packet does not have the valid number (7) of comma separated values." );
                return false;
            } else if( !_packetParts[0].matches( PREAMBLE ) ) {
                LOG.warn( "Packet has an invalid preamble." );
                return false;
            } else {
                // check for bad characters in binary string
                for( char c : _packetParts[5].toCharArray() ) {
                    // is this character within an accepted range?
                    if( !( ( c <= CHAR_RANGE_A_MAX && c >= CHAR_RANGE_A_MIN )
                            || ( c <= CHAR_RANGE_B_MAX && c >= CHAR_RANGE_B_MIN ) ) ) {
                        LOG.warn( "Packet contains an invalid character: {}", c );
                        return false;
                    }
                }

                // if we don't have any bad characters validate the checksum
                int csIndex = _packetParts[6].indexOf( "*" ) + 1;

                if( csIndex > 0 ) {
                    // validate checksum
                    if( !validateChecksum( _rawPacket, _packetParts[6].substring( csIndex ) ) ) {
                        LOG.warn( "Packet failed checksum validation." );
                    }
                } else {
                    LOG.warn( "Packet is missing fillbits and/or checksum." );
                    return false;
                }
            }
        } catch( AISPacketException ipe ) {
            // do nothing
            return false;
        }

        return true;
    }

    /**
     *
     * @param genString
     * @return
     */
    public static String generateChecksum( String genString ) {
        char[] buf = genString.toCharArray();

        int crc = 0;

        for( int i = 0; i < ( buf.length ); i++ ) {
            crc ^= buf[i];
        }

        String hexString = Integer.toHexString( crc );

        if( hexString.length() == 1 ) {
            hexString = "0" + hexString;
        }

        LOG.trace( "Generated CRC = {}", hexString );

        return hexString;
    }

    /**
     *
     * @param rawPacket
     * @return
     */
    public final static String getChecksum( String rawPacket ) {
        return getChecksum( rawPacket, 1, rawPacket.indexOf( "*" ) );
    }

    /**
     *
     * @param rawData
     * @return
     */
    public final static String createPacketStringFromBinaryString( String rawData ) {
        rawData = "!AIVDM,1,1,,A," + rawData + ",0*";
        rawData += AISPacket.getChecksum( rawData );

        return rawData;
    }

    /**
     *
     * @param rawData
     * @return
     * @throws jais.exceptions.AISPacketException
     */
    public final static AISPacket createFromBinaryString( String rawData ) throws AISPacketException {
        return createFromBinaryString( rawData, "UNKNOWN" );
    }

    /**
     *
     * @param rawData
     * @param source
     * @return
     * @throws jais.exceptions.AISPacketException
     */
    public final static AISPacket createFromBinaryString( String rawData, String source ) throws AISPacketException {
        AISPacket packet = new AISPacket( createPacketStringFromBinaryString( rawData ), source );

        return packet;
    }

    /**
     *
     * @param s
     * @param delimiter
     * @return
     */
    public static String[] fastSplit( String s, char delimiter ) {
        int count = 1;

        for( int i = 0; i < s.length(); i++ ) {
            if( s.charAt( i ) == delimiter ) {
                count++;
            }
        }

        String[] array = new String[count];

        int a = -1;
        int b = 0;

        for( int i = 0; i < count; i++ ) {
            while( b < s.length() && s.charAt( b ) != delimiter ) {
                b++;
            }

            array[i] = s.substring( a + 1, b );
            a = b;
            b++;
        }

        return array;
    }

    /**
     *
     * @param genString
     * @param startFrom
     * @param endAt
     * @return
     */
    public final static String getChecksum( String genString, int startFrom, int endAt ) {
        return AISPacket.generateChecksum( genString.substring( startFrom, endAt ) );
    }

    /**
     *
     * @param rawPacket
     * @param packetChecksum
     * @return
     */
    public final static boolean validateChecksum( String rawPacket, String packetChecksum ) {
        String calcChecksum = getChecksum( rawPacket );
        if( calcChecksum.length() == 1 ) {
            calcChecksum = "0" + calcChecksum;
        }
        LOG.debug( "Comparing: \"{}\" to \"{}\"", new Object[]{packetChecksum.toUpperCase(), calcChecksum.toUpperCase()} );
        return packetChecksum.equalsIgnoreCase( calcChecksum );
    }

    /**
     *
     * @return
     */
    public String getRawPacket() {
        return _rawPacket;
    }

    /**
     *
     * @return
     */
    public String getEnhancedPacketString() {
        return getEnhancedPacketString( _rawPacket, _source, _timeReceived );
    }

    /**
     *
     * @param data
     * @return
     */
    public String getEnhancedPacketString( String... data ) {
        return getEnhancedPacketString( _rawPacket, _source, _timeReceived, data );
    }

    /**
     *
     * @param rawPacket
     * @param source
     * @param timeReceived
     * @param data
     * @return
     */
    public final static String getEnhancedPacketString( String rawPacket, 
            String source, DateTime timeReceived, String... data ) {

        StringBuilder pktString = new StringBuilder();
        pktString.append( rawPacket ).append( "{" ).append( source ).append( "|" ).append( timeReceived );

        for( String value : data ) {
            if( !value.isEmpty() ) {
                pktString.append( "|" ).append( value );
            }
        }

        pktString.append( "}" ).append( AISPacket.generateChecksum( pktString.toString() ) );

        return pktString.toString();
    }

    /**
     *
     * @return
     */
    public String getType() {
        return _type;
    }

    /**
     *
     * @return
     */
    public int getFragmentCount() {
        return _fragmentCount;
    }

    /**
     *
     * @return
     */
    public int getFragmentNumber() {
        return _fragmentNumber;
    }

    /**
     *
     * @return
     */
    public int getSequentialMessageId() {
        return _sequentialMessageId;
    }

    /**
     *
     * @return
     */
    public char getRadioChannelCode() {
        return _radioChannelCode;
    }

    /**
     *
     * @return
     */
    public double getRadioChannelFrequencyInMhz() {
        double frequency = 0;

        switch( _radioChannelCode ) {
            case 'a':
                frequency = CHANNEL_A_FREQUENCY_IN_MHZ;
                break;
            case 'b':
                frequency = CHANNEL_B_FREQUENCY_IN_MHZ;
                break;
        }

        return frequency;
    }

    /**
     *
     * @return
     */
    public String getRawMessage() {
        return _rawMessage;
    }

    /**
     *
     * @return
     */
    public int getFillBits() {
        return _fillBits;
    }

    /**
     *
     * @return
     */
    public String getChecksum() {
        return _checksum;
    }

    /**
     *
     * @return
     */
    public DateTime getTimeReceived() {
        return _timeReceived;
    }

    /**
     *
     * @param timeReceived
     */
    public void setTimeReceived( DateTime timeReceived ) {
        _timeReceived = timeReceived;
    }

    /**
     *
     * @return
     */
    public String getSource() {
        return _source;
    }

    /**
     *
     * @param source
     */
    public void setSource( String source ) {
        _source = source;
    }

    /**
     *
     * @return
     */
    public static double getCHANNEL_A_FREQUENCY_IN_MHZ() {
        return CHANNEL_A_FREQUENCY_IN_MHZ;
    }

    /**
     *
     * @return
     */
    public static double getCHANNEL_B_FREQUENCY_IN_MHZ() {
        return CHANNEL_B_FREQUENCY_IN_MHZ;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals( Object o ) {
        boolean isEqual = false;

        if( o instanceof AISPacket ) {
            AISPacket p = ( AISPacket ) o;
            isEqual = p.getRawPacket().equals( _rawPacket );
            isEqual = isEqual && _timeReceived.equals( p.getTimeReceived() );
        }

        return isEqual;
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + ( _rawPacket != null ? _rawPacket.hashCode() : 0 );
        return hash;
    }

    /**
     *
     * @return
     */
    public HashMap<String, Object> toMap() {
        if( _packetMap.isEmpty() ) {
            _packetMap.put( "raw_message", _rawMessage );
            _packetMap.put( "raw_packet", _rawPacket );
            _packetMap.put( "time_received", _timeReceived );
            _packetMap.put( "source", _source );
            _packetMap.put( "fragment_count", _fragmentCount );
            _packetMap.put( "fragment_number", _fragmentNumber );
            _packetMap.put( "sequential_message_id", _sequentialMessageId );
            _packetMap.put( "radio_channel_code", _radioChannelCode );
            _packetMap.put( "checksum", _checksum );
            _packetMap.put( "fill_bits", _fillBits );
        }

        return _packetMap;
    }
}
