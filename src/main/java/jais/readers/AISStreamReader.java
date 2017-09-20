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

package jais.readers;

import jais.exceptions.AISPacketException;
import jais.handlers.AISHandler;
import jais.handlers.AISMessageHandler;
import jais.handlers.AISPacketHandler;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISStreamReader extends AISReaderBase {
    
    private final static Logger LOG = LogManager.getLogger( AISStreamReader.class );
    private final static int DEFAULT_BUFFER_SIZE = 8192;

    private final InputStream _input;
    private final int _bufferSize;
    
    /**
     * 
     * @param input 
     */
    public AISStreamReader( InputStream input ) {
        this( input, AISReaderBase.DEFAULT_SOURCE, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param input 
     * @param source 
     */
    public AISStreamReader( InputStream input, String source ) {
        this( input, source, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param input
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, int bufferSize ) {
        this( input, DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param input
     * @param source
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, String source, int bufferSize ) {
        _input = input;
        _source = source;
        _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     */
    public AISStreamReader( InputStream input, AISHandler handler ) {
        this( input, handler, AISReaderBase.DEFAULT_SOURCE );
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param source 
     */
    public AISStreamReader( InputStream input, AISHandler handler, String source ) {
        this( input, handler, source, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, AISHandler handler, int bufferSize ) {
        this( input, handler, AISReaderBase.DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param source 
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, AISHandler handler, String source, int bufferSize ) {
        super( handler, source );
       _input = input;
       _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @param input
     * @param pktHandler
     * @param msgHandler 
     */
    public AISStreamReader( InputStream input, AISPacketHandler pktHandler, AISMessageHandler msgHandler ) {
        this( input, pktHandler, msgHandler, AISReaderBase.DEFAULT_SOURCE, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param input
     * @param pktHandler
     * @param msgHandler 
     * @param bufferSize
     */
    public AISStreamReader( InputStream input, AISPacketHandler pktHandler, AISMessageHandler msgHandler, int bufferSize ) {
        this( input, pktHandler, msgHandler, AISStreamReader.DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param input
     * @param pktHandler
     * @param msgHandler 
     * @param bufferSize
     * @param source
     */
    public AISStreamReader( InputStream input, AISPacketHandler pktHandler, AISMessageHandler msgHandler, String source, int bufferSize ) {
        super( pktHandler, msgHandler, source );
        _input = input;
        _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @throws jais.readers.AISReaderException
     */
    @Override
    public void read() throws AISReaderException {
        LOG.info( "Reading..." );
        
        try( BufferedInputStream bis = new BufferedInputStream( _input, _bufferSize ); 
                InputStreamReader isr = new InputStreamReader( bis ); 
                BufferedReader br = new BufferedReader( isr, _bufferSize );
                ) {

            StringBuilder sb = new StringBuilder();
            CharBuffer cb = CharBuffer.allocate( _bufferSize );
            
            while( super._shouldRun ) {
                try {
                    int readCount = br.read( cb );
                    if( LOG.isDebugEnabled() ) LOG.debug( "{} - Read {} bytes from stream", _source, readCount );
                    
                    for( char c : cb.array() ) {
                        if( c == '\n' || c == '\r' ) {
                            if( sb.length() > 0 ) {
                                String line = sb.toString();
                                sb.delete( 0, line.length() );

                                if( !line.isEmpty() ) {
                                    super.processPacketString( line );
                                }
                            }
                        } else {
                            sb.append( c );
                        }
                    }
                } catch( AISPacketException ae ) {
                    if( LOG.isDebugEnabled() ) LOG.debug( "Encountered an AISException: {}", ae.getMessage(), ae );
                } finally {
                    cb.clear();
                }
            }
        } catch( IOException ioe ) {
            LOG.error( "Encountered an IOException: {}", ioe.getMessage(), ioe );
            throw new AISReaderException( "Encountered an IOException: " + ioe.getMessage(), ioe );
        }
    }
}
