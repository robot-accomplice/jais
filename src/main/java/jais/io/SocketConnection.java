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
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public interface SocketConnection extends AutoCloseable {
    
    public abstract void init();
    
    public abstract void setParams( Map<String, String> params );
    
    public abstract void connect() throws IOException;
    
    public abstract void connect( boolean reuseAddress ) throws IOException;

    public abstract void writeln( String line );

    public abstract long getCurrentLinesWritten();

    public abstract long getTotalLinesWritten();

    public abstract long getTotalConnectAttempts();

    public abstract long getWriteQueueSize();

    public abstract long getTotalLinesRead();

    public abstract long getCurrentLinesRead();
    
    public abstract boolean isConnected();
    
    public abstract OffsetDateTime getLastReadTime();
    
    public abstract long getMinutesSinceLastRead();
    
    public abstract long getMinutesSinceLastWrite();
    
    public abstract OffsetDateTime getLastWriteTime();
    
    public abstract int getActiveConnections();
    
    public abstract ConnectionType getConnectionType();
    
    @Override
    public abstract void close();
}
