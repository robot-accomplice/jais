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

package jais.readers;

import jais.handlers.AISHandler;
import jais.handlers.AISMessageHandler;
import jais.handlers.AISPacketHandler;
import java.io.IOException;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISSocketReader extends AISStreamReader {
    
    private final static Logger LOG = LogManager.getLogger( AISSocketReader.class );
    private final static int DEFAULT_BUFFER_SIZE = 16384;

    private final Socket _s;
    
    private final static int TIMEOUT = 30000;
    
    /**
     * 
     * @param s 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s ) throws IOException {
        this( s, DEFAULT_BUFFER_SIZE );
    }

    /**
     * 
     * @param s
     * @param bufferSize 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, int bufferSize ) throws IOException {
        super( s.getInputStream(), bufferSize );
        _s = s;
    }
    
    /**
     * 
     * @param s 
     * @param handler 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISHandler handler ) throws IOException {
        this( s, handler, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param s
     * @param handler 
     * @param bufferSize 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISHandler handler, int bufferSize ) throws IOException {
        this( s, handler, AISReaderBase.DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param s
     * @param handler
     * @param source
     * @param bufferSize 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISHandler handler, String source, int bufferSize ) throws IOException {
        super( s.getInputStream(), handler, source, bufferSize );
        _s = s;
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler 
     * @throws java.io.IOException 
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, AISMessageHandler msgHandler ) throws IOException {
        this( s, pktHandler, msgHandler, AISReaderBase.DEFAULT_SOURCE, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler 
     * @param bufferSize
     * @throws java.io.IOException
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, AISMessageHandler msgHandler, int bufferSize ) throws IOException {
        this( s, pktHandler, msgHandler, AISReaderBase.DEFAULT_SOURCE, bufferSize );
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler
     * @param source
     * @throws IOException 
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, AISMessageHandler msgHandler, String source ) throws IOException {
        this( s, pktHandler, msgHandler, source, DEFAULT_BUFFER_SIZE );
    }
    
    /**
     * 
     * @param s
     * @param pktHandler
     * @param msgHandler
     * @param source
     * @param bufferSize
     * @throws IOException 
     */
    public AISSocketReader( Socket s, AISPacketHandler pktHandler, AISMessageHandler msgHandler, String source, int bufferSize ) throws IOException {
        super( s.getInputStream(), pktHandler, msgHandler, source, bufferSize );
        _s = s;
    }
    
    /**
     * 
     * @throws jais.readers.AISReaderException
     */
    @Override
    public void read() throws AISReaderException {
        try {
            super.read();
        } finally {
            try {
                LOG.error( "Socket is no longer valid.  Closing." );
                _s.close();            
            } catch( IOException ioe ) {
                // do nothing
            }
        }
        
        LOG.error( "Read terminated." );
    }
}
