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

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public abstract class SocketConnectionBase implements SocketConnection {

    protected String _name;
    
    // basic connection info
    protected String _host;
    protected int _port;
    protected ConnectionType _type;
    
    // threads & queues
    protected ExecutorCompletionService<String> _readQueue;
    protected ExecutorService _threadPool;

    // counters used to track connection statistics
    final public LongAdder _totalConnectAttempts = new LongAdder();
    
    /**
     * 
     * @return 
     */
    @Override
    public int getActiveConnections() {
        if( this.isConnected() ) return 1;
        return 0;
    }
    
    /**
     * 
     * @return 
     */
    @Override
    public long getTotalConnectAttempts() {
        return _totalConnectAttempts.sum();
    }
}
