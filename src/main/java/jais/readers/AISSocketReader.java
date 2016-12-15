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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.joda.time.DateTime;

/**
 *
 * @author Jonathan Machen
 */
public class AISSocketReader extends AISReaderBase {
    
    private final static Logger LOG = LogManager.getLogger( AISSocketReader.class );
    private final static int DEFAULT_BUFFER_SIZE = 16384;

    private final int _bufferSize;
    private final Socket _s;
    
    private final int _timeout = 30000;
    
    /**
     * 
     * @param s 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s ) throws IOException {
        _s = s;
        _bufferSize = DEFAULT_BUFFER_SIZE;
    }

    /**
     * 
     * @param s
     * @param bufferSize 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, int bufferSize ) throws IOException {
        _s = s;
        _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @param s 
     * @param handler 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISHandler handler ) throws IOException {
        this( s, handler, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param s
     * @param handler 
     * @param bufferSize 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISHandler handler, int bufferSize ) throws IOException {
        super( handler );
        _s = s;
        _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, 
            AISMessageHandler msgHandler ) throws IOException {
        this( s, pktHandler, msgHandler, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler 
     * @param bufferSize
     * @throws java.io.IOException
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, 
            AISMessageHandler msgHandler, int bufferSize ) throws IOException {
        super( pktHandler, msgHandler );
        _s = s;
        _bufferSize = bufferSize;
    }
    
    /**
     * 
     * @throws jais.readers.AISReaderException
     */
    @Override
    public void read() throws AISReaderException {
        
        try( InputStream is = _s.getInputStream(); 
                BufferedInputStream bis = new BufferedInputStream( is, _bufferSize ); 
                InputStreamReader isr = new InputStreamReader( bis ); 
                BufferedReader br = new BufferedReader( isr, _bufferSize ) ) {
            LOG.info( "Reading..." );
            
            DateTime lastRead = DateTime.now();
            
            while( super._shouldRun && _s.isConnected() && !_s.isInputShutdown() ) {
                try {
                    if( lastRead.plusMillis( _timeout ).isBeforeNow() ) 
                        throw new AISReaderException( "Reader timed out!  No new data in " + _timeout + "ms." );
                    
                    String line = br.readLine();
                    if( line != null && !line.isEmpty() ) {
                        super.processPacketString( line );
                        lastRead = DateTime.now();
                    }
                } catch( AISPacketException ae ) {
                    LOG.debug( "Encountered an AISException: {}", ae.getMessage(), ae );
                } catch( IOException ioe ) {
                    LOG.error( "Encountered an IOException: {}", ioe.getMessage(), ioe );
                    throw new AISReaderException( "Encountered an IOException: " + ioe.getMessage(), ioe );
                }
            }
        } catch( IOException ioe ) {
            throw new AISReaderException( "Unable to read from socket: " + ioe.getMessage(), ioe );
        } finally {
            try {
                LOG.error( "Socket is no longer valid.  Closing." );
                _s.close();            
            } catch( IOException ioe ) {
                // do nothing
            }
        }
        
        LOG.fatal( "Read terminated." );
    }
}