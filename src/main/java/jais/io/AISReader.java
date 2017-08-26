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

package jais.io;

import jais.handlers.AISStringHandler;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Optional;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class AISReader implements Runnable, AutoCloseable {
    
    private final static Logger LOG = LogManager.getLogger( AISReader.class );
    
    private final String _name;
    private final Socket _socket;
    private final int _readBufferSize;
    
    private boolean _keepReading = true;
    
    private final ExecutorCompletionService<String> _readQueue;
    private final AISStringHandler _handler;
    private final LongAdder _current;
    private final LongAdder _session;
    private DateTime _lastReadTime;
    
    /**
     * 
     * @param name
     * @param socket
     * @param readBufferSize
     * @param readQueue
     * @param readCounter
     * @param readTotal
     * @param handler
     */
    public AISReader( String name, Socket socket, int readBufferSize, ExecutorCompletionService<String> readQueue, 
            LongAdder readCounter, LongAdder readTotal, AISStringHandler handler ) {
        _name = name;
        _socket = socket;
        _readBufferSize = readBufferSize;
        _readQueue = readQueue;
        _current = readCounter;
        _session = readTotal;
        _handler = handler;
    }

    /**
     * 
     */
    @Override
    public void run() {
        try {
            LOG.info( "{} - Getting InputStream...", _name );
            InputStream in = _socket.getInputStream();
            BufferedInputStream bin = new BufferedInputStream( in );
            LOG.info( "{} - Creating BufferedReader...", _name );
            BufferedReader reader = new BufferedReader( new InputStreamReader( bin ) );
            
            LOG.info( "{} - Reading from stream...", _name );
            StringBuilder sb = new StringBuilder();
            CharBuffer cb = CharBuffer.allocate( _readBufferSize );
            
            while( _keepReading && isConnected() ) {
                try {
                    if( !isConnected() ) {
                        throw new IOException( _name + " - Socket is closed." );
                    }
                    int readCount = reader.read( cb );
                    if( readCount > 0 ) {
                        _lastReadTime = DateTime.now();
                    }
                    if( LOG.isDebugEnabled() ) LOG.debug( "{} - Read {} bytes from stream", _name, readCount );
                    
                    for( char c : cb.array() ) {
                        if( sb.length() > 0 ) {
                            if( c == '\n' || c == '\r' ) {
                                String line = sb.toString();
                                sb.delete( 0, line.length() );

                                if( !line.trim().isEmpty() ) {
                                    if( _readQueue != null ) {
                                        _readQueue.submit( () -> {
                                            if( LOG.isInfoEnabled() ) LOG.info( "{} - Submitting \"{}\" to read queue...", _name, line );
                                            _current.increment();
                                            _session.increment();
                                        }, line );
                                    }
                                    
                                    if( _handler != null ) {
                                        if( LOG.isInfoEnabled() ) LOG.info( "{} - Submitting \"{}\" to AISStringHandler...", _name, line );
                                        _handler.processString( line );
                                    }
                                }
                            } 
                        }
                        
                        sb.append( c );
                    }
                    
                    cb.clear();
                } catch( RejectedExecutionException ree ) {
                    LOG.error( "{} - Queue size has been reached!  Pausing reads for 15 seconds...", _name );
                    if( LOG.isTraceEnabled() ) LOG.trace( "{} - {}", _name, ree.getMessage(), ree );
                    try {
                        Thread.sleep( 15000 );
                    } catch( InterruptedException ie ) {
                        // ignore
                    }
                }
            }
        } catch( IOException ioe ) {
            LOG.error( "{} - IOException encountered: {}", _name, ioe.getMessage() );
            if( LOG.isTraceEnabled() ) LOG.trace( ioe );
        } finally {
            try {
                close();
            } catch( Exception e ) {
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    public Optional<DateTime> getLastReadTime() {
        return Optional.ofNullable( _lastReadTime );
    }

    /**
     * @return
     */
    private boolean isConnected() {
        return ( _socket != null && !_socket.isClosed() && _socket.isConnected() && !_socket.isInputShutdown() );
    }
    
    /**
     * 
     * @throws Exception 
     */
    @Override
    public void close() throws Exception {
        LOG.fatal( "{} - AISReader shutting down...", _name );
        
        _keepReading = false;
        
        LOG.fatal( "{} - AISReader successfully closed.", _name );
    }
}
