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

import java.util.Optional;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

/**
 * 
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class SocketConnectionFactory {
    
    /**
     * 
     * @param name
     * @param host
     * @param port
     * @param runas
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @return 
     */
    public static SocketConnection buildConnection( String name, String host, int port, ConnectionType runas,
            ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, ExecutorService threadPool ) {
        if( runas.isServer() ) {
            return new SocketServer( name, port, runas, readQueue, readBufferSize, threadPool );
        } else {
            return new SocketClient( name, host, port, runas, readQueue, readBufferSize, threadPool );
        }
    }
    
    /**
     * 
     * @param name
     * @param port
     * @param runas
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @return 
     */
    public static SocketConnection buildConnection( String name, int port, ConnectionType runas, 
            ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, ExecutorService threadPool ) {
        return buildConnection( name, port, runas, readQueue, readBufferSize, threadPool, ActiveConnection.DEFAULT_WRITE_BACK_PRESSURE_THRESHOLD, 
                ActiveConnection.DEFAULT_WRITE_QUEUE_ABSOLUTE_THRESHOLD );
    }
    
    /**
     * 
     * Creates a read only SocketServer
     * 
     * @param name
     * @param port
     * @param runas
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param backpressureThreshold
     * @param absoluteThreshold
     * @return 
     */
    public static SocketConnection buildConnection( String name, int port, ConnectionType runas, 
            ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, ExecutorService threadPool, long backpressureThreshold, 
            long absoluteThreshold ) {
        
        return new SocketServer( name, port, runas, readQueue, readBufferSize, threadPool, backpressureThreshold, absoluteThreshold );
    }
    
    /**
     * 
     * @param name
     * @param host
     * @param port
     * @param runas
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param backpressureThreshold
     * @param absoluteThreshold
     * @param purgeOnDisconnect
     * @return 
     */
    public static SocketConnection buildConnection( String name, String host, int port, ConnectionType runas, 
            ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, ExecutorService threadPool, long backpressureThreshold, 
            long absoluteThreshold, boolean purgeOnDisconnect ) {
        return buildConnection( name, host, port, runas, readQueue, readBufferSize, threadPool, backpressureThreshold, absoluteThreshold, 
                purgeOnDisconnect, null, null );
    }
    
    /**
     * 
     * @param name
     * @param host
     * @param port
     * @param runas
     * @param readQueue
     * @param readBufferSize
     * @param threadPool
     * @param backpressureThreshold
     * @param absoluteThreshold
     * @param purgeOnDisconnect
     * @param readCounter
     * @param writeCounter
     * @return 
     */
    public static SocketConnection buildConnection( String name, String host, int port, ConnectionType runas, 
            ExecutorCompletionService<Optional<String>> readQueue, int readBufferSize, ExecutorService threadPool, long backpressureThreshold,
            long absoluteThreshold, boolean purgeOnDisconnect, LongAdder readCounter, LongAdder writeCounter ) {
        if( runas.isServer() ) {
            return new SocketServer( name, port, runas, readQueue, readBufferSize, threadPool, SocketServer.DEFAULT_CLIENT_IDLE_THRESHOLD_MS, 
                    backpressureThreshold, absoluteThreshold, readCounter, writeCounter );
        } else {
            return new SocketClient( name, host, port, runas, readQueue, readBufferSize, threadPool, backpressureThreshold, absoluteThreshold, 
                    purgeOnDisconnect, readCounter, writeCounter );
        }
        
    }
    
}
