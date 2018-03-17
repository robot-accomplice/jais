/*
 * Copyright 2018 jmachen.
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author jmachen
 */
public class AISPacketSet {
    
    private final static Logger LOG = LogManager.getLogger( AISPacketSet.class );

    private final ZonedDateTime _timestamp = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
    private final AISPacket[] _packets;
    private final Key _key;
    private final int _maxPacketAge;

    /**
     *
     * @param packet
     * @param maxPacketAge
     */
    public AISPacketSet( AISPacket packet, int maxPacketAge ) {
        this( packet, maxPacketAge, new Key( packet.getSource(), packet.getSequentialMessageId(), packet.getFragmentCount() ) );
    }
    
    /**
     * 
     * @param packet
     * @param maxPacketAge
     * @param key 
     */
    public AISPacketSet( AISPacket packet, int maxPacketAge, Key key ) {
        _packets = new AISPacket[packet.getFragmentCount()];
        _packets[0] = packet;
        _maxPacketAge = maxPacketAge;
        _key = key;
    }

    /**
     * 
     * @return 
     */
    public Key getKey() {
        return _key;
    }
    
    /**
     *
     * @return
     */
    public int getSequenceNumber() {
        return _key.getSequenceNumber();
    }

    /**
     *
     * @return
     */
    public int getFragmentCount() {
        return _key.getFragmentCount();
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
        return ( now.toInstant().toEpochMilli() - _timestamp.toInstant().toEpochMilli() );
    }

    /**
     *
     * @param expiredThresholdMs
     * @return
     */
    public boolean isExpired(long expiredThresholdMs) {
        return ( getAgeInMilliseconds() > _maxPacketAge );
    }

    /**
     *
     * @return
     */
    public boolean isExpired() {
        return isExpired(_maxPacketAge);
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
        return AISPacket.bArray2Str( _key.getSource() );
    }

    /**
     *
     * @param packet
     */
    public synchronized void add(AISPacket packet) {
        // make sure this fragment number doesn't exceed the size of the array
        if( packet.getFragmentNumber() <= _key.getFragmentCount() ) {
            _packets[packet.getFragmentNumber() - 1] = packet; // packet fragments are numbered starting at 1
        }
    }

    /**
     *
     * @return
     */
    public boolean isComplete() {
        boolean complete = true;

        if( _packets == null || _packets[0] == null ) {
            if( LOG.isDebugEnabled() ) {
                LOG.debug( "Packet set is empty." );
            }
            complete = false;
        } else if( _packets[0].getFragmentCount() > _packets.length ) {
            if( LOG.isDebugEnabled() ) {
                LOG.debug( "Fragment count {} > {}", _packets[0].getFragmentCount(), _packets.length );
            }

            // check age of first packet to see if it's time to retire the set
            ZonedDateTime now = ZonedDateTime.now( ZoneOffset.UTC.normalized() );
            if( _packets[0].getTimeReceived().isAfter( now.plus(_maxPacketAge, ChronoUnit.MILLIS ) ) ) {
                complete = false;
            } else if( LOG.isDebugEnabled() ) {
                LOG.debug( "Packetset has timed out" );
            }
        }

        return complete;
    }

    /**
     * 
     */
    public static class Key {
        
        private final int _sequenceNumber;
        private final int _fragmentCount;
        private final byte[] _source;
        
        /**
         * 
         * @param source
         * @param sequenceNumber
         * @param fragmentCount 
         */
        Key( byte [] source, int sequenceNumber, int fragmentCount ) {
            _source = source;
            _sequenceNumber = sequenceNumber;
            _fragmentCount = fragmentCount;
        }
        
        /**
         * 
         * @return 
         */
        public byte [] getSource() {
            return _source;
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
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + this._sequenceNumber;
            hash = 17 * hash + Arrays.hashCode(_source);

            return hash;
        }
    
        /**
         *
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if( o == null ) {
                return false;
            }
            if( !( o instanceof AISPacketSet.Key ) ) {
                return false;
            }

            AISPacketSet.Key other = ( AISPacketSet.Key ) o;
            if( other._sequenceNumber != _sequenceNumber ) {
                return false;
            }

            return Arrays.equals( _source, other._source );
        }
        
        /**
         * 
         * @return 
         */
        @Override
        public String toString() {
            return toString( _source, _sequenceNumber, _fragmentCount );
        }
        
        /**
         * 
         * @param source
         * @param sequenceNumber
         * @param fragmentCount
         * @return 
         */
        public static String toString( byte [] source, int sequenceNumber, int fragmentCount ) {
            return AISPacket.bArray2Str( source ) + sequenceNumber + "_" + fragmentCount;
        }
    }
}
