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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class SocketServer extends SocketConnectionBase {
    
    private final static Logger LOG = LogManager.getLogger( SocketServer.class );
    public final static long DEFAULT_CLIENT_IDLE_THRESHOLD_MS = 60000;
    
    private final int _readBufferSize;
    private final ConcurrentLinkedQueue<ActiveConnection> _connections = new ConcurrentLinkedQueue<>();
    private final Timer _houseKeeper = new Timer();
    private final long _clientIdleThresholdMs;
    private final long _backpressureThreshold;
    private final long _absoluteThreshold;
    private final LongAdder _readCounter;
    private final LongAdder _writeCounter;

    private ServerSocket _ssocket;
    private InetSocketAddress _address;
    private boolean _keepOpen = true;
    
    /**
     * 
     * @param name
     * @param port
     * @param type 
     * @param readQueue
     * @param readBufferSize
     */
    public SocketServer( String name, int port, ConnectionType type, ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize ) {
        this( name, port, type, readQueue, readBufferSize, ActiveConnection.DEFAULT_WRITE_BACK_PRESSURE_THRESHOLD, 
                ActiveConnection.DEFAULT_WRITE_QUEUE_ABSOLUTE_THRESHOLD );
    }
    
    /**
     * 
     * @param name
     * @param port
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param backpressureThreshold
     * @param absoluteThreshold 
     */
    public SocketServer( String name, int port, ConnectionType type, ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, 
            long backpressureThreshold, long absoluteThreshold ) {
        this( name, port, type, readQueue, readBufferSize, DEFAULT_CLIENT_IDLE_THRESHOLD_MS, backpressureThreshold, absoluteThreshold );
    }

    /**
     * 
     * @param name
     * @param port
     * @param type 
     * @param readQueue
     * @param readBufferSize
     * @param clientIdleThresholdMs
     * @param backpressureThreshold
     * @param absoluteThreshold
     */
    public SocketServer( String name, int port, ConnectionType type, ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, 
            long clientIdleThresholdMs, long backpressureThreshold, long absoluteThreshold ) {
        this( name, port, type, readQueue, readBufferSize, clientIdleThresholdMs, backpressureThreshold, absoluteThreshold, null, null );
    }
    
    /**
     * 
     * @param name
     * @param port
     * @param type 
     * @param readQueue
     * @param readBufferSize
     * @param clientIdleThresholdMs
     * @param backpressureThreshold
     * @param absoluteThreshold
     * @param readCounter
     * @param writeCounter
     */
    public SocketServer( String name, int port, ConnectionType type, ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, 
            long clientIdleThresholdMs, long backpressureThreshold, long absoluteThreshold, LongAdder readCounter, LongAdder writeCounter ) {
        _name = name;
        _port = port;
        _type = type;
        _readQueue = readQueue;
        _readBufferSize = readBufferSize;
        _clientIdleThresholdMs = clientIdleThresholdMs;
        _backpressureThreshold = backpressureThreshold;
        _absoluteThreshold = absoluteThreshold;
        _readCounter = readCounter;
        _writeCounter = writeCounter;
    }
    
    /**
     * 
     * @throws IOException 
     */
    @Override
    public void connect() throws IOException {
        connect( false );
    }
    
    /**
     * 
     * @param reuseAddress
     * @throws java.io.IOException
     */
    @Override
    public void connect( boolean reuseAddress ) throws IOException {
        _address = new InetSocketAddress( _port );
        
        try( ServerSocket ss = new ServerSocket() ) {
            _ssocket = ss;
            
            // prebinding options
            ss.setReuseAddress( reuseAddress );
            
            ss.bind( _address );

            _houseKeeper.schedule( new HouseKeeper(), 10000, 10000 );

            LOG.fatal( "{} - Now listening for new connections on port {}", _name, _port );
            while( _keepOpen ) {
                if( ss.isClosed() ) {
                    LOG.error( "{} - The socket is closed!", _name );
                }
                Socket s = ss.accept();
                _totalConnectAttempts.increment();
                LOG.fatal( "{} - New connection accepted! {}", _name, s.getRemoteSocketAddress() );
                LOG.fatal( "{} - Launching new ActiveConnection instance...", _name );
                ActiveConnection connection = new ActiveConnection( _name, s, _type, _readQueue, _readBufferSize, true, null,  _absoluteThreshold, 
                        _backpressureThreshold, _readCounter, _writeCounter );
                connection.launch();
                LOG.fatal( "{} - Adding new connection to list of connections...", _name );
                _connections.add( connection );
            }
        }
    }

    /**
     * 
     */   
    @Override
    public void close() {
        LOG.fatal( "{} - SocketServer.close() invoked, shutting down...", _name );
        
        _keepOpen = false;
        
        try {
            LOG.fatal( "{} - Closing server socket connection...", _name );
            _ssocket.close();
        } catch( IOException ioe ) {
            // ignore
        }
        
        LOG.fatal( "{} - SocketServer shutdown invoked.  Closing client connections...", _name );
        _connections.forEach( ( connection ) -> {
            connection.close();
            try {
                connection.getSocket().close();
            } catch( IOException ioe ) {
                if( LOG.isWarnEnabled() ) LOG.warn( "{} - Encountered IOException while closing client socket: {}", _name, ioe.getMessage() );
                if( LOG.isTraceEnabled() ) LOG.trace( "StackTrace", ioe );
            }
        } );
        
        _houseKeeper.cancel();

        LOG.fatal( "{} - SocketServer shutdown complete", _name );
    }

    /**
     * 
     * @param line 
     */
    @Override
    public void writeln( String line ) {
        _connections.forEach( ( connection ) -> {
            if( connection.isClosed() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "{} - Connection to {} is closed, aborting write operation.", _name, connection.getName() );
            } else if( connection.getSocket().isClosed() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "{} - Connection {} socket is closed, aborting write operation.", _name, connection.getName() );
            } else if( line != null && !line.isEmpty() ) {
                if( LOG.isInfoEnabled() ) LOG.info( "{} - Writing {} bytes to connection \"{}\"", _name, line.length(), connection.getName() );
                if( LOG.isDebugEnabled() ) LOG.debug( "{} - Writing {} bytes to connection \"{}\"", _name, line.length(), connection.getName() );
                connection.writeln( line );
            }
        } );
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Optional<ZonedDateTime> getLastReadTime() {
        _connections.forEach( ( connection ) -> {
            if( _lastReadTime == null && connection.getLastReadTime().isPresent() ) {
                _lastReadTime = connection.getLastReadTime().get();
            } else if( connection.getLastReadTime().isPresent() ) {
                if( connection.getLastReadTime().get().isAfter( _lastReadTime ) ) {
                    _lastReadTime = connection.getLastReadTime().get();
                }
            }
        } );
        
        return Optional.ofNullable( _lastReadTime );
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastRead() {
        if( getLastReadTime().isPresent() ) {
            _minutesSinceLastRead = ( System.currentTimeMillis() - _lastReadTime.toInstant().toEpochMilli() ) / ( 1000 * 60 );
        }
        
        return _minutesSinceLastRead;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getCurrentLinesRead() {
        if( _connections.isEmpty() ) return 0;
        
        long currentTotal = 0;
        
        currentTotal = _connections.stream().map( ( connection ) -> connection.getCurrentRead() )
                .reduce( currentTotal, ( accumulator, _item ) -> accumulator + _item );
        
        return currentTotal;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public Optional<ZonedDateTime> getLastWriteTime() {
        _connections.forEach( ( connection ) -> {
            if( _lastWriteTime == null && connection.getLastWriteTime().isPresent() ) {
                _lastWriteTime = connection.getLastWriteTime().get();
            } else if( connection.getLastWriteTime().isPresent() ) {
                if( connection.getLastWriteTime().get().isAfter( _lastWriteTime ) ) {
                    _lastWriteTime = connection.getLastWriteTime().get();
                }
            }
        } );
        
        return Optional.ofNullable( _lastWriteTime );
    }

    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastWrite() {
        if( getLastWriteTime().isPresent() ) {
            _minutesSinceLastWrite = ( System.currentTimeMillis() - _lastWriteTime.toInstant().toEpochMilli() ) / ( 1000 * 60 );
        }
        
        return _minutesSinceLastWrite;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getCurrentLinesWritten() {
        if( _connections.isEmpty() ) return 0;
        
        long currentTotal = 0;
        
        currentTotal = _connections.stream().map( ( connection ) -> connection.getCurrentWritten() )
                .reduce( currentTotal, ( accumulator, _item ) -> accumulator + _item );
        
        return currentTotal;
    }
    
    /**
     * 
     */
    @Override
    public void init() {
        // not relevant to this connection type
        if( LOG.isInfoEnabled() ) LOG.info( "{} - This connection type does not require initialization. Ignoring.", _name );
    }

    /**
     * 
     * @param params 
     */
    @Override
    public void setParams( Map<String, String> params ) {
        // not relevant to this connection type
        if( LOG.isInfoEnabled() ) LOG.info( "{} - This connection type takes no parameters, ignoring specified parameters.", _name );
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isConnected() {
        return !( _ssocket == null || _ssocket.isClosed() );
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public int getActiveConnections() {
        return _connections.size();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getWriteQueueSize() {
        long queueSize = 0;
        
        queueSize = _connections.stream().map( ( connection ) -> 
                connection.getWriteQueueSize() ).reduce( queueSize, ( accumulator, _item ) -> accumulator + _item );
        
        return queueSize;
    }
    
    /**
     * 
     */
    class HouseKeeper extends TimerTask {

        /**
         * 
         */
        @Override
        public void run() {
            // housekeeping
            _connections.forEach( ( c ) -> {
                try { 
                    if( c.isLaunched() ) {
                        if( c.isClosed() ) {
                            try {
                                if( LOG.isWarnEnabled() )
                                    LOG.warn( "{} - Client {} is no longer connected.  Removing from list of active connections", _name, 
                                            c.getSocket().getInetAddress() );
                                c.close();
                                _connections.remove( c );
                            } catch( Exception e ) {
                                if( LOG.isWarnEnabled() ) LOG.warn( "{} - Encountered an exception while trying to close client socket: {}", 
                                        _name, e.getMessage() );
                            }
                        } else {
                            long idleTimeMs = c.getIdleMilliseconds();
                            
                            if( idleTimeMs > _clientIdleThresholdMs ) {
                                if( LOG.isInfoEnabled() ) LOG.info( "{} - Client {} has been idle for more than {} ms.  Disconnecting.", _name, 
                                        c.getSocket().getInetAddress(), idleTimeMs );
                                c.close();
                                _connections.remove( c );
                            } else if( LOG.isDebugEnabled() ) {
                                LOG.debug( "{} - Connection to {} is launched, active, and does not exceed the idle threshold.  Skipping.", 
                                        _name, c.getSocket().getInetAddress() );
                            }
                        }
                    }
                } catch( Throwable t ) {
                    LOG.warn( "{} - Encountered an unforeseen error while cleaning out closed connections: {}", _name, t.getMessage() );
                }
            } );
        }
    }
}
