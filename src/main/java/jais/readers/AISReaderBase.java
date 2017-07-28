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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Jonathan Machen
 */
public abstract class AISReaderBase extends Observable implements AISReader {

    private final static Logger LOG = LoggerFactory.getLogger( AISReaderBase.class );
    protected final static String DEFAULT_SOURCE = "UNSPECIFIED";

    AISMessageHandler _messageHandler;
    AISPacketHandler _packetHandler;
    private final AISPacketBuffer _buffer = new AISPacketBuffer();
    protected boolean _shouldRun = true;
    protected String _source = DEFAULT_SOURCE;

    /**
     * for use with observers in lieu of direct handler calls
     */
    AISReaderBase() {
        this( DEFAULT_SOURCE );
    }
    
    /**
     * 
     * @param source 
     */
    AISReaderBase( String source ) {
        _source = source;
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
        _source = DEFAULT_SOURCE;
    }

    /**
     *
     * @param handler
     */
    AISReaderBase( AISHandler handler, String source ) {
        if( handler instanceof AISPacketHandler ) {
            _packetHandler = ( AISPacketHandler ) handler;
        }
        if( handler instanceof AISMessageHandler ) {
            _messageHandler = ( AISMessageHandler ) handler;
        }
        _source = source;
    }

    /**
     *
     * @param packetHandler
     * @param messageHandler
     */
    AISReaderBase( AISPacketHandler packetHandler, AISMessageHandler messageHandler ) {
        _packetHandler = packetHandler;
        _messageHandler = messageHandler;
        _source = DEFAULT_SOURCE;
    }

    /**
     *
     * @param packetHandler
     * @param messageHandler
     */
    AISReaderBase( AISPacketHandler packetHandler, AISMessageHandler messageHandler, String source ) {
        _packetHandler = packetHandler;
        _messageHandler = messageHandler;
        _source = source;
    }

    /**
     * 
     * @param source 
     */
    public void setSource( String source ) {
       _source = source; 
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
        // hacky NPE prevention for the short term
        if( _source == null ) {
            _source = DEFAULT_SOURCE;
        }
        
        try {
            if( packetString != null && !packetString.isEmpty() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Found packet to test: " + packetString );

                AISPacket packet = new AISPacket( packetString, _source ); // make sure we never 
                packet.process();

                // send packet to observers
                super.setChanged();
                super.notifyObservers( packet );

                // send packet to observers
                if( _packetHandler != null ) _packetHandler.processPacket( packet );

                try {
                    // assemble the message fragments, THEN decode
                    if( LOG.isDebugEnabled() ) LOG.debug( "Adding message part {} of {} : '{}' to buffer.", 
                            packet.getFragmentNumber(), packet.getFragmentCount(), packet.getRawPacket() );

                    AISPacket [] packets = _buffer.add( packet );

                    if( packets != null ) {
                        // last fragment, we can now build the full message
                        if( LOG.isDebugEnabled() ) LOG.debug( "Processing a message with {} fragments.",
                                packets.length );

                        AISMessage message = AISMessageFactory.create( _source, packets );

                        // send message to observers
                        super.setChanged();
                        super.notifyObservers( message );
                        
                        // send message to handlers
                        if( _messageHandler != null ) _messageHandler.processMessage( message );
                    } else if( LOG.isTraceEnabled() ) {
                        LOG.trace( "Message is not complete." );
                    }
                } catch( AISException ae ) {
                    if( LOG.isDebugEnabled() ) LOG.debug( "Encountered an error while processing packet \"{}\": {}",
                            packetString, ae.getMessage() );
                    if( LOG.isTraceEnabled() ) LOG.trace( ae.getMessage(), ae );
                }
            }
        } catch( AISPacketException ipe ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Encountered a serious error while trying to process a packet string: "
                    + ipe.getMessage() );
            if( LOG.isTraceEnabled() ) LOG.trace( "StackTrace for AISPacketException: {}", ipe.getMessage(), ipe );
        }
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.info( "AISReaderBase.close() invoked." );
        _buffer.close();
        _shouldRun = false;
        LOG.info( "Reader successfully closed." );
    }

    /**
     *
     */
    @Override
    public void run() {
        try {
            read();
        } catch( AISReaderException are ) {
            LOG.error( "Encountered a fatal error while reading the datasource: {}",
                    are.getMessage(), are );
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
