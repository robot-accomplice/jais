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

package jais.io;

import jais.AISPacket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISPacketBuffer {

    private final static Logger LOG = LogManager.getLogger( AISPacketBuffer.class );

    private final Map<String, AISPacketSet> _buffer = new ConcurrentHashMap<>();  // Map is used to avoid Java 7/8 cross version compatibility issues
    private final int _maxPacketAge;

    public final static int DEFAULT_MAX_PACKET_AGE = 360000; // six minutes

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
        if( packet == null ) throw new NullPointerException( "Packet is null!" );
        if( packet.getSource() == null ) packet.setSource( AISPacket.str2bArray( "UNKNOWN" ) );
        return new String( packet.getSource() ) + packet.getSequentialMessageId() + "_" + packet.getFragmentCount();
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
        return _buffer.containsKey( packetKey ) && _buffer.get( packetKey ).isComplete();
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
     * 
     * @return 
     */
    public int purgeExpired() {
        return purgeExpired( _maxPacketAge );
    }
    
    /**
     * Removes any expired AISPacketSets from the buffer.  Sets are considered to be expired when the first packet in
     * the set is older than the max packet age set during AISPacketBuffer initialization
     * 
     * @param thresholdMs
     * @return 
     */
    public int purgeExpired( long thresholdMs ) {
        int purgeCount = 0;
        
        if( _buffer.isEmpty() ) {
            // do nothing
        } else {
            for( String key : _buffer.keySet() ) {
                if( _buffer.containsKey( key ) ) {
                    AISPacketSet set = _buffer.get( key );
                    if( set == null || set.isExpired( thresholdMs ) ) {
                        purgeCount++;
                        _buffer.remove( key );
                    }
                }
            }
        }
        
        return purgeCount;
    }

    /**
     *
     */
    public void close() {
        LOG.info( "Closing AISPacketBuffer..." );
        if( _buffer != null ) _buffer.clear();
    }

    /**
     * ***********************************************************************
     */
    private class AISPacketSet {

        private final ZonedDateTime _timestamp = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
        private final AISPacket [] _packets;
        private final int _sequenceNumber;
        private final int _fragmentCount;
        private final byte [] _source;

        /**
         *
         * @param packet
         */
        public AISPacketSet( AISPacket packet ) {
            _packets = new AISPacket[ packet.getFragmentCount() ];
            _packets[0] = packet;
            _sequenceNumber = packet.getSequentialMessageId();
            _fragmentCount = packet.getFragmentCount();
            _source = packet.getSource();
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
            return _packets;
        }

        /**
         *
         * @return
         */
        public ZonedDateTime getTimestamp() {
            return _timestamp;
        }

        /**
         * 
         * @return 
         */
        public long getAgeInMilliseconds() {
            ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
            return now.toInstant().toEpochMilli() - _timestamp.toInstant().toEpochMilli();
        }
        
        /**
         * 
         * @param expiredThresholdMs
         * @return 
         */
        public boolean isExpired( long expiredThresholdMs ) {
            return ( getAgeInMilliseconds() > _maxPacketAge );
        }
        
        /**
         * 
         * @return 
         */
        public boolean isExpired() {
            return isExpired( _maxPacketAge );
        }

        /**
         *
         * @return
         */
        public int getSize() {
            return _packets.length;
        }
        
        /**
         * 
         * @return 
         */
        public String getSource() {
            return AISPacket.bArray2Str( _source );
        }

        /**
         *
         * @param packet
         */
        public synchronized void add( AISPacket packet ) {
            // make sure this fragment number doesn't exceed the size of the array
            if( packet.getFragmentNumber() <= _fragmentCount ) {
                _packets[ packet.getFragmentNumber() - 1 ] = packet; // packet fragments are numbered starting at 1
            }
        }

        /**
         *
         * @return
         */
        public boolean isComplete() {
            boolean complete = true;
            
            if( _packets == null || _packets[0] == null ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Packet set is empty." );
                complete = false;
            } else if( _packets[0].getFragmentCount() > _packets.length ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Fragment count " + _packets[0].getFragmentCount() + " > " + _packets.length );
                
                // check age of first packet to see if it's time to retire the set
                ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
                if( _packets[0].getTimeReceived().isAfter( now.plus( _maxPacketAge, ChronoUnit.MILLIS ) ) ) {
                    complete = false;
                } else if( LOG.isDebugEnabled() ) {
                    LOG.debug( "Packetset has timed out" );
                }
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
            if( o == null ) return false;
            if( !( o instanceof AISPacketSet ) ) return false;
            
            AISPacketSet other = ( AISPacketSet )o;
            if( other._sequenceNumber != _sequenceNumber ) return false;
            
            return Arrays.equals( _source, other._source );
        }

        /**
         *
         * @return
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this._sequenceNumber;
            hash = 17 * hash + Arrays.hashCode( _source );
            
            return hash;
        }
    }
}
