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

package jais.readers;

import jais.messages.AISMessageFactory;
import jais.AISPacket;
import jais.exceptions.AISException;
import jais.exceptions.AISPacketException;
import jais.handlers.AISHandler;
import jais.handlers.AISMessageHandler;
import jais.handlers.AISPacketHandler;
import jais.messages.AISMessage;
import java.util.Observable;
import org.apache.logging.log4j.*;

/**
 *
 * @author Jonathan Machen
 */
public abstract class AISReaderBase extends Observable implements AISReader {

    private final static Logger LOG = LogManager.getLogger( AISReaderBase.class );

    AISMessageHandler _messageHandler;
    AISPacketHandler _packetHandler;
    private final AISPacketBuffer _buffer = new AISPacketBuffer();
    protected boolean _shouldRun = true;
    protected String _source;

    /**
     * for use with observers in lieu of direct handler calls
     */
    AISReaderBase() {
    }

    /**
     *
     * @param handler
     */
    AISReaderBase( AISHandler handler ) {
        if( handler instanceof AISPacketHandler ) {
            _packetHandler = ( AISPacketHandler ) handler;
        }
        if( handler instanceof AISMessageHandler ) {
            _messageHandler = ( AISMessageHandler ) handler;
        }
    }

    /**
     *
     * @param packetHandler
     * @param messageHandler
     */
    AISReaderBase( AISPacketHandler packetHandler, AISMessageHandler messageHandler ) {
        _packetHandler = packetHandler;
        _messageHandler = messageHandler;
    }

    /**
     *
     * @param packetString
     */
    final void processPacketString( String packetString ) throws AISPacketException {
        if( packetString == null || packetString.isEmpty() ) {
            throw new AISPacketException( "Empty packetString" );
        }

        String[] embeddedPackets = AISPacket.fastSplit( packetString, '!' );

        if( embeddedPackets.length > 0 ) {
            for( String ps : embeddedPackets ) {
                if( ps.length() > 1 ) processSingleton( "!" + ps ); // don't bother to process Strings that contain only !
            }
        } else {
            processSingleton( packetString );
        }
    }

    /**
     *
     * @param packetString
     */
    private void processSingleton( String packetString ) {
        try {
            if( packetString != null && !packetString.isEmpty() ) {
                LOG.debug( "Found packet to test: " + packetString );

                AISPacket packet = new AISPacket( packetString, _source );
                packet.process();

                // send packet to observers
                super.setChanged();
                super.notifyObservers( packet );

                // send packet to observers
                if( _packetHandler != null ) _packetHandler.processPacket( packet );

                try {
                    // assemble the message fragments, THEN decode
                    LOG.debug( "Adding message part " + packet.getFragmentNumber()
                            + " of " + packet.getFragmentCount() + " : "
                            + packet.getRawPacket() + " to buffer." );

                    AISPacket [] packets = _buffer.add( packet );

                    if( packets != null ) {
                        // last fragment, we can now build the full message
                        LOG.debug( "Processing a message with "
                                + packets.length + " fragments." );

                        AISMessage message = AISMessageFactory.create( packets );

                        // send message to observers
                        super.setChanged();
                        super.notifyObservers( message );
                        
                        // send message to handlers
                        if( _messageHandler != null ) _messageHandler.processMessage( message );
                    } else if( LOG.isTraceEnabled() ) {
                        LOG.trace( "Message is not complete." );
                    }
                } catch( AISException ae ) {
                    LOG.debug( "Encountered an error while processing packet \""
                            + packetString + "\": "
                            + ae.getMessage() );
                    LOG.trace( ae.getMessage(), ae );
                }
            }
        } catch( AISPacketException ipe ) {
            LOG.debug( "Encountered a serious error while trying to process a packet string: "
                    + ipe.getMessage() );
            LOG.trace( "StackTrace for AISPacketException: " + ipe.getMessage(), ipe );
        }
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.fatal( "AISReaderBase.close() invoked." );
        _buffer.close();
        _shouldRun = false;
        LOG.fatal( "Reader successfully closed." );
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            read();
        } catch( AISReaderException are ) {
            LOG.fatal( "Encountered a fatal error while reading the datasource: "
                    + are.getMessage(), are );
        } finally {
            close();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public long getBadPacketCount() {
        return -1;
    }
}
