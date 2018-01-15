/*
 * This software is the sole property of ShipTracks, LLC and is not 
 * licensed for redistribution, modification, or resale by any other party.
 */
package jais.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class AISSocketWriter implements Runnable, AutoCloseable {
    
    private final static Logger LOG = LogManager.getLogger(AISSocketWriter.class );
    
    private final static char LINE_TERMINATOR = '\n';
    
    private final String _name;
    private Socket _socket;
    
    private boolean _keepWriting = true;
    
    private final ConcurrentLinkedQueue<String> _queue = new ConcurrentLinkedQueue<>();
    private final LongAdder _write;
    private final LongAdder _total;
    private final boolean _purge;
    private final long _bpThreshold;
    private final long _wqThreshold;
    public ZonedDateTime _lastWriteTime;
    
    
    /**
     * 
     * @param name
     * @param socket
     * @param writeCounter 
     * @param totalCounter 
     * @param wqThreshold 
     * @param bpThreshold 
     */
    public AISSocketWriter( String name, Socket socket, LongAdder writeCounter, LongAdder totalCounter, long wqThreshold, long bpThreshold ) {
        this( name, socket, writeCounter, totalCounter, true, wqThreshold, bpThreshold );
    }
    
    /**
     * 
     * @param name
     * @param socket
     * @param writeCounter
     * @param totalCounter
     * @param purgeQueueOnDisconnect
     * @param wqThreshold
     * @param bpThreshold
     */
    public AISSocketWriter( String name, Socket socket, LongAdder writeCounter, LongAdder totalCounter, boolean purgeQueueOnDisconnect, long wqThreshold,
            long bpThreshold ) {
        _name = name;
        _socket = socket;
        _write = writeCounter;
        _total = totalCounter;
        _purge = purgeQueueOnDisconnect;
        _wqThreshold = wqThreshold;
        _bpThreshold = bpThreshold;
    }
    
    /**
     * 
     */
    @Override
    public void run() {
        if( LOG.isInfoEnabled() ) LOG.info( "{} - AISWriter thread started...", _name );
            
        while( _keepWriting ) {
            try( OutputStream out = _socket.getOutputStream(); 
                    BufferedOutputStream bout = new BufferedOutputStream( out ); 
                    BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( bout ) ) ) {
                while( isConnected() ) {
                    if( _queue.isEmpty() ) {
                        try {
                            Thread.sleep( 1000 );
                        } catch( InterruptedException ie ) {
                            // do nothing
                        }
                    } else {
                        long startingSize = _queue.size();
                        if( LOG.isInfoEnabled() ) LOG.info( "{} - processing {} queue entries...", _name, _queue.size() );
                        
                        if( _queue.size() > _wqThreshold ) {
                            if( LOG.isWarnEnabled() ) LOG.warn( "{} - Maximum queue size ({}) exceeded.  Purging Queue.", _name, _wqThreshold );
                            purgeQueue();
                        } else {
                            for( String line : _queue ) {
                                if( !isConnected() ) throw new IOException( _name + " - Connection to socket was closed" );

                                _queue.remove( line );

                                if( line != null && !line.isEmpty() ) {
                                    if( LOG.isDebugEnabled() ) LOG.debug( "{} - Writing String \"{}\" to target", _name, line );
                                    writer.write( line + LINE_TERMINATOR );
                                    _lastWriteTime = java.time.ZonedDateTime.now( ZoneOffset.UTC.normalized() );
                                    _write.increment();
                                    _total.increment();
                                }

                                if( _queue.size() - startingSize > _bpThreshold ) {
                                    if( LOG.isWarnEnabled() ) LOG.warn( "{} - Back pressure threshold ({} messages) exceeded.  Purging Queue.", 
                                            _name, _bpThreshold );
                                    purgeQueue();
                                } else {
                                    startingSize = _queue.size();
                                }
                            }
                        }
                    }
                }
            } catch( IOException ioe ) {
                LOG.warn( "{} - IOException encountered: {}", _name, ioe.getMessage() );
                if( LOG.isTraceEnabled() ) LOG.trace( ioe );
                try {
                    LOG.info( "Sleeping for 1.5 seconds..." );
                    Thread.sleep( 1500 );
                } catch( InterruptedException ie ) {
                    // ignore
                }
            }
        }
    }

    /**
     * 
     * @param line 
     */
    public void writeln( String line ) {
        if( LOG.isTraceEnabled() ) LOG.trace( "{} - {} bytes received, adding to queue...", _name, line.length() );
        if( line != null && !line.isEmpty() ) _queue.add( line );
    }
    
    /**
     * 
     * @return 
     */
    public int purgeQueue() {
        int size = _queue.size();
        if( LOG.isDebugEnabled() ) LOG.debug( "{} - Purging {} elements from write queue...", _name, size );
        _queue.clear();
        
        return size;
    }
   
    /**
     * 
     * @return 
     */
    public long getQueueSize() {
        return _queue.size();
    }
    
    /**
     * 
     * @return 
     */
    public Optional<ZonedDateTime> getLastWriteTime() {
        return Optional.ofNullable( _lastWriteTime );
    }
    
    /**
     * @return
     */
    private boolean isConnected() {
        return ( _socket != null && !_socket.isClosed() && _socket.isConnected() && !_socket.isOutputShutdown() );
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
        LOG.fatal( "{} - AISSocketWriter shutting down...", _name );
        
        _keepWriting = false;
        
        LOG.fatal( "{} - Purging write queue...", _name );
        if( _purge ) _queue.clear();
        
        LOG.fatal( "{} - AISSocketWriter successfully closed.", _name );
    }    
}
