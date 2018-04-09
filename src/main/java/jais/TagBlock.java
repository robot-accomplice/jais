/*
 * Copyright 2017 Jonathan Machen.
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

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An object representing the TagBlock section of an AIS packet.  A "tagblock" is a comma separated collection of fields that appear before the 
 * message preamble (see example below) and provide a spec compliant way of providing metadata about the AIS packet that travels with the packet.  
 * Supported fields include:  
 *      
 *      t - timestamp        : in c unix time and represented as a positive integer 
 *      d - destination      : a string of 15 characters or less indicating a destination
 *      g - sentence grouping: a numeric string used to indicate when messages are associated and their proper message order
 *      n - line count       : a positive integer indicating the number of lines for a given message
 *      r - relative time    : a positive integer representing the relative time differential
 *      s - source id        : a string of 15 characters or less representing the message source
 *      t - text string      : a text string of 15 characters or less containing any data the sender cares to include
 * 
 * Example:
 * 
 *      \g:1-2-73874,n:157036,s:r003669945,c:1241544035,t:*4A\!AIVDM,1,1,,B,15N4cJ`005Jrek0H@9n`DW5608EP,0*13
 */
public final class TagBlock {
    
    public final static Logger LOG = LogManager.getLogger( TagBlock.class );
    public final static String TAGBLOCK_STRING = "\\\\(([cdgnrst]{1}\\:[A-Za-z0-9\\\\-]+\\,?)+)\\*([A-Za-z0-9]{2})\\\\";
    public final static Pattern TAGBLOCK_PATTERN = Pattern.compile( TAGBLOCK_STRING );
    
    boolean _parsed;

    byte [] rawTagBlock;
    byte [] checksum;

    // c unix time, positive int
    long timestamp;
    // d destination, alphanumeric (<= 15 chars)
    byte [] destination;
    // g sentence grouping, numeric string (e.g. \g:1-1-1234 or \g:1-2-1234
    byte [] sentenceGrouping;
    // n line count, positive int
    int lineCount;
    // r relative time, positive int
    long relativeTime;
    // s source id, alphanumeric (<= 15 chars)
    byte [] source = AISPacket.str2bArray( "UKNOWN" );
    // t text string (<= 15 chars)
    byte [] textStr;

    /**
     *
     */
    public TagBlock() {
    }
    
    /**
     * 
     * @return 
     */
    public final boolean isParsed() {
        return _parsed;
    }
    
    /**
     * 
     */
    public final void setParsed() {
        _parsed = true;
    }
    
    /**
     *
     * @return
     */
    public final byte [] getRawTagBlock() {
        return rawTagBlock;
    }

    /**
     *
     * @param rawTagBlock
     */
    public final void setRawTagBlock( byte [] rawTagBlock ) {
        this.rawTagBlock = rawTagBlock;
    }

    /**
     *
     * @return
     */
    public final byte [] getChecksum() {
        return checksum;
    }

    /**
     *
     * @param checksum
     */
    public final void setChecksum( byte [] checksum ) {
        this.checksum = checksum;
    }

    /**
     *
     * @return
     */
    public final long getTimestamp() {
        return timestamp;
    }

    /**
     * 
     * @param offset
     * @return 
     */
    public final ZonedDateTime getTimeStamp( ZoneOffset offset ) {
        return ZonedDateTime.ofInstant( Instant.ofEpochSecond( timestamp ), offset );
    }

