/*
 * Copyright 2018 jmachen.
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
package jais.readers.threads;

import jais.io.AISPacketBuffer;
import java.io.Closeable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jmachen
 */
public class PacketBuilderWorkerThreadPool implements Closeable {

    private final static Logger LOG = LogManager.getLogger( PacketBuilderWorkerThreadPool.class );
    private final static int DEFAULT_SIZE = 32;

    private final String _source;
    private final AISPacketBuffer _buffer;
    private final int _size;
    private final PacketBuilderWorkerThread[] _workers;
    private boolean _shouldRun = true;

    /**
     *
     * @param source
     * @param buffer
     */
    public PacketBuilderWorkerThreadPool( String source, AISPacketBuffer buffer ) {
        this( source, buffer, DEFAULT_SIZE );
    }

    /**
     *
     * @param source
     * @param buffer
     * @param size
     */
    public PacketBuilderWorkerThreadPool( String source, AISPacketBuffer buffer, int size ) {
        _source = source;
        _buffer = buffer;
        _size = size;
        _workers = new PacketBuilderWorkerThread[_size];
    }

    /**
     *
     */
    public void init() {
        for( int i = 0; i < _size; i++ ) {
            _workers[i] = new PacketBuilderWorkerThread( _buffer, _source );
        }
    }
    
    /**
     * 
     */
    @Override
    public void close() {
        _shouldRun = false;
    }

    /**
     *
     * @param s
     * @return 
     */
    public PacketBuilderWorkerThread getWorker( String s ) {
        while( _shouldRun ) {
            for( PacketBuilderWorkerThread w : _workers ) {
                if( w.isIdle() ) return w.init( s );
            }
            
            try {
                // exhausted the pool and all workers are busy, wait for one second and check again
                Thread.sleep( 1000 );
            } catch( InterruptedException ex ) {
                // ignore
            }
        }
        
        return null;
    }
}
