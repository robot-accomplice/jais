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

package jais.readers;

import jais.exceptions.AISPacketException;
import jais.handlers.AISHandler;
import jais.handlers.AISMessageHandler;
import jais.handlers.AISPacketHandler;
import java.io.*;
import org.slf4j.*;

/**
 *
 * @author Jonathan Machen
 */
public class AISStreamReader extends AISReaderBase {
    
    private final static Logger LOG = LoggerFactory.getLogger( AISStreamReader.class );
    private final static int DEFAULT_BUFFER_SIZE = 16384;

    private final InputStream _input;
    private final int _bufferSize;
    
    /**
     * 
     * @param input 
     */
    public AISStreamReader( InputStream input ) {
        this( input, DEFAULT_BUFFER_SIZE );
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
        _input = input;
        _bufferSize = bufferSize;
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
        super( handler );
       _input = input;
       _bufferSize = DEFAULT_BUFFER_SIZE;
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param source 
     */
    public AISStreamReader( InputStream input, AISHandler handler, String source ) {
        super( handler );
       _input = input;
       _bufferSize = DEFAULT_BUFFER_SIZE;
       _source = source;
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, AISHandler handler, int bufferSize ) {
        this( input, handler, DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param input 
     * @param handler 
     * @param source 
     * @param bufferSize 
     */
    public AISStreamReader( InputStream input, AISHandler handler, String source, int bufferSize ) {
        super( handler );
       _input = input;
       _source = source;
       _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @param input
     * @param pktHandler
     * @param msgHandler 
     */
    public AISStreamReader( InputStream input, AISPacketHandler pktHandler, AISMessageHandler msgHandler ) {
        super( pktHandler, msgHandler );
        _input = input;
        _bufferSize = DEFAULT_BUFFER_SIZE;
    }
    
    /**
     * 
     * @param input
     * @param pktHandler
     * @param msgHandler 
     * @param bufferSize
     */
    public AISStreamReader( InputStream input, AISPacketHandler pktHandler, 
            AISMessageHandler msgHandler, int bufferSize ) {
        super( pktHandler, msgHandler );
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
        BufferedReader br = new BufferedReader( new InputStreamReader( new BufferedInputStream( _input, _bufferSize ) ), _bufferSize );
        while( super._shouldRun ) {
            try {
                String line = br.readLine();
                if( line != null && !line.isEmpty() ) super.processPacketString( line );
            } catch( AISPacketException ae ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Encountered an AISException: {}", ae.getMessage(), ae );
            } catch( IOException ioe ) {
                LOG.error( "Encountered an IOException: {}", ioe.getMessage(), ioe );
                throw new AISReaderException( "Encountered an IOException: " + ioe.getMessage(), ioe );
            }
        }
    }
}
