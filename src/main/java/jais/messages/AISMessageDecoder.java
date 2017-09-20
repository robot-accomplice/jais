/*
 * Copyright 2016 Jonathan Machen <jon.machen@robotaccomplice.com>.
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
import jais.exceptions.InvalidAISCharacterException;
import jais.exceptions.AISException;
import jais.messages.AISMessage.AISFieldMap;
import jais.messages.enums.AISMessageType;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.BitSet;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Jonathan Machen
 */
public class AISMessageDecoder {

    private final static Logger LOG = LogManager.getLogger( AISMessageDecoder.class );

    public final static int CHAR_RANGE_A_MIN = 48;
    public final static int CHAR_RANGE_A_MAX = 87;
    public final static int CHAR_RANGE_B_MIN = 96;
    public final static int CHAR_RANGE_B_MAX = 119;

    /**
     *
     * @param rawMessage
     * @return
     */
    public static BitSet byteArrayToBitSet( byte [] rawMessage ) {
        return byteArrayToBitSet( rawMessage, AISPacket.DEFAULT_CHARSET );
    }
    
    /**
     *
     * @param rawMessage
     * @param charset
     * @return
     */
    public static BitSet byteArrayToBitSet( byte [] rawMessage, Charset charset ) {
        
        char [] msgChars = charset.decode( ByteBuffer.wrap( rawMessage ) ).array();

        if( LOG.isDebugEnabled() ) LOG.debug( "8 bit char array is {} bytes long.", rawMessage.length );

        BitSet bits = new BitSet( 6 * rawMessage.length );
        //boolean out[] = new boolean[6 * in.length];
        if( LOG.isDebugEnabled() ) LOG.debug( "6 bit boolean array is {} bits long.", bits.size() );

        int bIndex = 0;
        for( char c : msgChars ) {
            try {
                int oc = encodedToSixBitInt( c ); // pull the current raw message char
                if( oc == -1 ) {
                    LOG.info( "Invalid character: '{}'", c );
                    bits.clear();
                    break;
                } else {
                    for( int bPos = 5; bPos >= 0; bPos-- ) {
                        bits.set( bIndex++, 0 < ( oc & ( 1 << bPos ) ) );
                    }
                }
            } catch( InvalidAISCharacterException iace ) {
                LOG.info( "Encountered an invalid character (possible message padding?) : {}", iace.getMessage() );
            }
        }

        return bits;
    }

    /**
     *
     * @param rawMessage
     * @return
     */
    private static BitSet stringToBitSet( String rawMessage ) {

        char[] msgChars = rawMessage.toCharArray();
        if( LOG.isDebugEnabled() ) LOG.debug( "8 bit char array is {} bytes long.", msgChars.length );

        BitSet bits = new BitSet( 6 * msgChars.length );
        //boolean out[] = new boolean[6 * in.length];
        if( LOG.isDebugEnabled() ) LOG.debug( "6 bit boolean array is {} bits long.", bits.size() );

        int bIndex = 0;
        for( char c : msgChars ) {
            try {
                int oc = encodedToSixBitInt( c ); // pull the current raw message char
                if( oc == -1 ) {
                    if( LOG.isDebugEnabled() ) LOG.debug( "Invalid character: '{}'", c );
                    bits.clear();
                    break;
                } else {
                    for( int bPos = 5; bPos >= 0; bPos-- ) {
                        bits.set( bIndex++, 0 < ( oc & ( 1 << bPos ) ) );
                    }
                }
            } catch( InvalidAISCharacterException iace ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Encountered an invalid character (possible message padding?) : {}", iace.getMessage() );
                break;
            }
        }

        return bits;
    }

