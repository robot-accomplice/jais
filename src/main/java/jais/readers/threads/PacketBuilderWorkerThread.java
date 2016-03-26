/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class PacketBuilderWorkerThread implements Callable {

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

        AISPacket[] packets = null;

        try {
            LOG.trace( "Processing packet: {}", _packetString );
            AISPacket packet = new AISPacket( _packetString, _source );

            packet.process();
            packets = _pBuffer.add( packet, true ); // true = remove from buffer if complete
        } catch( AISPacketException ape ) {
            LOG.info( "Discarding invalid packet: \"" + _packetString + "\": " + ape.getMessage(), ape );
        }

        return packets;
    }
}
