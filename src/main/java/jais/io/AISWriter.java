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
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public class AISWriter implements Runnable, AutoCloseable {
    
    private final static Logger LOG = LogManager.getLogger( AISWriter.class );
    
    private final static String LINE_TERMINATOR = "\n";
    
    private final String _name;
    private final Socket _socket;
    
    private boolean _keepWriting = true;
    
    private final ConcurrentLinkedQueue<String> _queue = new ConcurrentLinkedQueue<>();
    private final LongAdder _write;
    private final LongAdder _total;
    private final boolean _purge;
    private final long _bpThreshold;
    private final long _wqThreshold;
    public DateTime _lastWriteTime;
    
    
    /**
     * 
     * @param name
     * @param socket
     * @param writeCounter 
     * @param totalCounter 
     * @param wqThreshold 
     * @param bpThreshold 
     */
    public AISWriter( String name, Socket socket, LongAdder writeCounter, LongAdder totalCounter, long wqThreshold, long bpThreshold ) {
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
    public AISWriter( String name, Socket socket, LongAdder writeCounter, LongAdder totalCounter, boolean purgeQueueOnDisconnect, long wqThreshold,
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
        LOG.fatal( "{} - AISWriter thread started...", _name );
        try( OutputStream out = _socket.getOutputStream() ) {
            LOG.fatal( "{} - Creating BufferedOutputStream...", _name );
            BufferedOutputStream bout = new BufferedOutputStream( out );
            LOG.fatal( "{} - Creating BuffereredWriter...", _name );
            BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( bout ) );
            
            while( _keepWriting && isConnected() ) {
                if( _queue.isEmpty() ) {
                    try {
                        Thread.sleep( 1000 );
                    } catch( InterruptedException ie ) {
                        // do nothing
                    }
                } else {
                    long startingSize = _queue.size();
                    if( LOG.isInfoEnabled() ) LOG.info( "{} - processing {} queue entries...", _name, _queue.size() );
                    
                    for( String line : _queue ) {
                        if( !isConnected() ) 
                            throw new IOException( _name + " - Connection to socket was closed" );
                        
                        _queue.remove( line );
                        
                        if( line != null && !line.isEmpty() ) {
                            if( LOG.isDebugEnabled() ) LOG.debug( "{} - Writing String \"{}\" to target", _name, line );
                            writer.write( line + LINE_TERMINATOR );
                            _lastWriteTime = DateTime.now();
                            _write.increment();
                            _total.increment();
                        }
                        
                        if( _queue.size() > _wqThreshold ) {
                            LOG.fatal( "{} - Maximum queue size ({}) exceeded.  Purging Queue.", _name, _wqThreshold );
                            purgeQueue();
                        } else if( _queue.size() - startingSize > _bpThreshold ) {
                            LOG.fatal( "{} - Backpressure threshold ({} messages) exceeded.  Purging Queue.", _name, _bpThreshold );
                            purgeQueue();
                        }
                    }
                }
            }
            
            if( !isConnected() ) {
                LOG.error( "{} - Socket connection was closed.", _name );
            }
        } catch( IOException ioe ) {
            LOG.error( "{} - IOException encountered: {}", _name, ioe.getMessage() );
            if( LOG.isTraceEnabled() ) LOG.trace( ioe );
        } catch( Throwable t ) {
            LOG.error( "{} - Unanticipated fault occurred: {}", _name, t.getMessage(), t );
        } finally {
            try {
                close();
            } catch( Exception e ) {
            }
        }
    }

    /**
     * 
     * @param line 
     */
    public void writeln( String line ) {
        if( LOG.isTraceEnabled() ) LOG.trace( "{} - {} bytes received, adding to queue...", _name, line.length() );
        try {
            if( line != null && !line.isEmpty() ) _queue.add( line );
        } catch( Exception e ) {
            LOG.error( "{} - Exception: {}", _name, e.getMessage() );
            if( LOG.isTraceEnabled() ) LOG.trace( e );
        }
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
    public Optional<DateTime> getLastWriteTime() {
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
     * @throws Exception 
     */
    @Override
    public void close() throws Exception {
        LOG.fatal( "\t{} - AISWriter shutting down...", _name );
        _keepWriting = false;
        if( _purge ) _queue.clear();
        
        try {
            if( _socket != null ) _socket.close();
        } catch( IOException ioe ) {
            // ignore
        }

        LOG.fatal( "\t{} - AISWriter successfully closed.", _name );
    }    
}
