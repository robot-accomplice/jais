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

import jais.handlers.AISMessageHandler;
import jais.handlers.AISPacketHandler;
import java.net.Socket;
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
    
    public static long ABSOLUTE_WRITE_QUEUE_THRESHOLD = 500000;
    public static long BACK_PRESSURE_THRESHOLD = 10000;

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

    private boolean _closed = false;

    private AISReader _reader;
    private AISWriter _writer;
    private boolean _purge;
    private AISPacketHandler _pktHandler;
    private AISMessageHandler _msgHandler;

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
        this( name, socket, type, readQueue, readBufferSize, threadPool, false, null, null );
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
        this( name, socket, type, readQueue, readBufferSize, threadPool, purgeQueuesOnDisconnect, null, null );
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
     * @param pktHandler
     * @param msgHandler 
     */
    public ActiveConnection( String name, Socket socket, ConnectionType type, ExecutorCompletionService<String> readQueue,
            int readBufferSize, ExecutorService threadPool, boolean purgeQueuesOnDisconnect, AISPacketHandler pktHandler, AISMessageHandler msgHandler ) {
        _name = name + ":" + socket.getRemoteSocketAddress();
        _socket = socket;
        _type = type;
        _readQueue = readQueue;
        _readBufferSize = readBufferSize;
        _threadPool = threadPool;
        _purge = purgeQueuesOnDisconnect;
        _pktHandler = pktHandler;
        _msgHandler = msgHandler;
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
     */
    public void launch() {
        LOG.debug( "{} - New ActiveConnection is of type {}", _name, _type.name() );
        
        _closed = false;

        if( _type.isReadable() ) {
            if( _reader == null ) {
                _reader = new AISReader( _name, _socket, _readBufferSize, _readQueue, _currentRead, _sessionRead );
            }
            LOG.fatal( "{} - Connection is readable, launching reader...", _name );
            _threadPool.execute( _reader );
        }

        if( _type.isWriteable() ) {
            if( _writer == null )
                _writer = new AISWriter( _name, _socket, _currentWritten, _sessionWritten, _purge );
            LOG.fatal( "{} - Connection is writeable, launching writer...", _name );
            _threadPool.execute( _writer );
        }
    }

    /**
     *
     * @param line
     */
    public void writeln( String line ) {
        if( _writer == null ) {
            LOG.error( "{} - AISWriter is null!", _name );
            if( _type.isWriteable() ) {
                if( _writer == null )
                    _writer = new AISWriter( _name, _socket, _currentWritten, _sessionWritten );
                LOG.fatal( "{} - Connection is writeable, launching writer...", _name );
                _threadPool.execute( _writer );
            }
        }

        if( LOG.isTraceEnabled() )
            LOG.trace( "{} - Sending \"{}\" to writer queue...", _name, line );

        if( _type.isWriteable() )
            _writer.writeln( line );
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
            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Attempt to call getWriteQueueSize() on null writer", _name );
            return 0;
        }
        
        return _writer.getQueueSize();
    }

    /**
     *
     */
    void purgeWriteQueue() {
        if( _writer == null ) {
            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Attempt to purge write queue when _writer is null!", _name );
        } else {
            _writer.purgeQueue();
        }
    }

    /**
     *
     * @return
     */
    public boolean isClosed() {
        return _closed;
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.fatal( "\t{} - ActiveConnection shutting down...." );

        if( _reader != null ) {
            try {
                _reader.close();
            } catch( Exception e ) {
                // do nothing;
            }
        }

        if( _writer != null ) {
            try {
                _writer.close();
            } catch( Exception e ) {
                // do nothing
            }
        }
        
        _closed = true;

        LOG.fatal( "\t{} - ActiveConnection successfully closed." );
    }
}
