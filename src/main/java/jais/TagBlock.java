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

import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
public final class TagBlock {
    
    public final static Logger LOG = LogManager.getLogger( TagBlock.class );
    public final static String TAGBLOCK_STRING = "\\\\(([cdgnrst]{1}\\:[\\-A-Za-z0-9]+)\\,?)+\\*([A-Za-z0-9]{2})[\\\\]{1}";
    public final static Pattern TAGBLOCK_PATTERN = Pattern.compile( TAGBLOCK_STRING );

    String rawTagBlock;
    String checksum;

    // c unix time, positive int
    long timestamp;
    // d destination, alphanumeric (<= 15 chars)
    String destination;
    // g sentence grouping, numeric string (e.g. \g:1-1-1234 or \g:1-2-1234
    String sentenceGrouping;
    // n line count, positive int
    int lineCount;
    // r relative time, positive int
    long relativeTime;
    // s source id, alphanumeric (<= 15 chars)
    String source = "UKNOWN";
    // t text string
    String textStr;

    /**
     *
     */
    public TagBlock() {
    }
    
    /**
     *
     * @return
     */
    public final String getRawTagBlock() {
        return rawTagBlock;
    }

    /**
     *
     * @param rawTagBlock
     */
    public final void setRawTagBlock( String rawTagBlock ) {
        this.rawTagBlock = rawTagBlock;
    }

    /**
     *
     * @return
     */
    public final String getChecksum() {
        return checksum;
    }

    /**
     *
     * @param checksum
     */
    public final void setChecksum( String checksum ) {
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
     * @param timestamp
     */
    public final void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    /**
     *
     * @return
     */
    public final String getDestination() {
        return destination;
    }

    /**
     *
     * @param destination
     */
    public final void setDestination( String destination ) {
        this.destination = destination;
    }

    /**
     *
     * @return
     */
    public final String getSentenceGrouping() {
        return sentenceGrouping;
    }

    /**
     *
     * @param sentenceGrouping
     */
    public final void setSentenceGrouping( String sentenceGrouping ) {
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
     * @param relativeTime
     */
    public final void setRelativeTime( long relativeTime ) {
        this.relativeTime = relativeTime;
    }

    /**
     *
     * @return
     */
    public final String getSource() {
        return source;
    }

    /**
     *
     * @param source
     */
    public void setSource( String source ) {
        this.source = source;
    }

    /**
     *
     * @return
     */
    public final String getTextStr() {
        return textStr;
    }

    /**
     *
     * @param textStr
     */
    public final void setTextStr( String textStr ) {
        this.textStr = textStr;
    }
    
    /**
     * Ignore source (or lack there of) in original tagblock in favor of the provided source value
     * 
     * @param rawTagBlock
     * @param source
     * @return 
     */
    public final static TagBlock parse( String rawTagBlock, String source ) {
        if( source.length() > 15 ) {
            LOG.warn( "Length of source String \"{}\" exceeds 15 character limit",
                    source );
        }
        
        TagBlock tb = new TagBlock();

        // substring starts at 1 to remove leading \
        for( String part : AISPacket.fastSplit( rawTagBlock.substring( 1, rawTagBlock.indexOf( "*" ) ) ) ) {
            LOG.fatal( "Processing: {}", part );
            String[] tag = AISPacket.fastSplit( part, ':' );

            switch( tag[0] ) {
                case "c":
                    tb.setTimestamp( Long.parseLong( tag[1] ) );
                    break;
                case "d":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of destination String \"{}\" exceeds 15 character limit",
                                tag[1] );
                    }
                    tb.setDestination( tag[1] );
                    break;
                case "g":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of sentence grouping String \"{}\" exceeds 15 character limit",
                                tag[1] );
                    }
                    tb.setSentenceGrouping( tag[1] );
                    break;
                case "n":
                    tb.setLineCount( Integer.parseInt( tag[1] ) );
                    break;
                case "r":
                    tb.setRelativeTime( Long.parseLong( tag[1] ) );
                    break;
                case "t":
                    tb.setTextStr( tag[1] );
                    break;
            }
        }
        
        tb.setSource( source );
        tb.setRawTagBlock( tb.toString() );
        
        return tb;
    }

    /**
     *
     * @param rawTagBlock
     * @return
     */
    public final static TagBlock parse( String rawTagBlock ) {
        TagBlock tb = new TagBlock();

        // substring starts at 1 to remove leading \
        for( String part : AISPacket.fastSplit( rawTagBlock.substring( 1, rawTagBlock.indexOf( "*" ) ) ) ) {
            LOG.fatal( "Processing: {}", part );
            String[] tag = AISPacket.fastSplit( part, ':' );

            switch( tag[0] ) {
                case "c":
                    tb.setTimestamp( Long.parseLong( tag[1] ) );
                    break;
                case "d":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of destination String \"{}\" exceeds 15 character limit",
                                tag[1] );
                    }
                    tb.setDestination( tag[1] );
                    break;
                case "g":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of sentence grouping String \"{}\" exceeds 15 character limit",
                                tag[1] );
                    }
                    tb.setSentenceGrouping( tag[1] );
                    break;
                case "n":
                    tb.setLineCount( Integer.parseInt( tag[1] ) );
                    break;
                case "r":
                    tb.setRelativeTime( Long.parseLong( tag[1] ) );
                    break;
                case "s":
                    if( tag[1].length() > 15 ) {
                        LOG.warn( "Length of source String \"{}\" exceeds 15 character limit",
                                tag[1] );
                    }
                    tb.setSource( tag[1] );
                    break;
                case "t":
                    tb.setTextStr( tag[1] );
                    break;
            }
        }

        return tb;
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

        if( this.sentenceGrouping != null && !this.sentenceGrouping.isEmpty() ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "g:" ).append( this.sentenceGrouping );
        }
        if( this.lineCount > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "n:" ).append( this.lineCount );
        }
        if( this.source != null && !this.source.isEmpty() ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "s:" ).append( this.source );
        }
        if( this.timestamp > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "c:" ).append( this.timestamp );
        }
        if( this.destination != null && !this.destination.isEmpty() ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "d:" ).append( this.destination );
        }
        if( this.relativeTime > 0 ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "r:" ).append( this.relativeTime );
        }
        if( this.textStr != null && !this.textStr.isEmpty() ) {
            if( tbs.length() > 1 ) {
                tbs.append( "," );
            }
            tbs.append( "t" ).append( this.textStr );
        }

        // add checksum and close
        this.rawTagBlock = new StringBuilder( "\\" )
                .append( tbs ).append( "*" )
                .append( AISPacket.generateChecksum( tbs.toString() ) ).append( "\\" )
                .toString();

        return this.rawTagBlock;
    }
}
