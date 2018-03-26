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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISPacketBuffer implements AutoCloseable {

    private final static Logger LOG = LogManager.getLogger( AISPacketBuffer.class );

    private final Map<AISPacketSet.Key, AISPacketSet> _packetSetMap = new ConcurrentHashMap<>();  // Map is used to avoid Java 7/8 cross version compatibility issues
    private final int _maxPacketAge;

    public final static int DEFAULT_MAX_PACKET_AGE = 360000; // six minutes
    private boolean _closed = false;

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
    private AISPacketSet.Key getKey( AISPacket packet ) {
        if( packet == null ) throw new NullPointerException( "Packet is null!" );
        if( packet.getSource() == null ) packet.setSource( AISPacket.str2bArray( "UNKNOWN" ) );
        return new AISPacketSet.Key( packet.getSource(), packet.getSequentialMessageId(), packet.getFragmentCount() );
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private boolean has( AISPacketSet.Key packetKey ) {
        return _packetSetMap.containsKey( packetKey );
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
    private boolean isComplete( AISPacketSet.Key packetKey ) {
        return _packetSetMap.containsKey( packetKey ) && _packetSetMap.get( packetKey ).isComplete();
    }

    /**
     *
     * @param packet
     * @return
     */
    public Optional<AISPacket[]> add( AISPacket packet ) {
        return add( packet, false );
    }

    /**
     *
     * @param packet
     * @param removeIfComplete
     * @return
     */
    public Optional<AISPacket[]> add( AISPacket packet, boolean removeIfComplete ) {
        if( packet == null ) {
            if( LOG.isDebugEnabled() ) LOG.debug( "Ignoring null packet." );
        } else {
            AISPacketSet.Key pk = getKey( packet );
            
            if( packet.getFragmentCount() > 1 ) {
                if( LOG.isTraceEnabled() ) LOG.trace( "This is a multi-packet message." );
                if( _packetSetMap.containsKey( pk ) && _packetSetMap.get( pk ) != null ) {
                    if( LOG.isTraceEnabled() ) LOG.trace( "Buffer already contains the first packet for this message." );
                    AISPacketSet aps = _packetSetMap.get( pk );
                    aps.add( packet );
                } else {
                    if( LOG.isTraceEnabled() ) LOG.trace( "This is the first packet in this message sequence." );
                    AISPacketSet aps = new AISPacketSet( packet, _maxPacketAge );
                    _packetSetMap.put( pk, aps );
                }

                if( isComplete( packet ) ) {
                    if( removeIfComplete ) {
                        if( LOG.isDebugEnabled() ) LOG.debug( "Removing completed packet set." );
                        return remove( packet );
                    } else {
                        return getPackets( packet );
                    }
                }
            } else {
                if( !removeIfComplete ) {
                    _packetSetMap.put( pk, new AISPacketSet( packet, _maxPacketAge ) );
                }
                return Optional.of( new AISPacket[]{ packet } );
            }
        }
        
        return Optional.empty();
    }

    /**
     *
     * @param packet
     * @return
     */
    public Optional<AISPacket[]> remove( AISPacket packet ) {
        AISPacketSet.Key packetKey = getKey( packet );
        return remove( packetKey );
    }
    
    /**
     * 
     * @param set 
     */
    public void remove( AISPacketSet set ) {
        remove( set.getKey() );
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private Optional<AISPacket[]> remove( AISPacketSet.Key packetKey ) {
        Optional<AISPacket[]> packets = getPackets( packetKey );
        if( packets.isPresent() ) {
            _packetSetMap.remove( packetKey );
            return packets;
        }
        
        return Optional.empty();
    }

    /**
     *
     * @param packet
     * @return
     */
    public Optional<AISPacket[]> getPackets( AISPacket packet ) {
        return getPackets( getKey( packet ) );
    }
    
    /**
     * 
     * @return 
     */
    public Collection<AISPacketSet> getAllPacketSets() {
        return _packetSetMap.values();
    }

    /**
     *
     * @param packetKey
     * @return
     */
    private Optional<AISPacket[]> getPackets( AISPacketSet.Key packetKey ) {
        if( _packetSetMap.containsKey( packetKey ) ) {
            return Optional.of( _packetSetMap.get( packetKey ).getPackets() );
        }

        return Optional.empty();
    }

    /**
     *
     * @return
     */
    public int size() {
        return _packetSetMap.size();
    }

    /**
     *
     * @param packet
     * @return
     */
    public int getMessageSize( AISPacket packet ) {
        int size = 0;

        if( has( packet ) ) {
            size = _packetSetMap.get( getKey( packet ) ).getSize();
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
        LOG.fatal( "Purging AISPacketSets older than {}ms from AISPacketBuffer", thresholdMs );
        int purgeCount = 0;
        
        if( _packetSetMap.isEmpty() ) {
            // do nothing
            LOG.info( "AISPacketBuffer is empty.  Aborting." );
        } else {
            for( AISPacketSet.Key key : _packetSetMap.keySet() ) {
                if( _packetSetMap.containsKey( key ) ) {
                    AISPacketSet set = _packetSetMap.get( key );
                    if( set == null || set.isExpired( thresholdMs ) ) {
                        purgeCount++;
                        _packetSetMap.remove( key );
                    }
                }
            }
        }
        
        LOG.fatal( "{} AISPacketSets purged from AISPacketBuffer.  New AISPacketBuffer size is {} elements", 
                purgeCount, _packetSetMap.size() );
        
        return purgeCount;
    }
    
    /**
     * 
     * @return 
     */
    public boolean isClosed() {
        return _closed;
    }

    /**
     *
     */
    @Override
    public void close() {
        LOG.info( "Closing AISPacketBuffer..." );
        if( _packetSetMap != null ) _packetSetMap.clear();
        _closed = true;
    }
}
