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


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class SocketServer extends SocketConnectionBase {
    
    private final static Logger LOG = LogManager.getLogger( SocketServer.class );
    
    private final int _readBufferSize;
    
    private InetSocketAddress _address;
    private boolean _keepOpen = true;
    private final List<ActiveConnection> _connections = new ArrayList();
    private final Timer _houseKeeper = new Timer();
    private ServerSocket _socket;
    private long _totalRead;
    private long _totalWritten;
    
    /**
     * 
     * @param name
     * @param port
     * @param type 
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     */
    public SocketServer( String name, int port, ConnectionType type, 
            ExecutorCompletionService<String> readQueue, int readBufferSize, ExecutorService threadPool ) {
        _name = name;
        _port = port;
        _type = type;
        _readQueue = readQueue;
        _readBufferSize = readBufferSize;
        _threadPool = threadPool;
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
            _socket = ss;
            
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
                LOG.fatal( "{} - New connection accepted! {}", _name, s.getRemoteSocketAddress() );
                LOG.fatal( "{} - Launching new ActiveConnection instance...", _name );
                ActiveConnection connection = new ActiveConnection( _name, s, _type, _readQueue, _readBufferSize, _threadPool, true );
                connection.launch();
                LOG.fatal( "{} - Adding new connection to list of connections...", _name );
                _connections.add( connection );
                _totalConnectAttempts.increment();
            }
        }
    }

    /**
     * 
     */   
    @Override
    public void close() {
        LOG.fatal( "{} - SocketServer shutdown invoked.  Closing client connections...", _name );
        _connections.forEach( ( connection ) -> {
            connection.close();
        } );
        
        _keepOpen = false;
        _houseKeeper.cancel();

        try {
            LOG.fatal( "{} - Closing socket connection...", _name );
            _socket.close();
        } catch( IOException ioe ) {
            // ignore
        }
        
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
    public String getLastReadTime() {
        DateTime latestTime = null;

        for( ActiveConnection connection: _connections ) {

            if( latestTime == null && connection.getLastReadTime().isPresent() ) {
                latestTime = connection.getLastReadTime().get();
            } else if( connection.getLastReadTime().isPresent() ) {
                if( connection.getLastReadTime().get().isAfter( latestTime ) ) {
                    latestTime = connection.getLastReadTime().get();
                }
            }
        }
        
        if( latestTime != null ) {
            return latestTime.toString( DateTimeFormat.fullDateTime() );
        }
        
        return "never";
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastRead() {
        DateTime latestTime = null;
        
        for( ActiveConnection connection: _connections ) {

            if( latestTime == null && connection.getLastReadTime().isPresent() ) {
                latestTime = connection.getLastReadTime().get();
            } else if( connection.getLastReadTime().isPresent() ) {
                if( connection.getLastReadTime().get().isAfter( latestTime ) ) {
                    latestTime = connection.getLastReadTime().get();
                }
            }
        }
        
        if( latestTime != null ) {
            return ( DateTime.now().getMillis() - latestTime.getMillis() ) / ( 1000 * 60 );
        }
        
        return -1;
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
    public long getTotalLinesRead() {
        for( ActiveConnection connection : _connections ) {
            // do something
        }
        
        return _totalRead;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getLastWriteTime() {
        DateTime latestTime = null;

        for( ActiveConnection connection: _connections ) {

            if( latestTime == null && connection.getLastWriteTime().isPresent() ) {
                latestTime = connection.getLastWriteTime().get();
            } else if( connection.getLastWriteTime().isPresent() ) {
                if( connection.getLastWriteTime().get().isAfter( latestTime ) ) {
                    latestTime = connection.getLastWriteTime().get();
                }
            }
        }
        
        if( latestTime != null ) {
            return latestTime.toString( DateTimeFormat.fullDateTime() );
        }
        
        return "never";
    }

    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastWrite() {
        DateTime latestTime = null;
        
        for( ActiveConnection connection: _connections ) {

            if( latestTime == null && connection.getLastWriteTime().isPresent() ) {
                latestTime = connection.getLastWriteTime().get();
            } else if( connection.getLastWriteTime().isPresent() ) {
                if( connection.getLastWriteTime().get().isAfter( latestTime ) ) {
                    latestTime = connection.getLastWriteTime().get();
                }
            }
        }
        
        if( latestTime != null ) {
            return ( DateTime.now().getMillis() - latestTime.getMillis() ) / ( 1000 * 60 );
        }
        
        return -1;
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
     * @return 
     */
    @Override
    public long getTotalLinesWritten() {
        for( ActiveConnection connection : _connections ) {
            // do something
        }
        
        return _totalWritten;
    }
    
    /**
     * 
     */
    @Override
    public void init() {
        // not relevant to this connection type
        LOG.warn( "{} - This connection type does not require initialization. Ignoring.", _name );
    }

    /**
     * 
     * @param params 
     */
    @Override
    public void setParams( Map<String, String> params ) {
        // not relevant to this connection type
        LOG.warn( "{} - This connection type takes no parameters, ignoring specified parameters.", _name );
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isConnected() {
        return !( _connections == null || _connections.isEmpty() );
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getWriteQueueSize() {
        long queueSize = 0;
        
        for( ActiveConnection connection : _connections ) {
            queueSize += connection.getWriteQueueSize();
        }
        
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
            _connections.stream().filter( ( c ) -> ( c == null || c.isClosed() ) ).forEachOrdered( ( c ) -> {
                LOG.fatal( "{} - Client {} is no longer connected.  Removing from list of active connections", _name, c.getName() );
                c.close();
                _connections.remove( c );
            } );
        }
    }
}
