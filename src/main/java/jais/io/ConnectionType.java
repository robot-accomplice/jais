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

/**
 * 
 * @author Jonathan Machen {@literal <jon.machen@robotaccomplice.com>}
 */
public enum ConnectionType {
    
    READ_ONLY_TCP_SERVER( true, true, false ),
    WRITE_ONLY_TCP_SERVER( true, false, true ),
    READ_WRITE_TCP_SERVER( true, true, true ),
    READ_ONLY_TCP_CLIENT( false, true, false ),
    WRITE_ONLY_TCP_CLIENT( false, false, true ),
    READ_WRITE_TCP_CLIENT( false, true, true );
    
    private final boolean _server;
    private final boolean _readable;
    private final boolean _writeable;
    
    /**
     * 
     */
    ConnectionType( boolean server, boolean readable, boolean writeable ) {
        _server = server;
        _readable = readable;
        _writeable = writeable;
    }

    /**
     * 
     * @return 
     */
    public boolean isServer() {
        return _server;
    }

    /**
     * 
     * @return 
     */
    public boolean isReadable() {
        return _readable;
    }

    /**
     * 
     * @return 
     */
    public boolean isWriteable() {
        return _writeable;
    }
}
