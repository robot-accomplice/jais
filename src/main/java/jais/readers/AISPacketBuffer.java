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

import jais.AISPacket;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.MutableDateTime;

/**
 *
 * @author Jonathan Machen
 */
public class AISPacketBuffer {

    private final static Logger LOG = LogManager.getLogger( AISPacketBuffer.class );

    private final Map<String, AISPacketSet> _buffer = new ConcurrentHashMap<>();  // Map is used to avoid Java 7/8 cross version compatibility issues
    private int _maxPacketAge;

    public final static int DEFAULT_MAX_PACKET_AGE = 60000; // one minute

    /**
     *
     * @param maxPacketAge
     */
    public AISPacketBuffer( int maxPacketAge ) {
        if( LOG.isDebugEnabled() ) LOG.debug( "AISPacketBuffer instantiated.  Max packet age is {} ms", maxPacketAge );
        _maxPacketAge = maxPacketAge;
    }

    /**
     *
     */
    public AISPacketBuffer() {
        this( DEFAULT_MAX_PACKET_AGE );
    }

    /**
     *
     * @param packet
     * @return
     */
    private String getKey( AISPacket packet ) {
        return new StringBuilder( new String( packet.getSource() ) )
                .append( packet.getSequentialMessageId() )
                .append( "_" )
                .append( packet.getFragmentCount() ).toString();
    }

    /**
     *
     * @param packetKey
     * @return
     */
    public boolean has( String packetKey ) {
        return _buffer.containsKey( packetKey );
    }

    /**
     *
     * @param packet
     * @return
     */
    public boolean has( AISPacket packet ) {
        return has( getKey( packet ) );
    }

    /**
     *
     * @param packet
     * @return
     */
    public boolean isComplete( AISPacket packet ) {
        return isComplete( getKey( packet ) );
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private boolean isComplete( String packetKey ) {
        boolean complete = _buffer.containsKey( packetKey )
                && _buffer.get( packetKey ).isComplete();

        return complete;
    }

    /**
     *
     * @param packet
     * @return
     */
    public synchronized AISPacket[] add( AISPacket packet ) {
        return add( packet, false );
    }

    /**
     *
     * @param packet
     * @param removeIfComplete
     * @return
     */
    public synchronized AISPacket[] add( AISPacket packet, boolean removeIfComplete ) {
        AISPacket[] packets = null;

        if( packet == null ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring null packet." );
        } else {
            try {
                _buffer.keySet().stream().forEach( ( k ) -> {
                    try {
                        MutableDateTime timestamp = _buffer.get( k ).getTimestamp();
                        timestamp.addMillis( _maxPacketAge );

                        if( timestamp.isBeforeNow() ) {
                            if( LOG.isDebugEnabled() ) LOG.debug( "Removing expired packet set." );
                            _buffer.remove( k );
                        }
                    } catch( NullPointerException npe ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "NPE encountered while cleaning old records from buffer: {}", 
                                npe.getMessage(), npe );
                        _buffer.remove( k );
                    }
                });
            } catch( NullPointerException npe ) {
                LOG.info( "NPE encountered while cleaning old records from buffer. Concurrency issue?", npe );
            } catch( Throwable t ) {
                LOG.error( "Encountered an unanticipated fault: " + t.getMessage(), t );
            }

            String pk = getKey( packet );
            if( packet.getFragmentCount() > 1 ) {
                if( LOG.isTraceEnabled() ) LOG.trace( "This is a multi-packet message." );
                if( _buffer.containsKey( pk ) && _buffer.get( pk ) != null ) {
                    if( LOG.isTraceEnabled() ) LOG.trace( "Buffer already contains the first packet for this message." );
                    AISPacketSet aps = _buffer.get( pk );
                    aps.add( packet );
                } else {
                    if( LOG.isTraceEnabled() ) LOG.trace( "This is the first packet in this message sequence." );
                    AISPacketSet aps = new AISPacketSet( packet );
                    _buffer.put( pk, aps );
                }

                if( isComplete( packet ) ) {
                    if( removeIfComplete ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Removing completed packet set." );
                        packets = remove( packet );
                    } else {
                        packets = getPackets( packet );
                    }
                }
            } else {
                packets = new AISPacket[]{ packet };
                if( !removeIfComplete ) {
                    _buffer.put( pk, new AISPacketSet( packet ) );
                }
            }
        }

        return packets;
    }

