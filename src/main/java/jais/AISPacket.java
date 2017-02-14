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

import jais.messages.enums.Manufacturers;
import jais.messages.enums.Talkers;
import jais.exceptions.AISPacketException;
import jais.messages.AISMessageDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Jonathan Machen
 */
public final class AISPacket {

    private final static Logger LOG = LogManager.getLogger( AISPacket.class );

    // reserved characters
    private final static char ENCAP_START = '!';
    private final static char PARAM_START = '$';
    private final static char CHECKSUM_DELIMITER = '*';
    private final static char FIELD_DELIMITER = ',';
    private final static char HEX_DELIMITER = '^';
    private final static char RESERVED_DELIMITER = '~';

    public final static double CHANNEL_A_FREQUENCY_IN_MHZ = 161.975;
    public final static double CHANNEL_B_FREQUENCY_IN_MHZ = 162.025;

    private final static String PREAMBLE = "([" + ENCAP_START + "|" + PARAM_START
            + "]{1})([A-Z0-9]{1,2})(([A-Z]{2})([A-Z]{1}))";
    public final static Pattern PREAMBLE_PATTERN = Pattern.compile( PREAMBLE );
    public final static Pattern PACKET_PATTERN = Pattern.compile( TagBlock.TAGBLOCK_STRING + "(" + PREAMBLE + "(.*))" );
    public static int PREAMBLE_GROUPS = 5;

    private TagBlock _tagBlock;
    private Preamble _preamble;
    private byte[] _rawPacket;
    private byte[] _source;
    private byte[] _type;
    private int _fragmentCount = 1;
    private int _fragmentNumber = 1;
    private int _sequentialMessageId = -1;
    private char _radioChannelCode;
    private byte[] _rawMessage;
    private byte[] _packetBody;
    private int _fillBits;
    private byte[] _checksum;
    private DateTime _timeReceived = DateTime.now();
    private byte[][] _packetParts;
    private final HashMap<String, Object> _packetMap = new HashMap<>();

    /**
     * 
     * @param rawPacket
     * @throws AISPacketException 
     */
    public AISPacket( byte [] rawPacket ) throws AISPacketException {
        _rawPacket = rawPacket;
    }

    /**
     *
     * @param rawPacket
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket ) throws AISPacketException {
        this( rawPacket.getBytes() );
    }

    /**
     *
     * @param rawPacket
     * @param source
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket, String source ) throws AISPacketException {
        this( rawPacket.getBytes(), source.getBytes() );
    }

    /**
     *
     * @param rawPacket
     * @param source
     * @throws AISPacketException
     */
    public AISPacket( byte[] rawPacket, byte[] source ) throws AISPacketException {
        if( LOG.isTraceEnabled() ) LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", 
                new Object[]{ rawPacket, source } );

