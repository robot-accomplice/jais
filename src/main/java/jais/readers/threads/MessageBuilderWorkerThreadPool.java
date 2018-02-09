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

import jais.AISPacket;
import java.io.Closeable;

/**
 *
 * @author jmachen
 */
public class MessageBuilderWorkerThreadPool implements Closeable {
    
    public final static int DEFAULT_SIZE = 32;
    private final String _source;
    private final int _size;
    private final MessageBuilderWorkerThread[] _workers;
    private boolean _shouldRun = true;
    
    /**
     * 
     * @param source
     * @param size 
     */
    public MessageBuilderWorkerThreadPool( String source, int size ) {
        _source = source;
        _size = size;
        _workers = new MessageBuilderWorkerThread[_size];
    }
    
    /**
     * 
     * @param source
     */
    public MessageBuilderWorkerThreadPool( String source ) {
        this( source, DEFAULT_SIZE );
    }
    
    /**
     * 
     */
    public void init() {
        for( int i = 0; i < _size; i++ ) {
            _workers[i] = new MessageBuilderWorkerThread( _source );
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
     * @param packets
     * @return 
     */
    public MessageBuilderWorkerThread getWorker( AISPacket [] packets ) {
        System.out.println( "Fetching MessageBuilderWorker thread..." );
        
        while( _shouldRun ) {
            for( MessageBuilderWorkerThread w : _workers ) {
                if( w.isIdle() ) {
                    System.out.println( "Found an idle worker thread, returning..." );
                    return w.init( packets );
                }
            }

            try {
                System.out.println( "No idle worker threads available.  Waiting for 1 seconds..." );
                // exhausted the pool and all workers are busy, wait for one second and check again
                Thread.sleep( 1000 );
            } catch( InterruptedException ex ) {
                // ignore
            }
        }
        
        return null;
    }
}
