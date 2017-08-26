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
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class SocketClient extends SocketConnectionBase {
    
    private final static Logger LOG = LogManager.getLogger( SocketClient.class );
    private final static int RECONNECT_DELAY = 15000;

    private final InetSocketAddress _address;
    private Socket _socket;
    private final int _readBufferSize;
    
    private boolean _keepOpen = true;
    private ActiveConnection _connection;
    
    /**
     * 
     * @param name
     * @param host
     * @param port
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     */
    public SocketClient( String name, String host, int port, ConnectionType type, 
            ExecutorCompletionService<String> readQueue, int readBufferSize, ExecutorService threadPool ) {
        _name = name;
        _host = host;
        _port = port;
        _type = type;
        _readQueue = readQueue;
        _readBufferSize = readBufferSize;
        _address = new InetSocketAddress( _host, _port );
        _socket = new Socket();
        _threadPool = threadPool;
        _connection = new ActiveConnection( _name, _socket, _type, _readQueue, _readBufferSize, _threadPool );
    }
    
    /**
     * 
     * @throws IOException 
     */
    @Override
    public void connect() throws IOException {
        LOG.fatal( "{} - Establishing connection to {}", _name, _address );

        while( _keepOpen ) {
            try {
                if( _keepOpen && _connection.isClosed() ) {
                    _totalConnectAttempts.increment();
                    LOG.fatal( "{} - Socket is closed, (re)connecting to {}...", _name, _address );

                    _socket = new Socket();
                    _socket.connect( _address );
                    
                    if( _socket.isConnected() ) {
                        _connection = new ActiveConnection( _name, _socket, _type, _readQueue, _readBufferSize, _threadPool );
                        _connection.launch();
                    } else {
                        LOG.fatal( "{} - Failed to connect to {}:{}. Sleeping for {} seconds before trying again...", _name, 
                                ( RECONNECT_DELAY / 1000 ) );
                    }
                }
            } catch( IOException ioe ) {
                LOG.error( "{} [{}:{}] - Encountered an IOException: {}", _name, _host, _port, ioe.getMessage() );
                if( LOG.isTraceEnabled() ) LOG.trace( ioe );
            } finally {
                try {
                    Thread.sleep( RECONNECT_DELAY );
                } catch( InterruptedException ie ) {
                    // do nothing
                }
            }
        }
    }
    
    /**
     * 
     * @param reuseAddress
     * @throws IOException 
     */
    @Override
    public void connect( boolean reuseAddress ) throws IOException {
        LOG.fatal( "Option \"reuseAddress\" is not relevant to this connection type and will be ignored." );
        connect();
    }

    /**
     * 
     * @param line 
     */
    @Override
    public void writeln( String line ) {
        if( _connection == null ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "{} - writeln(): Connection Object is null, connection may have failed", _name );
        } else if( _socket == null ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "{} - writeln(): Socket Object is null, connection may have failed", _name );
            _connection.purgeWriteQueue();
        } else if( _socket.isClosed() ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "{} - writeln(): Socket is closed!", _name );
            _connection.purgeWriteQueue();
        } else {
            _connection.writeln( line );
        }
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getLastReadTime() {
        String lastRead = _connection.getLastReadTimeAsString();
        
        if( !lastRead.equals( "never" ) ) _lastRead = lastRead;
        
        return _lastRead;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastRead() {
        long mins = _connection.getMinutesSinceLastRead();
        
        if( mins > -1 ) _minutesSinceLastRead = mins;
        
        return _minutesSinceLastRead;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getCurrentLinesRead() {
        if( _connection == null ) return 0;
        return _connection.getCurrentRead();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getTotalLinesRead() {
        return _connection.getSessionRead();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public String getLastWriteTime() {
        String lastWrite = _connection.getLastWriteTimeAsString();
        
        if( !lastWrite.equals( "never" ) ) _lastWrite = lastWrite;
                
        return _lastWrite;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getMinutesSinceLastWrite() {
        long mins = _connection.getMinutesSinceLastWrite();
        
        if( mins > -1 ) _minutesSinceLastWrite = mins;
        
        return _minutesSinceLastWrite;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getWriteQueueSize() {
        if( _connection == null ) return 0;
        return _connection.getWriteQueueSize();
    }

    /**
     * 
     * @return 
     */
    @Override
    public long getCurrentLinesWritten() {
        if( _connection == null ) return 0;
        return _connection.getCurrentWritten();
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getTotalLinesWritten() {
        return _connection.getSessionWritten();
    }
    
    /**
     * 
     */
    @Override
    public void close() {
        _keepOpen = false;
        LOG.fatal( "{} - SocketClient shutdown invoked.  Closing connection...", _name );
        if( !_connection.isClosed() ) _connection.close();

        try {
            LOG.fatal( "{} - Closing socket connection...", _name );
            _socket.close();
        } catch( IOException ioe ) {
            // ignore
        }

        LOG.fatal( "{} - SocketClient closed.", _name );
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
        return ( _socket != null && !_socket.isClosed() && _socket.isConnected() );
    }
}
