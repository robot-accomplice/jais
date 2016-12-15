/*
 * Copyright 2016 Jonathan Machen <jon.machen@gmail.com>.
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
import jais.readers.AISPacketBuffer;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class PacketBuilderWorkerThread implements Callable<AISPacket []> {

    private final static Logger LOG = LogManager.getLogger(
            PacketBuilderWorkerThread.class );

    private final String _packetString;
    private final AISPacketBuffer _pBuffer;
    private final String _source;

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
     * @return
     */
    @Override
    public AISPacket[] call() {
        Thread.currentThread().setName( "PBT" );

        try {
            LOG.trace( "Processing packet: {}", _packetString );
            AISPacket packet = new AISPacket( _packetString, _source );

            return _pBuffer.add( packet.process(), true ); // true = remove from buffer if complete
        } catch( AISPacketException ape ) {
            LOG.info( "Discarding invalid packet: \"" + _packetString + "\": " + ape.getMessage(), ape );
        }
        
        return null;
    }
}
