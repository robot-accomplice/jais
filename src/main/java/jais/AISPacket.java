/*
 * Copyright 2016-2019 Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}.
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
import jais.exceptions.AISPacketException;
import jais.exceptions.AISPacketParseException;
import jais.messages.enums.Manufacturers;
import jais.messages.enums.Talkers;
import jais.messages.AISMessageDecoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Objects;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public final class AISPacket {

    private final static Logger LOG = LogManager.getLogger(AISPacket.class);

    // reserved characters
    private final static char ENCAP_START = '!';
    private final static char PARAM_START = '$';
    private final static char CHECKSUM_DELIMITER = '*';
    private final static char FIELD_DELIMITER = ',';
    private final static char HEX_DELIMITER = '^';
    private final static char RESERVED_DELIMITER = '~';
    private final static String DEFAULT_SOURCE = "UNSPECIFIED";

    private final static double CHANNEL_A_FREQUENCY_IN_MHZ = 161.975;
    private final static double CHANNEL_B_FREQUENCY_IN_MHZ = 162.025;

    public final static String PREAMBLE = "([!|\\$]{1})([A-Z0-9]{1,2})(([A-Z]{2})([A-Z]{1})){1}";
    public final static Pattern PREAMBLE_PATTERN = Pattern.compile( PREAMBLE );
    public final static Pattern PACKET_PATTERN = Pattern.compile( "(" + TagBlock.TAGBLOCK_STRING + ")?(" + PREAMBLE + "(.*))" );
    public final static int PREAMBLE_GROUPS = 5;
    public final static Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;

    private TagBlock _tagBlock;
    private Preamble _preamble;
    private byte[] _source;
    private byte[] _type;
    private int _fragmentCount = 1;
    private int _fragmentNumber = 1;
    private int _sequentialMessageId = -1;
    private char _radioChannelCode;
    private byte[] _rawPacket;  // the unparsed initial string  
    private byte[] _binaryString; // the binary string 
    private byte[] _packetBody; // the message without the tagblock
    private int _fillBits;
    private byte[] _checksum;
    private final long _timeReceived = ZonedDateTime.now( ZoneOffset.UTC.normalized() ).toInstant().toEpochMilli();
    private byte[][] _packetParts;
    private boolean _parsed = false;

    /**
     * Constructor
     *
     * @param rawPacket A byte [] composed of the characters from the original non-decoded String representing a complete or partial AIS message
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( byte[] rawPacket ) throws AISPacketException {
        this( rawPacket, str2bArray( DEFAULT_SOURCE ) );
    }

    /**
     * Constructor
     *
     * @param rawPacket A byte [] composed of the characters from the original non-decoded String representing a complete or partial AIS message
     * @param source The named source of the AIS packet
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( byte[] rawPacket, byte[] source ) throws AISPacketException {
        if( LOG.isTraceEnabled() ) LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", rawPacket, source );
        _rawPacket = trim( rawPacket );
        _source = trim( source );
    }

    /**
     * Constructor
     *
     * @param rawPacket The original 6 bit encoded String representing a complete or partial AIS message
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket ) throws AISPacketException {
        this( rawPacket, DEFAULT_SOURCE );
    }

    /**
     * Constructor
     *
     * @param rawPacket The original 6 bit encoded String representing a complete or partial AIS message
     * @param source The named source of this AIS packet
     * @throws jais.exceptions.AISPacketException
     */
    public AISPacket( String rawPacket, String source ) throws AISPacketException {
        if( LOG.isTraceEnabled() ) LOG.trace( "Constructor instantiated with: \"{}\", \"{}\"", rawPacket, source );
        _rawPacket = str2bArray( rawPacket );
        if( source != null ) _source = str2bArray( source );
        else _source = str2bArray( DEFAULT_SOURCE );
    }

    /**
     * Validates the AIS packet preamble against a regular expression constant
     *
     * @return a boolean indicating whether or not the preamble is valid
     */
    private boolean validatePreamble() throws AISPacketParseException {
        if( _packetParts == null ) {
            LOG.debug( "_packetParts is null" );
            return false;
        } else if( _packetParts.length == 0 ) {
            LOG.debug( "_packetParts has zero members" );
            return false;
        } else if( _packetParts[0] == null ) {
            LOG.debug( "_packetParts[0] is null" );
            return false;
        } else {
            if( LOG.isTraceEnabled() ) LOG.trace( "Creating preamble object from {}", bArray2Str( _packetParts[0] ) );
            return validatePreamble( Preamble.parse( _packetParts[0] ) );
        }
    }

    /**
     * Validates that the provided Preamble object is non-null and does not contain null fields
     *
     * @param p the Preamble object to evaluate for validity
     * @return a boolean indicating whether or not the preamble is valid
     */
    private static boolean validatePreamble( Preamble p ) {
        return ( ( p != null ) && ( p.talker != null ) && ( p.format != null ) );
    }

    /**
     * Validates the AIS packet preamble against a regular expression constant
     * @param preambleStr the preamble String to evaluate for validity
     * @return a boolean indicating whether or not the preamble is valid
     * @throws AISPacketParseException
     */
    public static boolean validatePreamble( String preambleStr ) throws AISPacketParseException {
        return validatePreamble( Preamble.parse( preambleStr ) );
    }

    /**
     * Fetch the preamble (e.g. !AISVDM)
     * @see jais.AISPacket.Preamble
     * @return a Preamble object
     */
    public final Preamble getPreamble() { return _preamble; }

    /**
     * Determines whether or not a TagBlock was parsed from this AISPacket 
     * @see jais.TagBlock
     *
     * @return a boolean representing whether or not this packet has a TagBlock
     */
    public boolean hasTagBlock() { return ( _tagBlock != null ); }

    /**
     * Returns the TagBlock parsed from this AISPacket
     * @see jais.TagBlock
     * @return the TagBlock for this AISPacket
     */
    public final TagBlock getTagBlock() { return _tagBlock; }

    /**
     * Validates the contents of the packet and breaks it into its constituent parts
     *
     * @return @throws AISPacketException
     */
    public final AISPacket process() throws AISPacketException { return process( false ); }

    /**
     * Validates the contents of the packet and breaks it into its constituent parts, optionally generates a TagBlock
     * for the resulting AISPacket @see jais.TagBlock
     * 
     * @param addTagBlock boolean flag indicating whether or not a TagBlock should be pre-pended to the packet
     * @see jais.TagBlock
     * @return
     * @throws jais.exceptions.AISPacketException
     */
    public final AISPacket process( boolean addTagBlock ) throws AISPacketException {
        String rawPacket;

        if( _rawPacket == null ) throw new AISPacketException( "Raw packet is null" );
        else if( _rawPacket.length == 0 ) throw new AISPacketException( "Raw packet is empty" );
        else {
            rawPacket = bArray2Str( trim( _rawPacket ) );
            if( LOG.isDebugEnabled() ) LOG.debug( "Processing new raw packet: {}", rawPacket );
        }

        Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( rawPacket );
        if( m.find() ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Found a TagBlock in \"{}\"", rawPacket );
            try {
                if( _source == null || _source.length == 0 ) {
                    _tagBlock = TagBlock.parse( m.group( 0 ) );
                    _source = _tagBlock.getSource();
                } else _tagBlock = TagBlock.parse( m.group( 0 ), _source );
            } catch( Throwable t ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Unable to parse TagBlock from {}", m.group( 0 ) );
            }

            _packetBody = str2bArray( rawPacket.substring( m.end() ) );
        } else if( addTagBlock ) {
            if( _source == null || _source.length == 0 ) _tagBlock = TagBlock.build( null );
            else _tagBlock = TagBlock.build( _source );
            _packetBody = _rawPacket;
        } else {
            if( LOG.isDebugEnabled() ) LOG.debug( "No TagBlock found and addTagBlock is false" );
            _packetBody = _rawPacket;
        }

        if( LOG.isDebugEnabled() ) LOG.debug( "_packetBody = \"{}\"", bArray2Str( _packetBody ) );

        if( _packetParts == null ) _packetParts = AISPacket.fastSplit( _packetBody, FIELD_DELIMITER );

        if( _packetParts == null || _packetParts.length < 6 ) 
            throw new AISPacketException( "Raw packet contains no message (inadequate number of comma-separated values)." );

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
                        if( _packetParts[6] != null && _packetParts[6].length > 0 ) {
                            byte[] checksum = _packetParts[6];
                            int csIndex = indexOf( _packetParts[6], CHECKSUM_DELIMITER );
                            if( csIndex != -1 ) {
                                _fillBits = Integer.parseInt( substring( checksum, 0, csIndex ) );
                                _checksum = trim( Arrays.copyOfRange( checksum, csIndex + 1, checksum.length ) );
                            } else if( LOG.isDebugEnabled() ) LOG.debug( "Packet is missing checksum!" );
                        }
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() )
                            LOG.debug( "Failed to set fill bits and/or checksum due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 6:
                    if( _packetParts[5] == null ) throw new AISPacketException( "Raw message is null." );
                    else if( _packetParts[5].length == 0 ) throw new AISPacketException( "Raw message is empty." );
                    _binaryString = _packetParts[5]; // only the binary string
                case 5:
                    if( _packetParts[4] != null && _packetParts[4].length > 0 )
                        _radioChannelCode = ( _packetParts[4].length != 0 ) ? bArray2cArray( _packetParts[4] )[0] : 'Z';
                case 4:
                    try {
                        // default to -1 if no message ID is present
                        if( _packetParts[3] != null && _packetParts[3].length > 0 )
                            _sequentialMessageId = ( _packetParts[3].length == 0 ) ? -1 : Integer.parseInt( bArray2Str( _packetParts[3] ) );
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() )
                            LOG.debug( "Failed to set sequential message ID due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 3:
                    try {
                        if( _packetParts[2] != null && _packetParts[2].length > 0 )
                            _fragmentNumber = Integer.parseInt( bArray2Str( _packetParts[2] ) );
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() )
                            LOG.debug( "Failed to set fragment number due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 2:
                    try {
                        if( _packetParts[1] != null && _packetParts[1].length > 0 )
                            _fragmentCount = Integer.parseInt( bArray2Str( _packetParts[1] ) );
                    } catch( NumberFormatException nfe ) {
                        if( LOG.isDebugEnabled() )
                            LOG.debug( "Failed to set fragment count due to NumberFormatException: {}", nfe.getMessage() );
                    }
                case 1:
                    int index = indexOf( _packetParts[0], AISPacket.ENCAP_START );
                    if( index == -1 ) index = indexOf( _packetParts[0], AISPacket.PARAM_START );

                    if( index != -1 ) _type = Arrays.copyOfRange( _packetParts[0], index, _packetParts[0].length );
                    else throw new AISPacketException( "Packet has an unrecognizable preamble" );
                    break;
                default:
                    throw new AISPacketException( "Packet is corrupt and has no message body." );
            }
        } catch( AISPacketException ape ) {
            throw new AISPacketException( "Encountered a malformed AISPacket: \"" + bArray2Str( _rawPacket ) + "\" - " + ape.getMessage(), ape );
        }

        _parsed = true;
        return this;
    }

    /**
     * Uses the DEFAULT_CHARSET of StandardCharsets.US_ASCII to decode a byte [] into a String
     *
     * @param bytes the byte [] to decode into a String
     * @return the String decoded from the provided byte array
     */
    public static String bArray2Str( byte[] bytes ) { return bArray2Str( bytes, DEFAULT_CHARSET ); }

    /**
     * Decodes a byte [] into a String using the provided Charset
     *
     * @param bytes the byte[] to decode into a String
     * @param cs the Charset that should be used to perform the decode operation
     * @return the String decoded from the provided byte []
     */
    public static String bArray2Str( byte[] bytes, Charset cs ) { return new String( bArray2cArray( bytes, cs ) ); }

    /**
     * Encodes a String into a byte [] using the default Charset (US_ASCII)
     *
     * @param string the String to encode into a byte [] using the DEFAULT_CHARSET of StandardCharsets.US_ASCII
     * @return the byte [] encoded from the provided String
     */
    public static byte[] str2bArray( String string ) { return str2bArray( string, DEFAULT_CHARSET ); }

    /**
     * Encodes a String into a byte array using the provided Charset
     *
     * @param s the String to encode into a byte []
     * @param cs the Charset that should be used to perform the encode operation
     * @return the byte [] encoded from the provided String, using the provided Charset
     */
    public static byte[] str2bArray( String s, Charset cs ) { return cs.encode( s ).array(); }

    /**
     * Decodes a byte [] into a char [] using the default character set (US_ASCII)
     *
     * @param bytes the byte [] to decode into a char [] using the DEFAULT_CHARSET of StandardCharsets.US_ASCII
     * @return the char [] decoded from the provided byte []
     */
    public static char[] bArray2cArray( byte[] bytes ) { return bArray2cArray( bytes, DEFAULT_CHARSET ); }

    /**
     * Decodes a byte [] into a char [] using the provided Charset
     *
     * @param bytes the byte [] to decode into a char []
     * @param cs the Charset that should be used to perform the decode operation
     * @return the char [] decoded from the provided byte [] using the provided Charset
     */
    public static char[] bArray2cArray( byte[] bytes, Charset cs ) { return cs.decode( ByteBuffer.wrap( bytes ) ).array(); }

    /**
     * Encodes a char [] into a byte [] using the provided Charset
     *
     * @param ca the char[] to encode into a byte[]
     * @param cs the Charset that should be used to perform the encode operation
     * @return the encoded byte [] from the provide char [] using the provided Charset
     */
    public static byte[] cArray2bArray( char[] ca, Charset cs ) { return cs.encode( CharBuffer.wrap( ca ) ).array(); }

    /**
     * Encodes a char [] into a byte [] using the default Charset (US_ASCII)
     *
     * @param ca the char[] to encode into a byte[] using the DEFAULT_CHARSET of StandardCharsets.US_ASCII
     * @return the encoded byte [] from the provide char [] using the provided Charset
     */
    public static byte[] cArray2bArray( char[] ca ) { return cArray2bArray( ca, DEFAULT_CHARSET ); }

    /**
     * Performs a String.trim()-like operation on a byte[] using the StandardCharsets.US_ASCII Charset
     *
     * @param bytes the byte[] to be trimmed
     * @return the trimmed byte []
     */
    public static byte[] trim( byte[] bytes ) { return trim( bytes, DEFAULT_CHARSET ); }

    /**
     * Performs a String.trim()-like operation on a byte[] using the specified Charset
     *
     * @param bytes the byte[] to be trimmed
     * @param cs the Charset to use in the conversion of the byte[] into a char[]
     * @return the trimmed byte []
     */
    public static byte[] trim( byte[] bytes, Charset cs ) {
        char[] chars = bArray2cArray( bytes, cs );

        for( int i = chars.length - 1; i > -1; i-- ) {
            if( LOG.isTraceEnabled() ) LOG.trace( "Character at position {} is {}", i, chars[i] );

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
     * Constructs a String object from a subset of a byte []
     *
     * @param bytes
     * @param start
     * @param end
     * @return the substring decoded from the byte [] contained within the provided start index and end index of the provided byte array
     */
    public static String substring( byte[] bytes, int start, int end ) {
        return bArray2Str( Arrays.copyOfRange( bytes, start, end ) );
    }

    /**
     * Attempts to convert a byte [] into an int
     *
     * @param bytes
     * @return an int representation of the provided byte []
     * @throws jais.exceptions.AISException
     */
    public static int getInt( byte[] bytes ) throws AISException {
        if( bytes.length < 4 ) throw new AISException( "The byte array is too short to represent an int" );
        return ByteBuffer.wrap( bytes ).getInt();
    }

    /**
     * Returns the index of the first occurrence of char c in byte [] ba or -1 if the char is not present
     *
     * @param ba
     * @param c
     * @return the first index of the provided char in the provided byte [] or -1 if the char does not exist within this byte []
     */
    public static int indexOf( byte[] ba, char c ) {
        char[] chars = DEFAULT_CHARSET.decode( ByteBuffer.wrap( ba ) ).array();
        for( int i = 0; i < chars.length; i++ ) if( chars[i] == c ) return i;

        return -1;
    }

    /**
     * Returns a boolean indicating whether this AISPacket object has been parsed
     *
     * @return a boolean representing the parse state of this AISPacket object
     */
    public final boolean isParsed() { return _parsed; }

    /**
     * Checks the validity of the current AIS packet by analyzing the length of its String representation, the number of comma separated fields it
     * contains, whether or not it has a valid preamble, whether or not it contains any invalid characters, and whether or not it has a valid
     * checksum
     *
     * @return a boolean value representing the validity of this AISPacket
     */
    public final boolean isValid() {
        StringBuilder packetErrors = new StringBuilder();
        boolean valid = true;
        try {
            // so we don't throw NPEs over the failure to split the raw String
            if (_packetParts == null) process();

            if( LOG.isDebugEnabled() ) LOG.debug( "Validating packetBody: {}", AISPacket.bArray2Str( _packetBody ) );
            
            // validate preamble
            if( _packetBody.length > 82 ) {
                packetErrors.append( "Packet body exceeds maximum allowable size (82 characters).\n" );
                if( LOG.isDebugEnabled() ) return false;
                valid &= false;
            }
            if( _packetParts.length == 0 ) {
                if( LOG.isDebugEnabled() ) return false;
                packetErrors.append( "Unable to split packet by comma.\n" );
                valid &= false;
            }
            if( _packetParts.length != 7 ) {   // validate csv length
                packetErrors.append( "Packet does not have the valid number (7) of comma separated values.\n" );
                if( LOG.isDebugEnabled() ) return false;
                valid &= false;
            }
            if( !validatePreamble() ) {
                packetErrors.append( "Packet has an invalid preamble.\n" );
                if( LOG.isDebugEnabled() ) return false;
                valid &= false;
            }
            
            if( valid ){
                // check for bad characters in binary string
                for( char c : bArray2cArray( _packetParts[5] ) ) {
                    // is this character within an accepted range?
                    if( !( ( c <= AISMessageDecoder.CHAR_RANGE_A_MAX && c >= AISMessageDecoder.CHAR_RANGE_A_MIN )
                            || ( c <= AISMessageDecoder.CHAR_RANGE_B_MAX && c >= AISMessageDecoder.CHAR_RANGE_B_MIN ) ) ) {
                        LOG.fatal( "Packet contains an invalid character: {}", c );
                        return false;
                    }
                }

                // if we don't have any bad characters validate the checksum
                int csIndex = indexOf( _packetBody, CHECKSUM_DELIMITER ) + 1;

                if( csIndex > 0 ) {
                    // validate checksum
                    if( !validateChecksum( _packetBody, _checksum ) ) {
                        LOG.fatal( "Packet failed checksum validation." );
                        return false;
                    }
                } else {
                    LOG.fatal( "Packet is missing fillbits and/or checksum." );
                    return false;
                }
            }
        } catch( AISPacketException ape ) {
            // do nothing
            if( LOG.isDebugEnabled() ) LOG.debug( "Packet validation faied: {}", ape.getMessage(), ape );
            return false;
        } finally {
            if( !LOG.isDebugEnabled() && !valid ) LOG.debug( "Invalid Packet: {} {}", packetErrors, _rawPacket );
        }
        

        return valid;
    }

    /**
     * Generates a valid checksum based on the provided char []
     *
     * @param source the source char [] for which you wish to generate a checksum
     * @return a generated int checksum for the provided char []
     */
    private static int generateChecksum( char[] source ) {
        if( LOG.isDebugEnabled() ) LOG.debug( "Generating checksum for String \"{}\"", new String( source ) );

        int crc = 0;
        for ( char aSource : source )  crc ^= aSource;

        if ( LOG.isDebugEnabled() ) LOG.debug( "Generated CRC = {}(int)/{}(hex)", crc, Integer.toHexString( crc ) );

        return crc;
    }

    /**
     * Generates a valid checksum based on the provided String
     *
     * @param sourceString the source String fro which you wish to generate a checksum
     * @return a generated int checksum for the provided String
     */
    public static String generateChecksumString( String sourceString ) {
        String hexString = Integer.toHexString( generateChecksum( sourceString.toCharArray() ) );

        hexString = ( hexString.length() == 1 ) ? "0" + hexString : hexString;

        LOG.debug( "Produced hex string {} from sourceString {}", hexString, sourceString );

        return hexString;
    }

    /**
     * Attempts to parse a checksum from the provided String and generates a new one if the parsing operation is unsuccessful
     *
     * @param data the AIS packet string for which you wish to parse the checksum
     * @return the int checksum for the provided string
     */
    private static int getChecksum( String data ) {
        int index = data.indexOf( String.valueOf( CHECKSUM_DELIMITER ) );
        if( index > -1 ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Found * at {}", index );
            return getChecksum( data, 1, data.indexOf( ( String.valueOf( CHECKSUM_DELIMITER ) ) ) );
        } else {
            LOG.debug( "Index was {}", index );
            return getChecksum( data, 1, data.length() );
        }
    }

    /**
     * Generates a checksum for the provided byte []
     *
     * @param bytes the byte [] from which you wish to extract a checksum
     * @return the int checksum for the provided byte []
     */
    private static int getChecksum( byte[] bytes ) {
        return AISPacket.generateChecksum( bArray2cArray( Arrays.copyOfRange( bytes, 1, indexOf( bytes, CHECKSUM_DELIMITER ) ) ) );
    }

    /**
     * Generates a checksum for the substring (based on int startFrom and int endAt indices) of String genString 
     *
     * @param genString the String for which you wish to generate a checksum
     * @param startFrom the int start index of the substring
     * @param endAt the int end index of the substring
     * @return
     */
    public static int getChecksum( String genString, int startFrom, int endAt ) {
        if( endAt <= startFrom || endAt > genString.length() ) return -1;

        return AISPacket.generateChecksum( genString.substring( startFrom, endAt ).toCharArray() );
    }

    /**
     * Validates the provided checksum (byte [] packetChecksum) by generating a new checksum for byte [] data and comparing them
     *
     * @param data the byte [] to which the provided packetChecksum should apply
     * @param packetChecksum a byte [] representation of the checksum to be validated
     * @return
     */
    private static boolean validateChecksum( byte[] data, byte[] packetChecksum ) {
        long calcChecksum;
        long pktChecksum;

        byte[] trimmed = trim( data );

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
     * A utility method that enables binary decoding even when the binary string is all we have
     *
     * @param rawData the binary String from an AIS packet String which has no prefix or suffix
     * @return a generated String representation of a complete AIS packet (with prefix, suffix, checksum, etc)
     */
    public static String createPacketStringFromBinaryString( String rawData ) {
        String packetString = "!AIVDM,1,1,,A," + rawData + ",0*";
        LOG.debug( "Packet before checksum: {}", packetString );
        packetString += Integer.toHexString( AISPacket.getChecksum( packetString ) );
        LOG.debug( "Packet after checksum: {}", packetString );

        return packetString;
    }

    /**
     * A utility method that creates an AISPacket object based solely on the 6-bit encoded String from an AIS packet
     * String
     * 
     * @param rawData The binary encoded String
     * @return an AISPacket object based on the provided binary string
     * @throws jais.exceptions.AISPacketException 
     */
    public static AISPacket createFromBinaryString(String rawData) throws AISPacketException {
        return createFromBinaryString(rawData, null);
    }

    /**
     * Generates an AISPacket object from a raw 6-bit encoded String and a String representing the data source
     *
     * @param rawData The binary encoded String
     * @param source A string representing the source from which this packet originated
     * @return an AISPacket object based on the provided binary string
     * @throws jais.exceptions.AISPacketException
     */
    public static AISPacket createFromBinaryString( String rawData, String source ) throws AISPacketException {
        if( source == null ) source = "UNKNOWN";
        return new AISPacket( createPacketStringFromBinaryString( rawData ), source );
    }

    /**
     * Splits the provided String by commas
     *
     * @param toSplit The String we wish to split
     * @return a String [] containing the split elements of the source String
     */
    public static String[] fastSplit( String toSplit ) { return fastSplit( toSplit, ',' ); }

    /**
     * An alternative to String.split() (which is a memory hog and performance donkey at scale)
     *
     * @param toSplit the byte [] you wish you to subdivide
     * @param delimiter the char that will be the basis for splitting
     * @return a byte [][] containing the segmentation of the provided byte [] based on the provided delimiter
     */
    private static byte[][] fastSplit( byte[] toSplit, char delimiter ) {
        if( toSplit == null ) return null;

        int count = 1;
        for( byte value : toSplit ) if ( value == delimiter ) count++;

        byte[][] array = new byte[count][];

        int a = -1;
        int b = 0;

        for( int i = 0; i < count; i++ ) {
            while( b < toSplit.length && toSplit[b] != delimiter ) b++;

            array[i] = Arrays.copyOfRange( toSplit, a + 1, b );
            a = b;
            b++;
        }

        return array;
    }

    /**
     * An alternative to String.split() (which is a memory hog and performance donkey at scale)
     *
     * @param toSplit the String you wish to subdivide
     * @param delimiter the char that will be the basis of the subdivision
     * @return a String [] containing the segmented version of the provided String based on the provided char delimiter
     */
    public static String[] fastSplit( String toSplit, char delimiter ) {
        if( toSplit == null ) return null;

        int count = 1;
        
        for( int i = 0; i < toSplit.length(); i++ ) if ( toSplit.charAt( i ) == delimiter ) count++;

        String[] array = new String[count];

        int a = -1;
        int b = 0;

        for( int i = 0; i < count; i++ ) {
            while( b < toSplit.length() && toSplit.charAt( b ) != delimiter ) b++;

            array[i] = toSplit.substring( a + 1, b );
            a = b;
            b++;
        }

        return array;
    }

    /**
     * Returns the unparsed, non-decoded raw AISPacket contents as a byte []
     *
     * @return the byte [] containing the raw, non-decoded AISPacket data
     */
    public final byte[] getRawPacket() { return _rawPacket; }

    /**
     * Generates a String representation of the AISPacket with a pre-pended TagBlock String which contains only 
     * the source and time stamp values of the AISPacket object
     *
     * @return a String representation of the AISPacket with a pre-pended TagBlock String
     */
    public final String generateTagBlockPacketString() {
        TagBlock tb = new TagBlock();
        tb.setSource( _source );
        tb.setTimestamp( _timeReceived );
        return generateTagBlockPacketString( _rawPacket, tb );
    }

    /**
     * Generates a String representation of the AISPacket with a pre-pended TagBlock String containing the source and time stamp values of the 
     * AISPacket object as well as the text provided at method invocation
     *
     * @param text
     * @return
     */
    public final String generateTagBlockPacketString( byte[] text ) {
        TagBlock tb = new TagBlock();
        tb.setSource( _source );
        tb.setTimestamp( _timeReceived );
        tb.setTextStr( text );
        return generateTagBlockPacketString( _rawPacket, tb );
    }

    /**
     * Generates a String representation of the AISPacket with its pre-pended TagBlock String as already defined
     *
     * @param rawPacket
     * @param tb
     * @return
     */
    private static String generateTagBlockPacketString( byte[] rawPacket, TagBlock tb ) {
        return tb.toString() + bArray2Str( rawPacket );
    }

    /**
     *
     * @return
     */
    public final byte[] getType() { return _type; }

    /**
     * Returns the AIS message fragmentation count as defined in the original non-decoded AIS Packet String
     * This count indicates how many related AIS packets the complete AIS message is composed of
     *
     * @return
     */
    public final int getFragmentCount() { return _fragmentCount; }

    /**
     * Returns the AIS message fragmentation number as defined in the original non-decoded AIS Packet String
     * This number indicates the position of this fragment in the fully assembled AIS message
     *
     * @return
     */
    public final int getFragmentNumber() { return _fragmentNumber; }

    /**
     * This returns the value of the locally distinct "talker" sending this and other AIS messages so that they can easily be grouped by source
     *
     * @return
     */
    public final int getSequentialMessageId() { return _sequentialMessageId; }

    /**
     * Returns the letter representation of the Radio frequency on which this AIS packet was broadcast
     *
     * @return
     */
    public final char getRadioChannelCode() { return _radioChannelCode; }

    /**
     * Returns the actual numeric frequency (as a double) indicated by the _radioChannelCode (see {@link #getRadioChannelCode()})
     *
     * @return
     */
    public final double getRadioChannelFrequencyInMhz() {
        switch( _radioChannelCode ) {
            case 'a': return CHANNEL_A_FREQUENCY_IN_MHZ;
            case 'b': return CHANNEL_B_FREQUENCY_IN_MHZ;
        }

        return 0d;
    }

    /**
     * Returns the contents of the non-decoded binary string as a byte []
     *
     * @return
     */
    public final byte[] getBinaryStringAsByteArray() { return _binaryString; }

    /**
     * Returns the body of the AIS Packet as a byte []
     *
     * @return
     */
    public final byte[] getPacketBodyAsByteArray() { return _packetBody; }

    /**
     * Returns the parsed count of fill bits from the AIS packet String
     *
     * @return
     */
    public final int getFillBits() { return _fillBits; }

    /**
     * Returns the parsed or generated checksum from the AIS packet
     *
     * @return
     */
    public final byte[] getChecksum() { return _checksum; }

    /**
     * Returns the time at which this AISPacket object was instantiated
     *
     * @return
     */
    public final long getTimeReceived() { return _timeReceived; }

    /**
     * Returns the time at which this AISPacket object was instantiated for the specified ZoneOffset
     *
     * @param offset
     * @return
     */
    public final ZonedDateTime getTimeReceived( ZoneOffset offset ) {
        return ZonedDateTime.ofInstant( Instant.ofEpochMilli( _timeReceived ), offset );
    }

    /**
     * Returns the time at which this AISPacket object was instantiated for the specified ZoneId
     *
     * @param zone
     * @return
     */
    public final ZonedDateTime getTimeReceived( ZoneId zone ) {
        return ZonedDateTime.ofInstant( Instant.ofEpochMilli( _timeReceived ), zone );
    }

    /**
     * Returns the time at which this AISPacket object was presumably sent based on the timestamp contained within the TagBlock
     *
     * @return
     */
    public final long getTimeSent() {
        if( hasTagBlock() ) return _tagBlock.getTimestamp();
        return 0;
    }

    /**
     * Returns the source of the AISPacket as a byte []
     *
     * @return
     */
    public final byte[] getSource() { return _source; }

    /**
     * Sets the source of the AISPacket to the provided byte [] value
     *
     * @param source
     */
    public final void setSource( byte[] source ) { _source = source; }
    
    /**
     * Truncates the provided StringBuilder object based on the index returned by {@link #getPacketTruncIndex( StringBuilder sb )} 
     * 
     * @param sb
     * @return 
     */
    public static String truncatePacket( StringBuilder sb ) {
        return truncatePacket( sb.toString() );
    }

    /**
     * Truncates the provided StringBuilder object based on the index returned by {@link #getPacketTruncIndex( StringBuilder sb )} 
     *
     * @param s
     * @return
     */
    public static String truncatePacket( String s ) {
        int truncIndex = AISPacket.getPacketTruncIndex( s );
        String substring = null;

        if( truncIndex != -1 ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Truncating: {}", s );
            substring = s.substring( 0, truncIndex );
        }

        return substring;
    }

    /**
     *
     * @param sb
     * @return
     */
    private static int getPacketTruncIndex( String s ) {
        int truncIndex = 0;

        if( LOG.isDebugEnabled() ) LOG.debug( "Evaluating \"{}\" for truncation point.", s );

//        String [] sansTagBlock = s.split( "\\\\!" );
//        
//        Matcher m = AISPacket.PREAMBLE_PATTERN.matcher( ( sansTagBlock.length > 1) ? sansTagBlock[1] : sansTagBlock[0] );
        
        Matcher m = AISPacket.PREAMBLE_PATTERN.matcher( s );
        if( m.find() ) {
            if( s.contains("\n") ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "String is terminated by a newline" );
                truncIndex = s.indexOf( "\n" );
            } else if( s.contains("\r") ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "String is terminated by a carriage return" );
                truncIndex = s.indexOf( "\r" );
            } else if( m.find() ) {
                truncIndex = s.indexOf( m.group( 0 ), 1 );
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
     * Combines two or more AISPacket objects into a single non-decoded AIS message and returns the results as a byte []
     *
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static byte[] concatenate( AISPacket... packets ) throws AISException {
        if( packets.length == 1 ) {
            if( !packets[0].isParsed() ) packets[0].process();
            return packets[0].getBinaryStringAsByteArray();
        }

        byte[] compositeMsg = null;

        if( LOG.isDebugEnabled() ) LOG.debug( "Concatenating {} packets.", packets.length );
        for( AISPacket packet : packets ) {
            if( packet == null ) {
                if( LOG.isWarnEnabled() ) LOG.warn( "Skipping null packet in {} length array of packets.", packets.length );
                continue;
            } else if ( !packet.isParsed() ) packet.process();

            byte [] bytes = packet.getBinaryStringAsByteArray();
            if( compositeMsg == null ) compositeMsg = bytes;
            else {
                byte[] temp = new byte[compositeMsg.length + bytes.length];
                System.arraycopy( compositeMsg, 0, temp, 0, compositeMsg.length );
                System.arraycopy( bytes, 0, temp, compositeMsg.length, bytes.length );
                compositeMsg = temp;
            }
        }

        return compositeMsg;
    }

    /**
     * An override of Object.equals()
     *
     * @param o
     * @return
     */
    @Override
    public final boolean equals( Object o ) {
        if( o == null ) return false;
        if( !( o instanceof AISPacket ) ) return false;

        AISPacket that = ( AISPacket ) o;
        if( that.getRawPacket() == null ) return false;

        return ( Arrays.equals( that.getRawPacket(), _rawPacket ) && Objects.equals( that.getSource(), _source )) ;
    }

    /**
     * An override of Object.hashCode()
     *
     * @return
     */
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 79 * hash + ( _rawPacket != null ? Arrays.hashCode( _rawPacket ) : 0 );
        hash = 79 * hash + ( _source != null ? Arrays.hashCode( _source ) : 0 );
        return hash;
    }

    /**
     * Returns a HashMap representation of the AISPacket fields
     *
     * @return
     */
    public final HashMap<String, Object> toMap() {
        HashMap<String, Object> packetMap = new HashMap<>();
        if( packetMap.isEmpty() ) {
            packetMap.put( "tagblock", _tagBlock );
            packetMap.put( "preamble", _preamble );
            packetMap.put( "raw_message", _binaryString );
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
     * An object representation of an AISPacket preamble
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
        public Preamble( byte[] rawPreamble ) { this.rawPreamble = rawPreamble; }

        /**
         * Populates the fields of this Preamble object based on the parsing of it's rawPreamble byte [] and returns this Preamble object
         *
         * @return
         * @throws jais.exceptions.AISPacketParseException
         */
        public Preamble parse() throws AISPacketParseException { return parse( this, AISPacket.bArray2Str( rawPreamble ) ); }

        /**
         * Returns a Preamble object based on the parsing of the provided raw preamble byte []
         *
         * @param rawPreamble
         * @return
         * @throws jais.exceptions.AISPacketParseException
         */
        public static Preamble parse( byte[] rawPreamble ) throws AISPacketParseException { return parse( AISPacket.bArray2Str( rawPreamble ) ); }

        /**
         * Returns a Preamble object based on the parsing of the provided raw preamble String
         *
         * @param rawPreamble
         * @return
         * @throws jais.exceptions.AISPacketParseException
         */
        public static Preamble parse( String rawPreamble ) throws AISPacketParseException { return parse( new Preamble( AISPacket.str2bArray( rawPreamble ) ), rawPreamble ); }

        /**
         * Parses the provided rawPreamble String and populates the fields of the provided Preamble object before returning it
         *
         * @param p
         * @param rawPreamble
         * @return
         * @throws AISPacketParseException
         */
        public static Preamble parse( Preamble p, String rawPreamble ) throws AISPacketParseException {
            if( LOG.isDebugEnabled() ) LOG.debug( "Parsing {}", rawPreamble );
            Matcher m = PREAMBLE_PATTERN.matcher( rawPreamble );
            if( m.find() ) {
                String parsed = m.group( 0 );
                p.parsed = AISPacket.str2bArray( parsed );
                if( LOG.isDebugEnabled() ) LOG.debug( "Found {} matcher groups: {}=({})({})({})({})", m.groupCount(), 
                        m.group(), m.group( 1 ), m.group( 2 ), m.group( 4 ), m.group( 5 ) );
                p.firstChar = m.group( 1 ).charAt( 0 );

                if( p.firstChar == '!' ) p.isEncapsulated = true;
                else if( m.group( 1 ).equals( "$" ) )  p.isEncapsulated = false;
                else {
                    LOG.debug( "Unrecognized starting character in address field: {}", m.group( 1 ) );
                    p.isEncapsulated = false;
                }

                if( m.group( 3 ).startsWith( "P" ) ) {
                    p.talker = Talkers.P;
                    p.manufacturer = Manufacturers.valueOf( ( m.group( 3 ) + m.group( 4 ) ).toUpperCase() );
                } else if( Talkers.isValid( m.group( 2 ).toUpperCase() ) ) {
                    p.talker = Talkers.valueOf( m.group( 2 ).toUpperCase() );
                } else {
                    p.talker = null;
                    LOG.debug( "Unrecognized/invalid talker type: {}", m.group( 2 ) );
                }

                p.format = str2bArray(m.group(4));
                p.isQuery = m.group(5).equals("Q");
            } else {
                LOG.debug( "Preamble {} appears to be invalid and does not match the format: {}", rawPreamble, PREAMBLE );
            }

            return p;
        }
    }
}
