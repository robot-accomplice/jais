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
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 *
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class ActiveConnection implements AutoCloseable {

    private final static Logger LOG = LogManager.getLogger( ActiveConnection.class );
    
    public final static long DEFAULT_WRITE_QUEUE_ABSOLUTE_THRESHOLD = 500000;
    public final static long DEFAULT_WRITE_BACK_PRESSURE_THRESHOLD = 10000;

    private final String _name;
    private final Socket _socket;
    private final ConnectionType _type;
    private final ExecutorCompletionService<String> _readQueue;
    private final int _readBufferSize;
    private final ExecutorService _threadPool;

    private final LongAdder _currentWritten = new LongAdder();
    private final LongAdder _sessionWritten = new LongAdder();
    private final LongAdder _currentRead = new LongAdder();
    private final LongAdder _sessionRead = new LongAdder();

    private AISReader _reader;
    private AISWriter _writer;
    private boolean _purge;
    private AISStringHandler _handler;
    private long _wqThreshold;
    private long _bpThreshold;
    private boolean _launched = false;

    /**
     *
     * @param name
     * @param socket
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     */
    public ActiveConnection( String name, Socket socket, ConnectionType type, ExecutorCompletionService<String> readQueue,
            int readBufferSize, ExecutorService threadPool ) {
        this( name, socket, type, readQueue, readBufferSize, threadPool, false, null );
    }

    /**
     *
     * @param name
     * @param socket
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param purgeQueuesOnDisconnect
     */
    public ActiveConnection( String name, Socket socket, ConnectionType type, ExecutorCompletionService<String> readQueue,
            int readBufferSize, ExecutorService threadPool, boolean purgeQueuesOnDisconnect ) {
        this( name, socket, type, readQueue, readBufferSize, threadPool, purgeQueuesOnDisconnect, null );
    }

    /**
     * 
     * @param name
     * @param socket
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param purgeQueuesOnDisconnect 
     * @param handler 
     */
    public ActiveConnection( String name, Socket socket, ConnectionType type, ExecutorCompletionService<String> readQueue,
            int readBufferSize, ExecutorService threadPool, boolean purgeQueuesOnDisconnect, AISStringHandler handler ) {
        this( name, socket, type, readQueue, readBufferSize, threadPool, purgeQueuesOnDisconnect, handler, 
                ActiveConnection.DEFAULT_WRITE_QUEUE_ABSOLUTE_THRESHOLD, ActiveConnection.DEFAULT_WRITE_BACK_PRESSURE_THRESHOLD );
    }
    
    /**
     * 
     * @param name
     * @param socket
     * @param type
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param purgeQueuesOnDisconnect 
     * @param handler 
     * @param writeQueueSizeLimit 
     * @param backPressureLimit 
     */
    public ActiveConnection( String name, Socket socket, ConnectionType type, ExecutorCompletionService<String> readQueue,
            int readBufferSize, ExecutorService threadPool, boolean purgeQueuesOnDisconnect, AISStringHandler handler, long writeQueueSizeLimit, 
            long backPressureLimit ) {
        _name = name + ":" + socket.getRemoteSocketAddress();
        _socket = socket;
        _type = type;
        _readQueue = readQueue;
        _readBufferSize = readBufferSize;
        _threadPool = threadPool;
        _purge = purgeQueuesOnDisconnect;
        _handler = handler;
        _wqThreshold = writeQueueSizeLimit;
        _bpThreshold = backPressureLimit;
    }
    
    /**
     *
     * @return
     */
    public String getName() {
        return _name;
    }

    /**
     *
     * @return
     */
    public Socket getSocket() {
        return _socket;
    }

    /**
     *
     * @return
     */
    public ConnectionType getType() {
        return _type;
    }

    /**
     * 
     * @return 
     */
    public boolean isLaunched() {
        return _launched;
    }
    
    /**
     *
     */
    public void launch() {
        _launched = true;
        if( LOG.isDebugEnabled() ) LOG.debug( "{} - New ActiveConnection is of type {}", _name, _type.name() );
        
        if( _type.isReadable() ) {
            if( _reader == null ) {
                _reader = new AISReader( _name, _socket, _readBufferSize, _readQueue, _currentRead, _sessionRead, _handler );
            }
            if( LOG.isInfoEnabled() ) LOG.info( "{} - Connection is readable, launching reader...", _name );
            _threadPool.execute( _reader );
        }

        if( _type.isWriteable() ) {
            if( _writer == null )
                _writer = new AISWriter( _name, _socket, _currentWritten, _sessionWritten, _purge, _wqThreshold, _bpThreshold );
            if( LOG.isInfoEnabled() ) LOG.info( "{} - Connection is writeable, launching writer...", _name );
            _threadPool.execute( _writer );
        }
    }

    /**
     *
     * @param line
     */
    public void writeln( String line ) {
        if( _launched && _writer == null ) {
            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Ignoring attempt to write to null AISWriter.", _name );
        } else if( _launched && _type.isWriteable() ) {
            if( LOG.isTraceEnabled() )
                LOG.trace( "{} - Sending \"{}\" to writer queue...", _name, line );
            _writer.writeln( line );
        } else if( !_launched && LOG.isInfoEnabled() ) {
            LOG.info( "{} - Ignoring attempt to write before ActiveConnection has been launched...", _name );
        }
    }

    /**
     *
     * @return
     */
    public Optional<DateTime> getLastReadTime() {
        return _reader.getLastReadTime();
    }

    /**
     *
     * @return
     */
    public String getLastReadTimeAsString() {
        if( _reader != null ) {
            Optional<DateTime> opt = _reader.getLastReadTime();

            if( opt.isPresent() ) {
                return opt.get().toString( DateTimeFormat.fullDateTime() );
            }
        }

        return "never";
    }

    /**
     *
     * @return
     */
    public long getMinutesSinceLastRead() {
        if( _reader != null ) {
            Optional<DateTime> opt = _reader.getLastReadTime();

            if( opt.isPresent() ) {
                return ( DateTime.now().getMillis() - opt.get().getMillis() ) / ( 1000 * 60 );
            }
        }
        
        return -1;
    }

    /**
     *
     * @return
     */
    public Optional<DateTime> getLastWriteTime() {
        return _writer.getLastWriteTime();
    }

    /**
     *
     * @return
     */
    public String getLastWriteTimeAsString() {
        if( _writer != null ) {
            Optional<DateTime> opt = _writer.getLastWriteTime();

            if( opt.isPresent() ) {
                return opt.get().toString( DateTimeFormat.fullDateTime() );
            }
        }

        return "never";
    }

    /**
     *
     * @return
     */
    public long getMinutesSinceLastWrite() {
        if( _writer != null ) {
            Optional<DateTime> opt = _writer.getLastWriteTime();

            if( opt.isPresent() ) {
                return ( DateTime.now().getMillis() - opt.get().getMillis() ) / ( 1000 * 60 );
            }
        }
        
        return -1;
    }
    
    /**
     * Returns the number of "lines" read during this instantiation of ActiveConnection
     * 
     * @return 
     */
    public long getSessionRead() {
        return _sessionRead.sum();
    }
    
    /**
     * Returns the number of "lines" read since the last call to getCurrentRead within the current session and then resets the counter to zero
     * 
     * @return 
     */
    public long getCurrentRead() {
        return _currentRead.sumThenReset();
    }

    /**
     * Returns the number of "lines" written during this instantiation of ActiveConnection
     * 
     * @return 
     */
    public long getSessionWritten() {
        return _sessionWritten.sum();
    }
    
    /**
     * Returns the number of "lines" written since the last call to getCurrentRead within the current session and then resets the counter to zero
     * 
     * @return 
     */
    public long getCurrentWritten() {
        return _currentWritten.sumThenReset();
    }

    /**
     *
     * @return
     */
    public long getWriteQueueSize() {
        if( _writer == null ) {
            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Ignoring attempt to call getWriteQueueSize() on null writer", _name );
            return 0;
        }
        
        return _writer.getQueueSize();
    }

    /**
     *
     */
    void purgeWriteQueue() {
        if( _writer == null ) {
            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Ignoring attempt to purge write queue when _writer is null!", _name );
        } else {
            _writer.purgeQueue();
        }
    }

    /**
     *
     * @return
     */
    public boolean isClosed() {
        return ( _socket == null || _socket.isClosed() || !_socket.isConnected() );
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.fatal( "{} - ActiveConnection shutting down...", _name );

        if( _reader != null ) {
            try {
                LOG.fatal( "{} - Closing the reader...", _name );
                _reader.close();
            } catch( Exception e ) {
                // do nothing;
            }
        }

        if( _writer != null ) {
            try {
                LOG.fatal( "{} - Closing the writer...", _name );
                _writer.close();
            } catch( Exception e ) {
                // do nothing
            }
        }
        
        LOG.fatal( "{} - ActiveConnection successfully closed.", _name );
    }
    
    /**
     * 
     * @param obj
     * @return 
     */
    @Override
    public boolean equals( Object obj ) {
        if( obj instanceof ActiveConnection ) {
            ActiveConnection otherConnection = ( ActiveConnection )obj;
            return ( otherConnection.getSocket().equals( _socket ) );
        } else {
            return false;
        }
    }

    /**
     * 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode( this._socket );
        return hash;
    }
}