    /**
     *
     * @param timestamp
     */
    public final void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }
    
    /**
     * 
     * @return 
     */
    public final boolean hasTimestamp() {
        if( _parsed ) {
            return ( this.timestamp > 0 );
        }
        
        return false;
    }

    /**
     *
     * @return
     */
    public final byte [] getDestination() {
        return destination;
    }
    
    /**
     * 
     * @return 
     */
    public final boolean hasDestination() {
        if( _parsed ) {
            return ( this.destination != null );
        }
        return false;
    }

    /**
     *
     * @param destination
     */
    public final void setDestination( byte [] destination ) {
        this.destination = destination;
    }

    /**
     *
     * @return
     */
    public final byte [] getSentenceGrouping() {
        return sentenceGrouping;
    }

    /**
     * 
     * @return 
     */
    public final boolean hasSentenceGrouping() {
        if( _parsed ) {
            return ( this.sentenceGrouping != null );
        }
        return false;
    }

    /**
     *
     * @param sentenceGrouping
     */
    public final void setSentenceGrouping( byte [] sentenceGrouping ) {
        this.sentenceGrouping = sentenceGrouping;
    }

    /**
     *
     * @return
     */
    public final int getLineCount() {
        return lineCount;
    }

    /**
     * 
     * @return 
     */
    public final boolean hasLineCount() {
        if( _parsed ) {
            return ( this.lineCount != 0 );
        }
        return false;
    }

    /**
     *
     * @param lineCount
     */
    public final void setLineCount( int lineCount ) {
        this.lineCount = lineCount;
    }

    /**
     *
     * @return
     */
    public final long getRelativeTime() {
        return relativeTime;
    }

    /**
     * 
     * @return 
     */
    public final boolean hasRelativeTime() {
        if( _parsed ) {
            return ( this.relativeTime != 0 );
        }
        return false;
    }

    /**
     *
     * @param relativeTime
     */
    public final void setRelativeTime( long relativeTime ) {
        this.relativeTime = relativeTime;
    }

    /**
     *
     * @return
     */
    public final byte [] getSource() {
        return source;
    }

    /**
     * 
     * @return 
     */
    public final boolean hasSource() {
        if( _parsed ) {
            return ( this.source != null );
        }
        return false;
    }

    /**
     *
     * @param source
     */
    public void setSource( byte [] source ) {
        this.source = source;
    }

    /**
     *
     * @return
     */
    public final byte [] getTextStr() {
        return textStr;
    }

    /**
     * 
     * @return 
     */
    public final boolean hasTextStr() {
        if( _parsed ) {
            return ( this.textStr != null );
        }
        return false;
    }

    /**
     *
     * @param textStr
     */
    public final void setTextStr( byte [] textStr ) {
        this.textStr = textStr;
    }
    
    /**
     * 
     * @param source
     * @return 
     */
    public static TagBlock build( byte [] source ) {
        if( source.length > 15 ) {
            source = Arrays.copyOfRange( source, 0, 15 );
            LOG.warn( "Truncating oversized source from {} to {}" );
        }
        
        TagBlock tb = new TagBlock();
        tb.setSource( source );
        tb.setTimestamp( ZonedDateTime.now( ZoneOffset.UTC ).toEpochSecond() );
        
        return tb;
    }
    
    /**
     * Ignore source (or lack there of) in original TagBlock in favor of the provided source value
     * 
     * @param rawTagBlock
     * @param source
     * @return 
     */
    public static TagBlock parse( String rawTagBlock, byte [] source ) {
        if( LOG.isInfoEnabled() ) LOG.info( "Parsing {}", rawTagBlock );
        TagBlock tb = new TagBlock();

        // substring starts at 1 to remove leading \
        for( String part : AISPacket.fastSplit( rawTagBlock.substring( 1, rawTagBlock.indexOf( "*" ) ) ) ) {
            if( LOG.isInfoEnabled() ) LOG.info( "Processing: {}", part );
            String[] tag = AISPacket.fastSplit( part, ':' );

            switch( tag[0] ) {
                case "c":
                    tb.setTimestamp( Long.parseLong( tag[1] ) );
                    break;
                case "d":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of destination String \"{}\" exceeds 15 character limit",tag[1] );
                    }
                    tb.setDestination( AISPacket.str2bArray( tag[1] ) );
                    break;
                case "g":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of sentence grouping String \"{}\" exceeds 15 character limit",tag[1] );
                    }
                    tb.setSentenceGrouping( AISPacket.str2bArray( tag[1] ) );
                    break;
                case "n":
                    tb.setLineCount( Integer.parseInt( tag[1] ) );
                    break;
                case "r":
                    tb.setRelativeTime( Long.parseLong( tag[1] ) );
                    break;
                case "s":
                    if( source == null ) {
                        if( tag[1].length() > 15 ) {
                            LOG.warn( "Length of source String \"{}\" exceeds 15 character limit", tag[1] );
                        }
                        source = AISPacket.str2bArray( tag[1] );
                    }
                    break;
                case "t":
                    tb.setTextStr( AISPacket.str2bArray( tag[1] ) );
                        if( tag[1].length() > 15 ) {
                            LOG.warn( "Length of text String \"{}\" exceeds 15 character limit", tag[1] );
                        }
                    break;
            }
        }
        
        if( source != null ) {
            if( source.length > 15 ) {
                source = Arrays.copyOfRange( source, 0, 15 );
                LOG.warn( "Truncating oversized source 15 characters" );
            }
            tb.setSource( source );
        }
        
        tb.setParsed();
        tb.setRawTagBlock( AISPacket.str2bArray( tb.toString() ) );
        
        return tb;
    }

    /**
     *
     * @param rawTagBlock
     * @return
     */
    public static TagBlock parse( String rawTagBlock ) {
        return parse( rawTagBlock, null );
    }
    
    /**
     *
     * @return
     */
    @Override
    public final String toString() {
        // c unix time, positive int
        // d destination, alphanumeric (<= 15 chars)
        // g sentence grouping, numeric string (e.g. \g:1-1-1234 or \g:1-2-1234
        // n line count, positive int
        // r relative time, positive int
        // s source id, alphanumeric (<= 15 chars)
        // t text string

        StringBuilder tbs = new StringBuilder();

        if( this.sentenceGrouping != null && this.sentenceGrouping.length != 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "g:" ).append( AISPacket.bArray2Str( this.sentenceGrouping ) );
        }
        if( this.lineCount > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "n:" ).append( this.lineCount );
        }
        if( this.source != null && this.source.length != 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "s:" ).append( AISPacket.bArray2Str( this.source ) );
        }
        if( this.timestamp > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "c:" ).append( this.timestamp );
        }
        if( this.destination != null && this.destination.length != 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "d:" ).append( AISPacket.bArray2Str( this.destination ) );
        }
        if( this.relativeTime > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "r:" ).append( this.relativeTime );
        }
        if( this.textStr != null && this.textStr.length != 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "t" ).append( AISPacket.bArray2Str( this.textStr ) );
        }

        tbs = new StringBuilder( "\\" )
                .append( tbs ).append( "*" )
                .append( AISPacket.getChecksum( tbs.toString(), 0, tbs.length() ) ).append( "\\" );
        
        // add checksum and close
        this.rawTagBlock = AISPacket.str2bArray( tbs.toString() );

        return tbs.toString();
    }
}
