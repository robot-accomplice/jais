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

package jais.readers.threads;

import jais.AISPacket;
import jais.exceptions.AISPacketException;
import jais.io.AISPacketBuffer;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class PacketBuilderWorkerThread implements Callable<Optional<AISPacket []>> {

    private final static Logger LOG = LogManager.getLogger( PacketBuilderWorkerThread.class );

    private final AISPacketBuffer _pBuffer;
    private String _packetString;
    private final String _source;
    private volatile boolean _idle;

    /**
     *
     * @param pBuffer
     * @param packetString
     * @param source
     */
    public PacketBuilderWorkerThread( AISPacketBuffer pBuffer, String packetString,
            String source ) {
        _pBuffer = pBuffer;
        _packetString = packetString;
        _source = source;
    }
    
    /**
     * 
     * @param pBuffer 
     * @param source 
     */
    public PacketBuilderWorkerThread( AISPacketBuffer pBuffer, String source ) {
        _pBuffer = pBuffer;
        _source = source;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isIdle() {
        return _idle;
    }
    
    /**
     * 
     * @param packetString
     * @return 
     */
    public PacketBuilderWorkerThread init( String packetString ) {
        _packetString = packetString;
        return this;
    }
    
    /**
     *
     * @return
     */
    @Override
    public Optional<AISPacket[]> call() {
        _idle = false;

        try {
            LOG.trace( "Processing packet: {}", _packetString );
            // true = remove from buffer if complete
            return _pBuffer.add( new AISPacket( _packetString, _source ).process(), true );
        } catch( AISPacketException ape ) {
            LOG.info( "Discarding invalid packet: \"" + _packetString + "\": " + ape.getMessage(), ape );
        } finally {
            _idle = true;
        }
        
        return Optional.empty();
    }
}
