/*
 * Copyright 2016 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class AISSocketReader implements Runnable, AutoCloseable {
    
    private final static Logger LOG = LogManager.getLogger(AISSocketReader.class );
    
    private final String _name;
    private Socket _socket;
    private final int _readBufferSize;
    
    private boolean _keepReading = true;
    
    private final ExecutorCompletionService<Optional<String>> _readQueue;
    private final AISStringHandler _handler;
    private final LongAdder _current;
    private ZonedDateTime _lastReadTime;
    
    /**
     * 
     * @param name
     * @param socket
     * @param readBufferSize
     * @param readQueue
     * @param readCounter
     * @param handler
     */
    public AISSocketReader( String name, Socket socket, int readBufferSize, ExecutorCompletionService<Optional<String>> readQueue, 
            LongAdder readCounter, AISStringHandler handler ) {
        _name = name;
        _socket = socket;
        _readBufferSize = readBufferSize;
        _readQueue = readQueue;
        _current = readCounter;
        _handler = handler;
    }

    /**
     * 
     */
    @Override
    public void run() {
        if( LOG.isInfoEnabled() ) LOG.info( "{} - Reading from stream...", _name );
        
        StringBuilder sb = new StringBuilder();
        CharBuffer cb = CharBuffer.allocate( _readBufferSize );
        
        while( _keepReading ) {
            try( InputStream in = _socket.getInputStream(); BufferedInputStream bin = new BufferedInputStream( in ); 
                    BufferedReader reader = new BufferedReader( new InputStreamReader( bin ) ) ) {
                while( isConnected() ) {
                    try {
                        int readCount = reader.read( cb );
                        if( LOG.isDebugEnabled() ) LOG.debug( "{} - Read {} bytes from stream", _name, readCount );

                        if( readCount > 0 ) {
                            _lastReadTime = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
                        }

                        for( char c : cb.array() ) {
                            if( c == '\n' || c == '\r' ) { // if we encounter a line terminator, process StringBuilder contents
                                if( sb.length() > 0 ) { // if StringBuilder is not empty, submit contents to queue and/or handler
                                    String submitStr = sb.toString().trim(); // save StringBuilder content
                                    sb.delete( 0, sb.length() ); // clear StringBuilder

                                    if( submitStr == null || submitStr.isEmpty() ) {
                                        if( LOG.isDebugEnabled() ) LOG.debug( "{} - Trimmed String was null or empty", _name );
                                    } else {
                                        if( _readQueue != null ) {
                                            _readQueue.submit( () -> {
                                                if( LOG.isInfoEnabled() ) LOG.info( "{} - Submitting \"{}\" to read queue...", _name, submitStr );
                                                if( _current != null ) _current.increment();
                                            }, Optional.ofNullable( submitStr ) );
                                        }

                                        if( _handler != null ) {
                                            if( LOG.isInfoEnabled() ) LOG.info( "{} - Submitting \"{}\" to AISStringHandler...", _name, submitStr );
                                            _handler.processString( submitStr );
                                        }
                                    }
                                }
                            } else { // if not a line terminator, append the new character to the StringBuilder
                                sb.append( c );
                            }
                        }

                        cb.clear(); // clear the buffer now that we've processed everything
                    } catch( RejectedExecutionException ree ) {
                        LOG.error( "{} - Queue element rejected!  Clearing CharBuffer and pausing reads for 15 seconds...", _name );
                        cb.clear();
                        if( LOG.isTraceEnabled() ) LOG.trace( "{} - {}", _name, ree.getMessage(), ree );
                        try {
                            Thread.sleep( 15000 );
                        } catch( InterruptedException ie ) {
                            // ignore
                        }
                    }
                }
                LOG.warn( "{} - Connection lost.", _name );
            } catch( IOException ioe ) {
                LOG.warn( "{} - IOException encountered: {}", _name, ioe.getMessage() );
                if( LOG.isTraceEnabled() ) LOG.trace( ioe );
                try {
                    LOG.info( "Sleeping for 1.5 seconds..." );
                    Thread.sleep( 1500 );
                } catch( InterruptedException ie ) {
                    // ignore
                }
            } catch( Throwable t ) {
                LOG.error( "Unanticipated Fault: {}", t.getMessage(), t );
                break;
            }
        }
    }
    
    /**
     * 
     * @return 
     */
    public Optional<ZonedDateTime> getLastReadTime() {
        return Optional.ofNullable( _lastReadTime );
    }

    /**
     * @return
     */
    private boolean isConnected() {
        if( LOG.isTraceEnabled() ) {
            if( _socket == null ) {
                LOG.trace( "{} - Socket is null!", _name );
            } else if( _socket.isClosed() ) {
                LOG.trace( "{} - Socket is closed!", _name );
            } else if( !_socket.isConnected() )  {
                LOG.trace( "{} - Socket is disconnected!", _name );
            } else if( _socket.isInputShutdown() ) {
                LOG.trace( "{} - Socket InputStream is shutdown!", _name );
            }
        }

        return ( _socket != null && !_socket.isClosed() && _socket.isConnected() && !_socket.isInputShutdown() );
    }
    
    /**
     * 
     * @param socket 
     */
    protected void setSocket( Socket socket ) {
        _socket = socket;
    }
    
    /**
     * 
     * @throws Exception 
     */
    @Override
    public void close() throws Exception {
        LOG.fatal( "{} - Close invoked.  AISSocketReader shutting down...", _name );
        
        _keepReading = false;
        
        LOG.fatal( "{} - AISSocketReader successfully closed.", _name );
    }
}