        _rawPacket = rawPacket;
        _source = source;
    }

    /**
     *
     * @return
     */
    public final boolean validatePreamble() {
        if( _packetParts == null ) {
            LOG.warn( "_packetParts is null" );
            return false;
        } else if( _packetParts.length == 0 ) {
            LOG.warn( "_packetParts has zero members" );
            return false;
        } else if( _packetParts[0] == null ) {
            LOG.warn( "_packetParts[0] is null" );
            return false;
        } else {
            LOG.trace( "Creating preamble object from {}", _packetParts[0] );
            return validatePreamble( Preamble.parse( _packetParts[0] ) );
        }
    }

    /**
     *
     * @param p
     * @return
     */
    public final static boolean validatePreamble( Preamble p ) {
        return ( ( p.talker != null ) && ( p.format != null ) );
    }

    /**
     *
     * @param preambleStr
     * @return
     */
    public final static boolean validatePreamble( String preambleStr ) {
        return validatePreamble( Preamble.parse( preambleStr ) );
    }

    /**
     * Fetch the preamble (e.g. !AISVDM)
     *
     * @return
     */
    public final Preamble getPreamble() {
        return _preamble;
    }

    /**
     *
     * @return
     */
    public final TagBlock getTagBlock() {
        return _tagBlock;
    }

    /**
     *
     * @return @throws AISPacketException
     */
    public final AISPacket process() throws AISPacketException {
        return process( false );
    }

    /**
     * validate the contents of the packet and break it into its constituent parts
     *
     * @param addTagBlock
     * @return
     * @throws jais.exceptions.AISPacketException
     */
    public final AISPacket process( boolean addTagBlock ) throws AISPacketException {
        String rawPacket = null;

        if( _rawPacket == null ) {
            throw new AISPacketException( "Raw packet is null" );
        } else if( _rawPacket.length == 0 ) {
            throw new AISPacketException( "Raw packet is empty" );
        } else {
            rawPacket = new String( _rawPacket ).trim();
            if( LOG.isDebugEnabled() ) LOG.debug( "Processing new raw packet: {}", rawPacket );
        }

        Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( rawPacket );
        String source = ( _source == null ) ? new String() : new String( _source );
        if( m.find() ) {
            try {
                if( _source == null || _source.length == 0 ) {
                    _tagBlock = TagBlock.parse( m.group( 0 ) );
                    _source = _tagBlock.getSource();
                } else {
                    _tagBlock = TagBlock.parse( m.group( 0 ), source );
                }
            } catch( Throwable t ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Unable to parse TagBlock from {}", m.group( 0 ) );
            }
            _packetBody = rawPacket.substring( m.end() ).getBytes();
        } else if( addTagBlock ) {
            if( source.isEmpty() ) {
                _tagBlock = TagBlock.build( null );
            } else {
                _tagBlock = TagBlock.build( source );
            }
            _packetBody = _rawPacket;
        } else {
            if( LOG.isDebugEnabled() ) LOG.debug( "No TagBlock found and addTagBlock is false" );
            _packetBody = _rawPacket;
        }

        if( LOG.isDebugEnabled() ) LOG.debug( "_packetBody = {}", new String( _packetBody ) );
        if( _packetParts == null ) {
            _packetParts = AISPacket.fastSplit( _packetBody, FIELD_DELIMITER );
        }

        if( _packetParts == null || _packetParts.length < 6 ) {
            throw new AISPacketException( "Raw packet contains no message (inadequate number of comma-separated values)." );
        }

        try {
            switch( _packetParts.length ) {
                case 10:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position 10: {}", _packetParts[9] );
                case 9:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position  9: {}", _packetParts[8] );
                case 8:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position  8: {}", _packetParts[7] );
                case 7:
                    try {
                        String checksum = new String( _packetParts[6] );
                        if( checksum.contains( String.valueOf( CHECKSUM_DELIMITER ) ) ) {
                            _fillBits = Integer.parseInt( checksum.substring( 0, checksum.indexOf( "*" ) ) );
                            _checksum = checksum.substring( checksum.indexOf( String.valueOf( CHECKSUM_DELIMITER ) ) + 1 ).getBytes();
                        } else {
                            if( LOG.isDebugEnabled() ) LOG.debug( "Packet is missing checksum!" );
                        }
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fill bits and/or checksum due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 6:
                    if( _packetParts[5] == null || _packetParts[5].length == 0 ) {
                        throw new AISPacketException( "Raw message is empty." );
                    }
                    _rawMessage = _packetParts[5];
                case 5:
                    _radioChannelCode = ( _packetParts[4].length > 0 ) ? ( char ) _packetParts[4][0] : 'Z';
                case 4:
                    try {
                        _sequentialMessageId = ( _packetParts[3].length > 0 ) ? -1
                                : Integer.parseInt( Arrays.toString( _packetParts[3] ) ); // default to -1 if no message ID is present
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set sequential message ID due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 3:
                    try {
                        _fragmentNumber = Integer.parseInt( new String( _packetParts[2] ) );
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fragment number due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 2:
                    try {
                        _fragmentCount = Integer.parseInt( new String( _packetParts[1] ) );
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fragment count due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 1:
                    int index = indexOf( _packetParts[0], AISPacket.ENCAP_START );
                    if( index == -1 ) indexOf( _packetParts[0], AISPacket.PARAM_START );
                    
                    if( index != -1 ) {
                        _type = Arrays.copyOfRange( _packetParts[0], index, _packetParts[0].length );
                    } else {
                        throw new AISPacketException( "Packet has an unrecognizable preamble" );
                    }
                    break;
                default:
                    throw new AISPacketException( "Packet is corrupt and has no message body." );
            }
        } catch( Throwable t ) {
            throw new AISPacketException( "Encountered a malformed AISPacket: \""
                    + _rawPacket + "\" - " + t.getMessage(), t );
        }

        return this;
    }
    
    /**
     * 
     * @param ba
     * @param c
     * @return 
     */
    private static int indexOf( byte[] ba, char c ) {
        for( int i = 0; i < ba.length; i++ ) {
            if( ba[i] == c ) {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * Checks the validity of the current AIS packet
     *
     * @return
     */
    public final boolean isValid() {
        try {
            // so we don't throw NPEs over the failure to split the raw String
            if( _packetParts == null ) {
                process();
            }

            // validate preamble
            if( _packetBody.length > 82 ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet body exceeds maximum allowable size (82 characters)! {}", _packetBody );
                return false;
            } else if( _packetParts.length == 0 ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet is empty!" );
                return false;
            } else if( _packetParts.length != 7 ) {   // validate csv length
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet does not have the valid number (7) of comma separated values. {}", _packetBody );
                return false;
            } else if( !validatePreamble() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet has an invalid preamble: {}", _packetParts[0] );
                return false;
            } else {
                // check for bad characters in binary string
                for( byte c : _packetParts[5] ) {
                    // is this character within an accepted range?
                    if( !( ( c <= AISMessageDecoder.CHAR_RANGE_A_MAX && c >= AISMessageDecoder.CHAR_RANGE_A_MIN )
                            || ( c <= AISMessageDecoder.CHAR_RANGE_B_MAX && c >= AISMessageDecoder.CHAR_RANGE_B_MIN ) ) ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Packet contains an invalid character: {}", c );
                        return false;
                    }
                }

                // if we don't have any bad characters validate the checksum
                int csIndex = indexOf( _packetParts[6], CHECKSUM_DELIMITER ) + 1;

                if( csIndex > 0 ) {
                    // validate checksum
                    if( !validateChecksum( new String( _packetBody ), new String( Arrays.copyOfRange( _packetParts[6], 0, csIndex ) ) ) ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Packet failed checksum validation." );
                    }
                } else {
                    if( LOG.isDebugEnabled() ) LOG.debug( "Packet is missing fillbits and/or checksum." );
                    return false;
                }
            }
        } catch( AISPacketException ape ) {
            // do nothing
            if( LOG.isDebugEnabled() ) LOG.debug( "Packet validation faied: {}", ape.getMessage(), ape );
            return false;
        }

        return true;
    }

    /**
     *
     * @param sourceString
     * @return
     */
    public final static String generateChecksum( String sourceString ) {
        char[] buf = sourceString.toCharArray();

        int crc = 0;

        for( int i = 0; i < ( buf.length ); i++ ) {
            crc ^= buf[i];
        }

        String hexString = Integer.toHexString( crc );

        if( hexString.length() == 1 ) {
            hexString = "0" + hexString;
        }

        if( LOG.isTraceEnabled() ) LOG.trace( "Generated CRC = {}", hexString.toUpperCase() );

        return hexString;
    }

    /**
     *
     * @param rawPacket
     * @return
     */
    public final static String getChecksum( String rawPacket ) {
        return getChecksum( rawPacket, 1, rawPacket.indexOf( String.valueOf( CHECKSUM_DELIMITER ) ) );
    }

    /**
     * This is a utility message that enables binary decoding even when the binary string is all we have
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
     * @return
     */
    public final static String[] fastSplit( String s ) {
        return fastSplit( s, ',' );
    }

    /**
     * an alternative to String.split() which is a memory hog and performance donkey at scale
     *
     * @param s
     * @param delimiter
     * @return
     */
    public final static byte[][] fastSplit( byte[] s, char delimiter ) {
        if( s == null ) {
            return null;
        }

        int count = 1;

        for( int i = 0; i < s.length; i++ ) {
            if( s[i] == delimiter ) {
                count++;
            }
        }

        byte[][] array = new byte[count][];

        int a = -1;
        int b = 0;

        for( int i = 0; i < count; i++ ) {
            while( b < s.length && s[b] != delimiter ) {
                b++;
            }

            array[i] = Arrays.copyOfRange( s, a + 1, b );
            a = b;
            b++;
        }

        return array;
    }

    /**
     * an alternative to String.split() which is a memory hog and performance donkey at scale
     *
     * @param s
     * @param delimiter
     * @return
     */
    public final static String[] fastSplit( String s, char delimiter ) {
        if( s == null ) {
            return null;
        }

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
        if( LOG.isDebugEnabled() ) LOG.debug( "Comparing: \"{}\" to \"{}\"", packetChecksum.toUpperCase(), calcChecksum.toUpperCase() );
        return packetChecksum.equalsIgnoreCase( calcChecksum );
    }

    /**
     *
     * @return
     */
    public final byte[] getRawPacket() {
        return _rawPacket;
    }

    /**
     *
     * @return
     */
    public final String generateTagBlockPacketString() {
        TagBlock tb = new TagBlock();
        tb.setSource( _source );
        tb.setTimestamp( _timeReceived.getMillis() );
        return generateTagBlockPacketString( _rawPacket, tb );
    }

    /**
     *
     * @param text
     * @return
     */
    public final String generateTagBlockPacketString( String text ) {
        TagBlock tb = new TagBlock();
        tb.setSource( _source );
        tb.setTimestamp( _timeReceived.getMillis() );
        tb.setTextStr( text.getBytes() );
        return generateTagBlockPacketString( _rawPacket, tb );
    }

    /**
     *
     * @param rawPacket
     * @param tb
     * @return
     */
    public final static String generateTagBlockPacketString( byte[] rawPacket, TagBlock tb ) {
        return new StringBuilder( tb.toString() ).append( rawPacket ).toString();
    }

    /**
     *
     * @return
     */
    public final byte[] getType() {
        return _type;
    }

    /**
     *
     * @return
     */
    public final int getFragmentCount() {
        return _fragmentCount;
    }

    /**
     *
     * @return
     */
    public final int getFragmentNumber() {
        return _fragmentNumber;
    }

    /**
     *
     * @return
     */
    public final int getSequentialMessageId() {
        return _sequentialMessageId;
    }

    /**
     *
     * @return
     */
    public final char getRadioChannelCode() {
        return _radioChannelCode;
    }

    /**
     *
     * @return
     */
    public final double getRadioChannelFrequencyInMhz() {
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
    public final byte[] getRawMessage() {
        return _rawMessage;
    }

    /**
     *
     * @return
     */
    public final int getFillBits() {
        return _fillBits;
    }

    /**
     *
     * @return
     */
    public final byte[] getChecksum() {
        return _checksum;
    }

    /**
     *
     * @return
     */
    public final DateTime getTimeReceived() {
        return _timeReceived;
    }

    /**
     *
     * @param timeReceived
     */
    public final void setTimeReceived( DateTime timeReceived ) {
        _timeReceived = timeReceived;
    }

    /**
     *
     * @return
     */
    public final byte[] getSource() {
        return _source;
    }

    /**
     *
     * @param source
     */
    public final void setSource( byte[] source ) {
        _source = source;
    }

    /**
     *
     * @param sb
     * @return
     */
    public static String truncatePacket( StringBuilder sb ) {
        int truncIndex = AISPacket.getPacketTruncIndex( sb );
        String substring = null;

        if( truncIndex != -1 ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Truncating: {}", sb );
            substring = sb.substring( 0, truncIndex ).trim();
        }

        return substring;
    }

    /**
     *
     * @param sb
     * @return
     */
    public static int getPacketTruncIndex( StringBuilder sb ) {
        int truncIndex = 0;

        if( LOG.isDebugEnabled() ) LOG.debug( "Evaluating \"{}\" for truncation point.", sb );

        Matcher m = AISPacket.PREAMBLE_PATTERN.matcher( sb.toString() );
        if( m.find() ) {
            if( sb.indexOf( "\n" ) > -1 ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "String is terminated by a newline" );
                truncIndex = sb.indexOf( "\n" );
            } else if( sb.indexOf( "\r" ) > -1 ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "String is terminated by a carriage return" );
                truncIndex = sb.indexOf( "\r" );
            } else if( m.find() ) {
                truncIndex = sb.indexOf( m.group( 0 ), 1 );
                if( LOG.isDebugEnabled() ) LOG.debug( "Truncating based on preamble" );
                if( LOG.isDebugEnabled() ) LOG.debug( "Matched string for index is: \"{}\"", m.group( 0 ) );
            } else {
                if( LOG.isDebugEnabled() ) LOG.debug( "Line should not be truncated." );
                truncIndex = -1;
            }
        }

        if( LOG.isDebugEnabled() ) LOG.debug( "Truncation index set to {}", truncIndex );

        return truncIndex;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public final boolean equals( Object o ) {
        boolean isEqual = false;

        if( o instanceof AISPacket ) {
            AISPacket p = ( AISPacket ) o;
            isEqual = Arrays.equals( p.getRawPacket(), _rawPacket );
            isEqual = isEqual && _timeReceived.equals( p.getTimeReceived() );
        }

        return isEqual;
    }

    /**
     *
     * @return
     */
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 79 * hash + ( _rawPacket != null ? Arrays.hashCode( _rawPacket ) : 0 );
        return hash;
    }

    /**
     *
     * @return
     */
    public final HashMap<String, Object> toMap() {
        if( _packetMap.isEmpty() ) {
            _packetMap.put( "tagblock", _tagBlock );
            _packetMap.put( "preamble", _preamble );
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

    /**
     *
     */
    public static class Preamble {

        public final byte[] rawPreamble;
        public char firstChar;
        public boolean isEncapsulated;
        public Talkers talker;
        public boolean isProprietary;
        public Manufacturers manufacturer;
        public byte[] format;
        public boolean isQuery;
        public byte[] parsed;

        /**
         *
         * @param rawPreamble
         */
        public Preamble( byte[] rawPreamble ) {
            this.rawPreamble = rawPreamble;
        }

        /**
         *
         * @return
         */
        public Preamble parse() {
            return parse( this.rawPreamble );
        }

        /**
         *
         * @param rawPreamble
         * @return
         */
        public static Preamble parse( byte[] rawPreamble ) {
            return parse( new String( rawPreamble ) );
        }

        /**
         *
         * @param rawPreamble
         * @return
         */
        public static Preamble parse( String rawPreamble ) {
            Preamble p = new Preamble( rawPreamble.getBytes() );

            if( LOG.isDebugEnabled() ) LOG.debug( "Parsing {}", rawPreamble );
            Matcher m = PREAMBLE_PATTERN.matcher( rawPreamble );
            if( m.find() ) {
                String parsed = m.group( 0 );
                p.parsed = parsed.getBytes();
                if( LOG.isDebugEnabled() ) LOG.debug( "Found {} matcher groups: {}=({})({})({})({})", m.groupCount(), m.group(), m.group( 1 ), m.group( 2 ), m.group( 4 ), m.group( 5 ) );
                p.firstChar = m.group( 1 ).charAt( 0 );

                if( p.firstChar == '!' ) {
                    p.isEncapsulated = true;
                } else if( m.group( 1 ).equals( "$" ) ) {
                    p.isEncapsulated = false;
                } else {
                    if( LOG.isDebugEnabled() ) LOG.info( "Unrecognized starting character in address field: {}", m.group( 1 ) );
                    p.isEncapsulated = false;
                }

                if( m.group( 3 ).startsWith( "P" ) ) {
                    p.talker = Talkers.P;
                    p.manufacturer = Manufacturers.valueOf( ( m.group( 3 ) + m.group( 4 ) ).toUpperCase() );
                } else if( Talkers.isValid( m.group( 2 ).toUpperCase() ) ) {
                    p.talker = Talkers.valueOf( m.group( 2 ).toUpperCase() );
                } else {
                    p.talker = null;
                    if( LOG.isDebugEnabled() ) LOG.info( "Unrecognized/invalid talker type: {}", m.group( 2 ) );
                }

                p.format = m.group( 4 ).getBytes();
                p.isQuery = m.group( 5 ).equals( "Q" );
            } else {
                if( LOG.isDebugEnabled() ) LOG.warn( "Preamble {} appears to be invalid and does not match the format: {}", rawPreamble, PREAMBLE );
            }

            return p;
        }
    }
}