    /**
     *
     * @param packet
     * @return
     */
    public synchronized AISPacket[] remove( AISPacket packet ) {
        String packetKey = getKey( packet );
        return remove( packetKey );
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private synchronized AISPacket[] remove( String packetKey ) {
        AISPacket[] packets = getPackets( packetKey );
        _buffer.remove( packetKey );
        return packets;
    }

    /**
     *
     * @param packet
     * @return
     */
    public synchronized AISPacket[] getPackets( AISPacket packet ) {
        return getPackets( getKey( packet ) );
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private AISPacket[] getPackets( String packetKey ) {
        AISPacket[] packets = null;

        if( _buffer.containsKey( packetKey ) ) {
            packets = _buffer.get( packetKey ).getPackets();
        }

        return packets;
    }

    /**
     *
     * @return
     */
    public int getBufferSize() {
        return _buffer.size();
    }

    /**
     *
     */
    public void close() {
        LOG.fatal( "Closing AISPacketBuffer..." );
    }

    /**
     *
     * @param packet
     * @return
     */
    public int getMessageSize( AISPacket packet ) {
        int size = 0;

        if( has( packet ) ) {
            size = _buffer.get( getKey( packet ) ).getSize();
        }

        return size;
    }

    /**
     * ***********************************************************************
     */
    private class AISPacketSet {

        private final MutableDateTime _timestamp = MutableDateTime.now();
        private final ArrayList<AISPacket> _packets = new ArrayList<>();
        private final int _sequenceNumber;
        private final int _fragmentCount;

        /**
         *
         * @param packet
         */
        public AISPacketSet( AISPacket packet ) {
            _packets.add( packet );
            _sequenceNumber = packet.getSequentialMessageId();
            _fragmentCount = packet.getFragmentCount();
        }

        /**
         *
         * @return
         */
        public int getSequenceNumber() {
            return _sequenceNumber;
        }

        /**
         *
         * @return
         */
        public int getFragmentCount() {
            return _fragmentCount;
        }

        /**
         *
         * @return
         */
        public AISPacket[] getPackets() {
            AISPacket[] packets = new AISPacket[_packets.size()];

            return _packets.toArray( packets );
        }

        /**
         *
         * @return
         */
        public MutableDateTime getTimestamp() {
            return _timestamp;
        }

        /**
         *
         * @return
         */
        public int getSize() {
            return _packets.size();
        }

        /**
         *
         * @param packet
         */
        public synchronized void add( AISPacket packet ) {
            _packets.add( packet );
        }

        /**
         *
         * @return
         */
        public boolean isComplete() {
            boolean complete = true;
            
            if( _packets.isEmpty() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet set is empty." );
                complete = false;
            } else if( _packets.get(0).getFragmentCount() > _packets.size() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Fragment count " + _packets.get(0).getFragmentCount() + " > " + _packets.size() );
                // we can't put this into place yet as the second or third packet 
                // in a multi-packet message may not contain enough information to tie
                // it to early packets in the same set (without having adequate source info)
//                complete = false;
            } else {
                complete = _packets.stream().map( 
                        (_packet ) -> ( _packet != null ) )
                        .reduce( complete, ( accumulator, _item ) -> accumulator & _item );
            }

            return complete;
        }

        /**
         *
         * @param o
         * @return
         */
        @Override
        public boolean equals( Object o ) {
            if( o instanceof AISPacketSet ) {
                return ( ( AISPacketSet ) o ).getSequenceNumber() == _sequenceNumber;
            } else {
                return false;
            }
        }

        /**
         *
         * @return
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this._sequenceNumber;
            return hash;
        }
    }
}