    /**
     *
     * @param c
     * @return
     * @throws jais.exceptions.InvalidAISCharacterException
     */
    private static int encodedToSixBitInt( char c ) throws InvalidAISCharacterException {
        if( c <= CHAR_RANGE_A_MAX && c >= CHAR_RANGE_A_MIN ) {  // is this character within the first range?
            return c - CHAR_RANGE_A_MIN;
        } else if( c <= CHAR_RANGE_B_MAX && c >= CHAR_RANGE_B_MIN ) {   // is this character within the second range?
            return c - CHAR_RANGE_B_MIN + ( CHAR_RANGE_A_MAX - CHAR_RANGE_A_MIN + 1 );
        } else {
            throw new InvalidAISCharacterException( "Character \'" + c
                    + "\' is outside of either of the acceptable ranges." );
        }
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static int decodeUnsignedInt( BitSet bits, int startBit, int endBit ) throws AISException {
        int rval = 0;

        if( endBit > bits.size() ) {
            throw new AISException( "DecodeInt: position " + endBit + " exceeds input array length " + bits.size() );
        }

        int binPosValue = 1;
        for( int i = endBit; i >= startBit; i-- ) {
            if( bits.get( i ) ) {
                rval += binPosValue;
            }

            binPosValue += binPosValue; // double binPosValue to produce valid 
            // binary position value as per binary math
        }

        return rval;
    }

    /**
     *
     * @param b
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeDraught( BitSet b, int startBit, int endBit )
            throws AISException {
        int intVal = decodeUnsignedInt( b, startBit, endBit );
        return ( ( float ) intVal ) / 10.f;
    }

    /**
     * decode integers in twos-complement form
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static int decodeSignedInt( BitSet bits, int startBit, int endBit ) throws AISException {
        int intValue = 0;

        boolean negative = bits.get( startBit );

        if( negative ) {
            int binPosValue = 1;
            for( int i = endBit; i >= startBit; i-- ) {
                if( !bits.get( i ) ) {
                    intValue += binPosValue;
                }

                // double binPosValue to produce valid 
                // binary position value as per binary math
                binPosValue += binPosValue;
            }
            intValue = -( intValue + 1 );   // correct for dropped bit and add negative
        } else {
            intValue = decodeUnsignedInt( bits, ( startBit + 1 ), endBit );
        }

        return intValue;
    }

    /**
     *
     * @param c
     * @return
     * @throws AISException
     */
    private static char sixBitIntToAscii( int c ) throws AISException {
        int rval = c;

        if( c < 0 || c > 63 ) {
            throw new AISException( "sixBitIntToAscii: illegal input for 6-bit conversion: " + c );
        } else if( c < 32 ) {
            rval += 64;
        }

        return ( char ) rval;
    }

    /**
     *
     * @param packets
     * @return
     * @throws AISException
     */
    public static AISMessageType decodeMessageType( AISPacket... packets ) throws AISException {
        // concatenate full raw message from all packets
        return decodeMessageType( AISPacket.concatenate( packets ) );
    }

    /**
     *
     * @param rawMessage
     * @return
     * @throws AISException
     */
    public static AISMessageType decodeMessageType( String rawMessage ) throws AISException {
        return decodeMessageType( stringToBitSet( rawMessage ) );
    }
    
    /**
     * 
     * @param rawMessage
     * @return
     * @throws AISException 
     */
    public static AISMessageType decodeMessageType( byte [] rawMessage ) throws AISException {
        return decodeMessageType( byteArrayToBitSet( rawMessage ) );
    }

    /**
     *
     * @param bits
     * @return
     * @throws jais.exceptions.AISException
     */
    public static AISMessageType decodeMessageType( BitSet bits )
            throws AISException {

        if( bits.size() < AISFieldMap.TYPE.getEndBit() ) {
            throw new AISException( "BitSet is too short: " + bits.size() );
        }
        if( LOG.isDebugEnabled() ) LOG.debug( "BitSet Size: {}, Start Bit: {}, End Bit: {}", bits.size(),
                AISFieldMap.TYPE.getStartBit(), AISFieldMap.TYPE.getEndBit() );
        int typeId = decodeUnsignedInt( bits, AISFieldMap.TYPE.getStartBit(),
                AISFieldMap.TYPE.getEndBit() );

        return AISMessageType.fetchById( typeId );
    }

    /**
     *
     * @param b
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeLatitude( BitSet b, int startBit, int endBit )
            throws AISException {
        float lat;

        int i = decodeSignedInt( b, startBit, endBit );

        switch( i ) {
            case 0x3412140:
                throw new AISException( "Latitude unavailable." );
            default:
                lat = ( float ) ( ( ( double ) i ) / ( 60f * 10000f ) );
        }

        return lat;
    }

    /**
     *
     * @param b
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeLongitude( BitSet b, int startBit, int endBit )
            throws AISException {
        float lon;

        int i = decodeSignedInt( b, startBit, endBit );

        switch( i ) {
            case 0x6791AC0:
                throw new AISException( "Longitude unavailable." );
            default:
                lon = ( float ) ( ( ( double ) i ) / ( 60f * 10000f ) );
        }

        return lon;
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeTurn( BitSet bits, int startBit, int endBit )
            throws AISException {
        float turn;

        int i = decodeSignedInt( bits, startBit, endBit );

        turn = ( ( float ) i ) / 4.733f;
        turn = turn * turn;

        return turn;
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeSpeed( BitSet bits, int startBit, int endBit ) throws AISException {
        float speed;

        int i = decodeUnsignedInt( bits, startBit, endBit );

        if( ( i < 0 ) || ( i > 1023 ) ) {
            throw new AISException( "getSpeedOverGround: invalid value: " + i );
        }

        switch( i ) {
            case 1023:
                // speed unavailable
                if( LOG.isInfoEnabled() ) LOG.info( "getSpeedOverGround: unavailable: {}", i );
                speed = -1f;
                break;
            case 1022:
                speed = 102.2f;
                break;
            default:
                speed = ( ( float ) i ) / 10.f;
        }

        return speed;
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static float decodeCourse( BitSet bits, int startBit, int endBit )
            throws AISException {
        float course;

        int i = decodeUnsignedInt( bits, startBit, endBit );

        if( ( i < 0 ) || ( i > 3600 ) ) {
            throw new AISException( "decodeCourse: invalid value: " + i );
        }

        switch( i ) {
            case 3600:
                throw new AISException( "Course unavailable" );
            default:
                course = ( ( float ) i ) / 10f;
        }

        return course;
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static String decodeToString( BitSet bits, int startBit, int endBit ) throws AISException {
        return AISPacket.bArray2Str( decodeToByteArray( bits, startBit, endBit ) );
    }

    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @return
     * @throws AISException
     */
    public static byte[] decodeToByteArray( BitSet bits, int startBit, int endBit ) throws AISException {
        return decodeToByteArray( bits, startBit, endBit, AISPacket.DEFAULT_CHARSET );
    }
    
    /**
     *
     * @param bits
     * @param startBit
     * @param endBit
     * @param charset
     * @return
     * @throws AISException
     */
    public static byte[] decodeToByteArray( BitSet bits, int startBit, int endBit, Charset charset ) throws AISException {
        if( LOG.isTraceEnabled() ) LOG.trace( "Decoding bit {} through bit {} of {} BitSet", startBit, endBit, bits.length() );
        CharBuffer cb = CharBuffer.allocate( ( ( ( endBit - startBit ) / 6 ) + 1 ) );
        
        if( endBit > bits.size() ) {
            endBit = bits.size();
        }

        try {
            // we need to walk forward through every set of six bits without
            // travelling past the endBit
            for( int sb = startBit; sb <= endBit; sb += 6 ) {
                int binPosVal = 1;    // binary position value
                int charVal = 0;      // current binary position value
                for( int s = ( sb + 5 ); s >= sb && s <= endBit; s-- ) {
                    if( bits.get( s ) ) {
                        charVal += binPosVal; // sum bits to arrive at int char value
                    }
                    binPosVal += binPosVal; // doubling consistent with binary math
                }

                char c = sixBitIntToAscii( charVal );
                if( c != '@' ) {
                    if( LOG.isTraceEnabled() ) LOG.trace( "Appending character to CharBuffer: {}" , c );
                    cb.put( c );
                }
            }
        } catch( AISException e ) {
            if( LOG.isWarnEnabled() ) LOG.warn( "Could not decode String due to : {}", e.getMessage(), e );
        }
        
        return charset.encode( cb ).array();
    }
}
