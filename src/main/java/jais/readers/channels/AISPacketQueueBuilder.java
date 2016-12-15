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

package jais.readers.channels;

import jais.readers.threads.PacketBuilderWorkerThread;
import jais.AISPacket;
import jais.readers.AISPacketBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.concurrent.ExecutorCompletionService;
import java.util.regex.Matcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 */
public class AISPacketQueueBuilder implements Runnable, Closeable, AutoCloseable {

    // constants
    private final static Logger LOG = LogManager
            .getLogger( AISPacketQueueBuilder.class );
    
    private final static int DEFAULT_READ_BUFFER_SIZE = 65536;

    // socket related elements
    private final SocketAddress _targetISA;
    private SocketChannel _socketChannel;
    private Selector _socketSelector;

    // buffers and queues
    private ByteBuffer _readBuffer;
    private StringBuilder _sb = new StringBuilder();
    private final ExecutorCompletionService<AISPacket[]> _pktQueue;
    private AISPacketBuffer _pBuffer = new AISPacketBuffer();

    // everything else
    private String _source = "UNNAMED";
    private boolean _running = true;
    private final static boolean RECONNECT = true;

    /**
     *
     * @param source
     * @param isa
     * @param pktQueue
     * @param readBufferSize
     */
    public AISPacketQueueBuilder( String source, InetSocketAddress isa,
            ExecutorCompletionService<AISPacket[]> pktQueue, int readBufferSize ) {
        _source = source;
        _targetISA = isa;
        _pktQueue = pktQueue;
        _readBuffer = ByteBuffer.allocate( readBufferSize );
        LOG.fatal( "########################################################################" );
        LOG.fatal( "Instantiated new AISStream \"{}\"", _source );
        LOG.fatal( "{} Binding:{}...", _source, _targetISA );
    }

    /**
     *
     * @param source
     * @param isa
     * @param pktQueue
     */
    public AISPacketQueueBuilder( String source, InetSocketAddress isa,
            ExecutorCompletionService<AISPacket[]> pktQueue ) {
        this( source, isa, pktQueue, DEFAULT_READ_BUFFER_SIZE );
    }

    /**
     *
     * @param source
     * @param sc
     * @param pktQueue
     * @param readBufferSize
     * @throws java.io.IOException
     */
    public AISPacketQueueBuilder( String source, SocketChannel sc,
            ExecutorCompletionService<AISPacket[]> pktQueue, int readBufferSize ) throws IOException {
        _source = source;
        _targetISA = sc.getRemoteAddress();
        _socketChannel = sc;
        _pktQueue = pktQueue;
        _readBuffer = ByteBuffer.allocate( readBufferSize );
        LOG.fatal( "########################################################################" );
        LOG.fatal( "Instantiated new AISStream \"{}\"", _source );
        LOG.fatal( "# {} Establishing selector...", _source );
        _socketSelector = _socketChannel.provider().openSelector();
    }

    /**
     *
     * @param source
     * @param sc
     * @param pktQueue
     * @throws java.io.IOException
     */
    public AISPacketQueueBuilder( String source, SocketChannel sc,
            ExecutorCompletionService<AISPacket[]> pktQueue ) throws IOException {
        this( source, sc, pktQueue, DEFAULT_READ_BUFFER_SIZE );
    }

    /**
     *
     */
    private void init() {
        LOG.fatal( "########################################################################" );
        LOG.fatal( "# {} Initializing AISPacketQueueBuilder...", _source );
        LOG.fatal( "# {} Initialization complete!", _source );
        LOG.fatal( "########################################################################" );
    }

    /**
     *
     * @return
     */
    public String getName() {
        return _source;
    }

    /**
     *
     */
    @Override
    public void run() {
        init();

        try {
            Thread.currentThread().setName( "AECSF-" + _source );

            while( _running ) {
                try {
                    if( _socketChannel == null || !_socketChannel.isConnected() ) {
                        LOG.fatal( "{} - SocketChannel is null or disconnected.  (Re)starting connection...", _source );
                        _socketChannel = SocketChannel.open();
                        _socketSelector = SelectorProvider.provider().openSelector();
                        _socketChannel.configureBlocking( false );
                        _socketChannel.register( _socketSelector, SelectionKey.OP_CONNECT );
                        _socketChannel.connect( _targetISA );
                    }

                    if( _socketSelector.select() > 0 ) {
                        Iterator selectedKeys = _socketSelector.selectedKeys().iterator();

                        while( selectedKeys.hasNext() ) {
                            SelectionKey key = ( SelectionKey ) selectedKeys.next();
                            LOG.debug( "{} new Key: {} ({2})", _source, key, key.getClass() );
                            selectedKeys.remove();

                            if( !key.isValid() ) {
                                LOG.debug( "{} key is invalid.", _source );
                                continue;
                            }

                            if( key.isReadable() ) {
                                read( key );
                            } else if( key.isConnectable() && RECONNECT ) {
                                connect( key );
                            } else if( key.isAcceptable() ) {
                                LOG.debug( "{} key is acceptable", _source );
                            }
                        }
                    }
                } catch( IOException ioe ) {
                    LOG.fatal( "{} - !! Connection error: {}", _source, ioe.getMessage(), ioe );
                    try {
                        _socketChannel.close();
                    } catch( IOException ex ) {
                    }
                    _socketChannel = null;

                    try {
                        LOG.fatal( "{} - Sleeping for 5 seconds before reconnecting...", _source );
                        Thread.sleep( 5000 );
                    } catch( InterruptedException ie ) {
                    }
                }
            }

            LOG.fatal( "{} - Shutting down AISStream on {}", _source, _targetISA );
            LOG.fatal( "{} - AISStream shutdown complete.", _source );
        } finally {
            close();
        }
    }

