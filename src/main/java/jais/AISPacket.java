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

import jais.exceptions.AISException;
import jais.messages.enums.Manufacturers;
import jais.messages.enums.Talkers;
import jais.exceptions.AISPacketException;
import jais.messages.AISMessageDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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

    private final static double CHANNEL_A_FREQUENCY_IN_MHZ = 161.975;
    private final static double CHANNEL_B_FREQUENCY_IN_MHZ = 162.025;

    private final static String PREAMBLE = "([" + ENCAP_START + "|" + PARAM_START
            + "]{1})([A-Z0-9]{1,2})(([A-Z]{2})([A-Z]{1}))";
    public final static Pattern PREAMBLE_PATTERN = Pattern.compile( PREAMBLE );
    public final static Pattern PACKET_PATTERN = Pattern.compile( "(" + TagBlock.TAGBLOCK_STRING + ")?(" + PREAMBLE + "(.*))" );
    public static int PREAMBLE_GROUPS = 5;
    public final static Charset CHARSET = StandardCharsets.US_ASCII;
    

    private TagBlock _tagBlock;
    private Preamble _preamble;
    private final byte [] _rawPacket;  // the unparsed initial string
    private byte [] _source;
    private byte [] _type;
    private int _fragmentCount = 1;
    private int _fragmentNumber = 1;
    private int _sequentialMessageId = -1;
    private char _radioChannelCode;
    private byte [] _rawMessage; // the message sans tagblock
    private byte [] _packetBody; // the binary string
    private int _fillBits;
    private byte [] _checksum;
    private DateTime _timeReceived = DateTime.now();
    private byte [][] _packetParts;

    /**
     *
     * @param rawPacket
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( byte [] rawPacket ) throws AISPacketException {
        this( rawPacket, str2bArray( "UNKNOWN" ) );
    }

    /**
     *
     * @param rawPacket
     * @param source
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( byte [] rawPacket, byte [] source ) throws AISPacketException {
        if( LOG.isTraceEnabled() ) LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", rawPacket, source );
        _rawPacket = trim( rawPacket );
        _source = trim( source );
    }

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
        if( LOG.isTraceEnabled() ) LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", rawPacket, source );
        _rawPacket = str2bArray( rawPacket.trim() );
        _source = str2bArray( source.trim() );
    }

    /**
     *
     * @return
     */
    private boolean validatePreamble() {
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
            if( LOG.isTraceEnabled() ) LOG.trace( "Creating preamble object from {}", bArray2Str( _packetParts[0] ) );
            return validatePreamble( Preamble.parse( _packetParts[0] ) );
        }
    }

    /**
     *
     * @param p
     * @return
     */
    private static boolean validatePreamble( Preamble p ) {
        return ( ( p.talker != null ) && ( p.format != null ) );
    }

    /**
     *
     * @param preambleStr
     * @return
     */
    public static boolean validatePreamble( String preambleStr ) {
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
        String rawPacket;

        if( _rawPacket == null ) {
            throw new AISPacketException( "Raw packet is null" );
        } else if( _rawPacket.length == 0 ) {
            throw new AISPacketException( "Raw packet is empty" );
        } else {
            rawPacket = bArray2Str( trim( _rawPacket ) );
            if( LOG.isDebugEnabled() ) LOG.debug( "Processing new raw packet: {}", rawPacket );
        }

        Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( rawPacket );
        if( m.find() ) {
            try {
                if( _source == null || _source.length == 0 ) {
                    _tagBlock = TagBlock.parse( m.group( 0 ) );
                    _source = _tagBlock.getSource();
                } else {
                    _tagBlock = TagBlock.parse( m.group( 0 ), _source );
                }
            } catch( Throwable t ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Unable to parse TagBlock from {}", m.group( 0 ) );
            }

            _packetBody = str2bArray( rawPacket.substring( m.end() ) );
        } else if( addTagBlock ) {
            if( _source == null || _source.length == 0 ) {
                _tagBlock = TagBlock.build( null );
            } else {
                _tagBlock = TagBlock.build( _source );
            }
            _packetBody = _rawPacket;
        } else {
            if( LOG.isDebugEnabled() ) LOG.debug( "No TagBlock found and addTagBlock is false" );
            _packetBody = _rawPacket;
        }

        if( LOG.isDebugEnabled() ) LOG.debug( "_packetBody = {}", bArray2Str( _packetBody ) );
        
        if( _packetParts == null ) {
            _packetParts = AISPacket.fastSplit( _packetBody, FIELD_DELIMITER );
        }

        if( _packetParts == null || _packetParts.length < 6 ) {
            throw new AISPacketException( "Raw packet contains no message (inadequate number of comma-separated values)." );
        }

        try {
            switch( _packetParts.length ) {
                case 10:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position 10: {}", bArray2Str( _packetParts[9] ) );
                case 9:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position  9: {}", bArray2Str( _packetParts[8] ) );
                case 8:
                    if( LOG.isDebugEnabled() ) LOG.debug( "Unrecognized field at position  8: {}", bArray2Str( _packetParts[7] ) );
                case 7:
                    try {
                        byte[] checksum = _packetParts[6];
                        int csIndex = indexOf( _packetParts[6], CHECKSUM_DELIMITER );
                        if( csIndex != -1 ) {
                            _fillBits = Integer.parseInt( substring( checksum, 0, csIndex ) );
                            _checksum = trim( Arrays.copyOfRange( checksum, csIndex + 1, checksum.length ) );
                        } else {
                            if( LOG.isDebugEnabled() ) LOG.debug( "Packet is missing checksum!" );
                        }
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fill bits and/or checksum due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 6:
                    if( _packetParts[5] == null ) {
                        throw new AISPacketException( "Raw message is null." );
                    } else if( _packetParts[5].length == 0 ) {
                        throw new AISPacketException( "Raw message is empty." );
                    }
                    _rawMessage = _packetParts[5]; // only the binary string
                case 5:
                    _radioChannelCode = ( _packetParts[4].length != 0 ) ? ( char ) _packetParts[4][0] : 'Z';
                case 4:
                    try {
                        // default to -1 if no message ID is present
                        _sequentialMessageId = ( _packetParts[3].length == 0 ) ? -1 : ( int )_packetParts[3][0]; 
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set sequential message ID due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 3:
                    try {
                        _fragmentNumber = ( int )_packetParts[2][0];
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fragment number due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 2:
                    try {
                        _fragmentCount = ( int )_packetParts[1][0];
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Failed to set fragment count due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 1:
                    int index = indexOf( _packetParts[0], AISPacket.ENCAP_START );
                    if( index == -1 ) index = indexOf( _packetParts[0], AISPacket.PARAM_START );
                    
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
            throw new AISPacketException( "Encountered a malformed AISPacket: \"" + bArray2Str( _rawPacket ) + "\" - " + t.getMessage(), t );
        }

        return this;
    }
    
    /**
     * 
     * @param bytes
     * @return 
     */
    public static String bArray2Str( byte [] bytes ) {
        return new String( bArray2cArray( bytes ) );
    }
    
    /**
     * 
     * @param string
     * @return 
     */
    public static byte [] str2bArray( String string ) {
        return CHARSET.encode( string ).array();
    }
    
    /**
     * 
     * @param bytes
     * @return 
     */
    private static byte [] trim( byte [] bytes ) {
        char [] chars = CHARSET.decode( ByteBuffer.wrap( bytes ) ).array();
        
        for( int i = chars.length - 1; i > -1; i-- ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Character at position {} is {}", i, chars[i] );
            
            switch( chars[i] ) {
                case '\n':
                    if( LOG.isTraceEnabled() ) LOG.trace( "Found newline" );
                    break;
                case '\r':
                    if( LOG.isTraceEnabled() ) LOG.trace( "Found carriage return" );
                    break;
                case '\t':
                    if( LOG.isTraceEnabled() ) LOG.trace( "Found tab" );
                    break;
                case ' ':
                    if( LOG.isTraceEnabled() ) LOG.trace( "Found space" );
                    break;
                default:
                    if( LOG.isTraceEnabled() ) LOG.trace( "Found non-whitespace character {} at position {}", chars[i], i );
                    return Arrays.copyOfRange( bytes, 0, i + 1 );  // because the "to" value in Arrays.copyOfRange is EXclusive
            }
        }
        
        return bytes;
    }
    
    /**
     * 
     * @param bytes
     * @param start
     * @param end
     * @return 
     */
    private static String substring( byte [] bytes, int start, int end ) {
        return bArray2Str( Arrays.copyOfRange( bytes, start, end ) );
    }
    
    /**
     * 
     * @param bytes
     * @return 
     */
    private static int getInt( byte [] bytes ) throws AISException {
        if( bytes.length < 4 ) throw new AISException( "The byte array is too short to represent an int" );
        return ByteBuffer.wrap( bytes ).getInt();
    }
    
    /**
     * 
     * @param ba
     * @param c
     * @return 
     */
    private static int indexOf( byte[] ba, char c ) {
        char [] chars = CHARSET.decode( ByteBuffer.wrap( ba ) ).array();
        for( int i = 0; i < chars.length; i++ ) {
            if( chars[i] == c ) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * 
     * @param bytes
     * @return 
     */
    private static char [] bArray2cArray( byte [] bytes ) {
        return CHARSET.decode( ByteBuffer.wrap( bytes ) ).array();
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
                if( LOG.isDebugEnabled() ) 
                    LOG.debug( "Packet body exceeds maximum allowable size (82 characters)! {}", bArray2Str( _packetBody ) );
                return false;
            } else if( _packetParts.length == 0 ) {
                if( LOG.isDebugEnabled() ) 
                    LOG.debug( "Packet is empty!" );
                return false;
            } else if( _packetParts.length != 7 ) {   // validate csv length
                if( LOG.isDebugEnabled() ) 
                    LOG.debug( "Packet does not have the valid number (7) of comma separated values. {}", bArray2Str( _packetBody ) );
                return false;
            } else if( !validatePreamble() ) {
                if( LOG.isDebugEnabled() ) 
                    LOG.debug( "Packet has an invalid preamble: {}", bArray2Str( _packetParts[0] ) );
                return false;
            } else {
                // check for bad characters in binary string
                for( char c : bArray2cArray( _packetParts[5] ) ) {
                    // is this character within an accepted range?
                    if( !( ( c <= AISMessageDecoder.CHAR_RANGE_A_MAX && c >= AISMessageDecoder.CHAR_RANGE_A_MIN )
                            || ( c <= AISMessageDecoder.CHAR_RANGE_B_MAX && c >= AISMessageDecoder.CHAR_RANGE_B_MIN ) ) ) {
                        if( LOG.isDebugEnabled() ) 
                            LOG.debug( "Packet contains an invalid character: {}", c );
                        return false;
                    }
                }

                // if we don't have any bad characters validate the checksum
                int csIndex = indexOf( _packetBody, CHECKSUM_DELIMITER ) + 1;

                if( csIndex > 0 ) {
                    // validate checksum
                    if( !validateChecksum( _packetBody, _checksum ) ) {
                        if( LOG.isDebugEnabled() ) 
                            LOG.debug( "Packet failed checksum validation." );
                        return false;
                    }
                } else {
                    if( LOG.isDebugEnabled() ) 
                            LOG.debug( "Packet is missing fillbits and/or checksum." );
                    return false;
                }
            }
        } catch( AISPacketException ape ) {
            // do nothing
            if( LOG.isDebugEnabled() ) 
                LOG.debug( "Packet validation faied: {}", ape.getMessage(), ape );
            return false;
        }

        return true;
    }
    
    /**
     *
     * @param source
     * @return
     */
    private static int generateChecksum( char [] source ) {
        if( LOG.isDebugEnabled() ) LOG.debug( "Generating checksum for String \"{}\"", new String( source ) );

        int crc = 0;

        for (char aSource : source) {
            crc ^= aSource;
        }

        if( LOG.isTraceEnabled() ) LOG.trace( "Generated CRC = {}", crc );

        return crc;
    }
    
    /**
     * 
     * @param sourceString
     * @return 
     */
    public static String generateChecksumString( String sourceString ) {
        String hexString = Integer.toHexString( generateChecksum( sourceString.toCharArray() ) );
        
        hexString = ( hexString.length() == 1 ) ? "0" + hexString : hexString;
        
        return hexString;
    }

    /**
     *
     * @param data
     * @return
     */
    private static int getChecksum( String data ) {
        if( LOG.isTraceEnabled() ) LOG.trace( "Found * at {}", data.indexOf( String.valueOf( CHECKSUM_DELIMITER ) ) );
        return getChecksum( data, 1, data.indexOf( String.valueOf( CHECKSUM_DELIMITER ) ) );
    }
    
    /**
     * 
     * @param bytes
     * @return 
     */
    private static int getChecksum( byte [] bytes ) {
        return AISPacket.generateChecksum( bArray2cArray( Arrays.copyOfRange( bytes, 1, indexOf( bytes, CHECKSUM_DELIMITER ) ) ) );
    }

    /**
     *
     * @param genString
     * @param startFrom
     * @param endAt
     * @return
     */
    public static int getChecksum( String genString, int startFrom, int endAt ) {
        if( endAt <= startFrom || endAt > genString.length() ) return -1;
            
        return AISPacket.generateChecksum( genString.substring( startFrom, endAt ).toCharArray() );
    }
    
    /**
     *
     * @param data
     * @param packetChecksum
     * @return
     */
    private static boolean validateChecksum( byte [] data, byte [] packetChecksum ) {
        long calcChecksum;
        long pktChecksum;
        
        byte [] trimmed = trim( data );
        
        try {
            calcChecksum = getChecksum( trimmed );
            if( LOG.isDebugEnabled() ) LOG.debug( "Generated checksum {}", calcChecksum );
        } catch( NumberFormatException nfe ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Cannot produce a checksum from  \"{}\"", bArray2Str( trimmed ) );
            return false;
        }

        try {
            pktChecksum = Long.parseUnsignedLong( bArray2Str( packetChecksum ), 16 );
        } catch( NumberFormatException nfe ) {
            LOG.info( "Cannot parse \"{}\" into a valid long", bArray2Str( packetChecksum ) );
            return false;
        }
        
        if( LOG.isDebugEnabled() ) {
            LOG.debug( "Comparing: \"{}/{}\" to \"{}/{}\"", pktChecksum, bArray2Str( packetChecksum ).toUpperCase(), calcChecksum, Long.toHexString( calcChecksum ).toUpperCase() );
            LOG.debug( "\"{}\" is {} equal to \"{}\"", calcChecksum, ( ( calcChecksum == pktChecksum ) ? "" : "not" ), pktChecksum );
        }
        
        return pktChecksum == calcChecksum;
    }

    /**
     * This is a utility message that enables binary decoding even when the binary string is all we have
     *
     * @param rawData
     * @return
     */
    private static String createPacketStringFromBinaryString( String rawData ) {
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
    public static AISPacket createFromBinaryString( String rawData, String source ) throws AISPacketException {
        if( source == null ) source = "UNKNOWN";
        return new AISPacket( createPacketStringFromBinaryString( rawData ), source );
    }

    /**
     *
     * @param s
     * @return
     */
    public static String[] fastSplit( String s ) {
        return fastSplit( s, ',' );
    }

    /**
     * an alternative to String.split() which is a memory hog and performance donkey at scale
     *
     * @param s
     * @param delimiter
     * @return
     */
    private static byte[][] fastSplit( byte[] s, char delimiter ) {
        if( s == null ) {
            return null;
        }

        int count = 1;

        for (byte value : s) {
            if (value == delimiter) {
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
    public static String[] fastSplit( String s, char delimiter ) {
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
    public final String generateTagBlockPacketString( byte [] text ) {
        TagBlock tb = new TagBlock();
        tb.setSource( _source );
        tb.setTimestamp( _timeReceived.getMillis() );
        tb.setTextStr( text );
        return generateTagBlockPacketString( _rawPacket, tb );
    }

    /**
     *
     * @param rawPacket
     * @param tb
     * @return
     */
    private static String generateTagBlockPacketString( byte [] rawPacket, TagBlock tb ) {
        return tb.toString() + bArray2Str( rawPacket );
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
    public final byte [] getRawMessage() {
        return _rawMessage;
    }
    
    /**
     * 
     * @return 
     */
    public final byte [] getPacketBody() {
        return _packetBody;
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
    public final byte [] getChecksum() {
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
    public final byte [] getSource() {
        return _source;
    }

    /**
     *
     * @param source
     */
    public final void setSource( byte [] source ) {
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
    private static int getPacketTruncIndex( StringBuilder sb ) {
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
     * @param strict
     * @param packets
     * @return 
     * @throws jais.exceptions.AISException 
     */
    public static byte [] concatenate( boolean strict, AISPacket ... packets ) throws AISException {
        byte [] compositeMsg = null;
        
        for( AISPacket packet : packets ) {
            if( !strict || packet.isValid() ) {
                if( compositeMsg == null ) {
                    compositeMsg = packet.getRawMessage();
                } else {
                    byte [] temp = new byte[compositeMsg.length + packet.getRawMessage().length];
                    System.arraycopy( compositeMsg, 0, temp, 0, compositeMsg.length );
                    System.arraycopy( packet.getRawMessage(), 0, temp, compositeMsg.length, packet.getRawMessage().length );
                    compositeMsg = temp;
                }
            } else {
                throw new AISException( "Packet : \"" + bArray2Str( packet.getRawPacket() ) + "\" failed validation!" );
            }
        }
        
        return compositeMsg;
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
        HashMap<String,Object> packetMap = new HashMap<>();
        if( packetMap.isEmpty() ) {
            packetMap.put( "tagblock", _tagBlock );
            packetMap.put( "preamble", _preamble );
            packetMap.put( "raw_message", _rawMessage );
            packetMap.put( "raw_packet", _rawPacket );
            packetMap.put( "time_received", _timeReceived );
            packetMap.put( "source", _source );
            packetMap.put( "fragment_count", _fragmentCount );
            packetMap.put( "fragment_number", _fragmentNumber );
            packetMap.put( "sequential_message_id", _sequentialMessageId );
            packetMap.put( "radio_channel_code", _radioChannelCode );
            packetMap.put( "checksum", _checksum );
            packetMap.put( "fill_bits", _fillBits );
        }

        return packetMap;
    }

    /**
     *
     */
    public static class Preamble {

        public final byte [] rawPreamble;
        public char firstChar;
        public boolean isEncapsulated;
        public Talkers talker;
        public boolean isProprietary;
        public Manufacturers manufacturer;
        public byte [] format;
        public boolean isQuery;
        public byte [] parsed;

        /**
         *
         * @param rawPreamble
         */
        public Preamble( byte [] rawPreamble ) {
            this.rawPreamble = rawPreamble;
        }

        /**
         *
         * @return
         */
        public Preamble parse() {
            return parse( AISPacket.bArray2Str( rawPreamble ) );
        }

        /**
         *
         * @param rawPreamble
         * @return
         */
        public static Preamble parse( byte [] rawPreamble ) {
            return parse( AISPacket.bArray2Str( rawPreamble ) );
        }

        /**
         *
         * @param rawPreamble
         * @return
         */
        public static Preamble parse( String rawPreamble ) {
            Preamble p = new Preamble( str2bArray( rawPreamble ) );

            if( LOG.isDebugEnabled() ) LOG.debug( "Parsing {}", rawPreamble );
            Matcher m = PREAMBLE_PATTERN.matcher( rawPreamble );
            if( m.find() ) {
                String parsed = m.group( 0 );
                p.parsed = AISPacket.str2bArray( parsed );
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

                p.format = str2bArray( m.group( 4 ) );
                p.isQuery = m.group( 5 ).equals( "Q" );
            } else {
                if( LOG.isDebugEnabled() ) LOG.warn( "Preamble {} appears to be invalid and does not match the format: {}", rawPreamble, PREAMBLE );
            }

            return p;
        }
    }
}