    /**
     *
     * @param key
     * @throws IOException
     */
    private void connect( SelectionKey key ) throws IOException {
        LOG.fatal( "{} connect event detected.", _source );
        _socketChannel = ( SocketChannel ) key.channel();
        LOG.fatal( "{} completing connection...", _source );
        _socketChannel.finishConnect();
        _socketChannel.register( _socketSelector, SelectionKey.OP_READ );

        if( _socketChannel.isConnected() ) {
            LOG.fatal( "{} connection to {} completed successfully.", _source, _targetISA );
        } else {
            throw new IOException( _source + " connection to " + _targetISA
                    + " closed by server!" );
        }
    }

    /**
     *
     * @param key
     * @throws IOException
     */
    private void read( SelectionKey key ) throws IOException {
        _socketChannel = ( SocketChannel ) key.channel();

        try {
            // clear the buffer
            _readBuffer.clear();
            // fill the buffer
            int numRead = _socketChannel.read( _readBuffer );

            if( numRead == -1 ) {
                key.channel().close();
                key.cancel();
            } else {
                LOG.debug( "Read {} bytes...", numRead );

                // process the contents of the buffer
                for( int c : _readBuffer.array() ) {
                    if( !_running ) {
                        break;
                    }

                    try {
                        Matcher m = AISPacket.PREAMBLE_PATTERN.matcher( _sb );
                        // new line + carriage return means end of packet
                        if( c == '\n' || c == '\r' || m.find() ) {
                            if( _sb.length() > 0 ) {
                                String packetString = AISPacketQueueBuilder.getSubstring( _sb );

                                if( packetString != null && !packetString.isEmpty() ) {
                                    LOG.debug( "{} - Truncated String is: {}", _source, packetString );
                                    _pktQueue.submit( new PacketBuilderWorkerThread( _pBuffer, packetString, _source ) );
                                    _sb.delete( 0, packetString.length() );
                                }
                            }
                        } else {
                            _sb.append( ( char ) c );
                        }
                    } catch( BufferUnderflowException bue ) {
                        // this should never happen
                        LOG.fatal( bue.getMessage(), bue );
                    } catch( NullPointerException npe ) {
                        LOG.fatal( "NullPointerException: ", npe );
                    } catch( Exception e ) {
                        LOG.fatal( "Unanticipated fault: " + e.getMessage(), e );
                    }
                }

                if( _sb.length() > 0 ) {
                    _sb.replace( 0, _sb.length(), _sb.toString().trim() );
                }

                LOG.debug( "{} leftovers: {}", _source, _sb );
            }
        } catch( IOException ioe ) {
            LOG.fatal( "{} !! IOException thrown.", _source );
            LOG.debug( "{} - failed to read from buffer: {}", _source, ioe.getMessage(), ioe );
            LOG.info( "{} cancelling key.", _source );
            key.cancel();
            LOG.info( "{} closing socket.", _source );
            _socketChannel.close();
            throw new IOException( ioe );
        } finally {
            // a bit paranoid, but no harm done
            _readBuffer.clear();
        }
    }

    /**
     *
     * @param sb
     * @return
     */
    public static String getSubstring( StringBuilder sb ) {
        String substring = sb.substring( 0, AISPacketQueueBuilder.getTruncIndex( sb ) ).trim();

        return substring;
    }

    /**
     *
     * @param sb
     * @return
     */
    public static int getTruncIndex( StringBuilder sb ) {
        int truncIndex;

        LOG.debug( "Evaluating \"{}\" for truncation point.", sb );

        Matcher m = AISPacket.PREAMBLE_PATTERN.matcher( sb.toString() );
        m.find();
        
        if( m.groupCount() > 0 ) {
            LOG.debug( "More than one occurence of preamble \"" + 
                    AISPacket.PREAMBLE_PATTERN + "\" in string." );
            truncIndex = sb.indexOf( m.group(2) );
        } else if( sb.indexOf( "\n\r" ) > -1 ) {
            LOG.debug( "String is terminated by a newline and carriage return" );
            truncIndex = sb.indexOf( "\n\r" );
        } else if( sb.indexOf( "\n" ) > -1 ) {
            LOG.debug( "String is terminated by a newline" );
            truncIndex = sb.indexOf( "\n" );
        } else if( sb.indexOf( "\r" ) > -1 ) {
            LOG.debug( "String is terminated by a carriage return" );
            truncIndex = sb.indexOf( "\r" );
        } else {
            LOG.debug( "Line should not be truncated." );
            truncIndex = sb.length();
        }

        LOG.debug( "Truncation index set to {}", truncIndex );

        return truncIndex;
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.fatal( "{} - Closing AISPacketQueueBuilder...", _source );
        _running = false;

        try {
            LOG.fatal( "{} - Closing SocketChannel...", _source );
            if( _socketChannel != null ) {
                _socketChannel.close();
            }
        } catch( IOException ioe ) {
        }

        try {
            LOG.fatal( "{} - Closing SocketSelector...", _source );
            if( _socketSelector != null ) {
                _socketSelector.close();
            }
        } catch( IOException ioe ) {
        }

        LOG.fatal( "{} - Closing AISPacketBuffer...", _source );
        if( _pBuffer != null ) {
            _pBuffer.close();
        }

        LOG.fatal( "{} - Clearing ByteBuffer...", _source );
        if( _readBuffer != null ) {
            _readBuffer.clear();
        }

        _socketChannel = null;
        _socketSelector = null;
        _pBuffer = null;
        _readBuffer = null;
        _sb = null;

        LOG.fatal( "{} - AISPacketQueueBuilder closed.", _source );

        try {
            Thread.currentThread().join( 1000 );
        } catch( InterruptedException ie ) {
        }
    }
}
