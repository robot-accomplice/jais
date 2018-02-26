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
package jais.messages;

import jais.AISPacket;
import jais.exceptions.AISException;
import jais.messages.enums.AISMessageType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class AISMessageFactory {

    private final static Logger LOG = LogManager.getLogger( AISMessageFactory.class );
    private final static String DEFAULT_SOURCE = "UNKNOWN";
    
    /**
     * 
     * @param source
     * @param strict
     * @param packets
     * @return
     * @throws AISException 
     */
    public static Optional<AISMessage> create( String source, boolean strict, List<AISPacket> packets ) throws AISException {
        return create( source, strict, AISPacket.DEFAULT_CHARSET, packets );
    }
    
    /**
     * 
     * @param source
     * @param strict
     * @param charset
     * @param packets
     * @return
     * @throws AISException 
     */
    public static Optional<AISMessage> create( String source, boolean strict, Charset charset, List<AISPacket> packets ) throws AISException {
        AISPacket [] packetA = new AISPacket[packets.size()];
        packets.toArray( packetA );
        return create( source, strict, charset, packetA );
    }
    
    /**
     * 
     * @param source
     * @param strict
     * @param packets
     * @return
     * @throws AISException 
     */
    public static Optional<AISMessage> create( String source, boolean strict, AISPacket... packets ) throws AISException {
        return create( source, strict, AISPacket.DEFAULT_CHARSET, packets );
    }
    
    /**
     *
     * @param source
     * @param strict
     * @param charset
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static Optional<AISMessage> create( String source, boolean strict, Charset charset, AISPacket... packets ) throws AISException {
        String compositeMsg = null;

        try {
            if( packets.length < 1 ) throw new AISException( "Packets array is empty!" ); 
            if( LOG.isDebugEnabled() ) LOG.debug( "Decoding message from {} packet(s). Strict is set to {}", packets.length, strict );
            
            byte [] compositeBytes;
            if( packets.length == 1 ) {
                if( ! packets[0].isParsed() ) packets[0].process();
                compositeBytes = packets[0].getBinaryString();
            } else {
                compositeBytes = AISPacket.concatenate( packets );
            }
            
            compositeMsg = AISPacket.bArray2Str( compositeBytes );
            
            if( LOG.isDebugEnabled() ) LOG.debug( "Composite message is: {}", compositeMsg );
            
            // we need the message type in order to invoke the reflective constructor
            Optional<AISMessageType> mType = AISMessageDecoder.decodeMessageType( compositeMsg );

            if( mType.isPresent() ) {
                if( LOG.isDebugEnabled() ) LOG.debug( "Creating a new {} instance.", mType.get().getDescription() );

                AISMessage message = null;
                switch( mType.get() ) {
                    case POSITION_REPORT_CLASS_A:
                        message = new PositionReportClassA( source, packets );
                        break;
                    case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                        message = new PositionReportClassAAssignedSchedule( source, packets );
                        break;
                    case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                        message = new PositionReportClassAResponseToInterrogation( source, packets );
                        break;
                    case BASE_STATION_REPORT:
                        message = new BaseStationReport( source, packets );
                        break;
                    case STATIC_AND_VOYAGE_RELATED_DATA:
                        message = new StaticAndVoyageRelatedData( source, packets );
                        break;
                    case BINARY_ACKNOWLEDGE:
                        message = new BinaryAcknowledge( source, packets );
                        break;
                    case STANDARD_SAR_AIRCRAFT_POSITION_REPORT:
                        message = new StandardSARAircraftPositionReport( source, packets );
                        break;
                    case UTC_AND_DATE_INQUIRY:
                        message = new UTCDateInquiry( source, packets );
                        break;
                    case UTC_AND_DATE_RESPONSE:
                        message = new UTCDateResponse( source, packets );
                        break;
                    case ADDRESSED_SAFETY_RELATED_MESSAGE:
                        message = new AddressedSafetyRelatedMessage( source, packets );
                        break;
                    case SAFETY_RELATED_ACKNOWLEDGEMENT:
                        message = new SafetyRelatedAcknowledgement( source, packets );
                        break;
                    case SAFETY_RELATED_BROADCAST_MESSAGE:
                        message = new SafetyRelatedBroadcastMessage( source, packets );
                        break;
                    case INTERROGATION:
                        message = new Interrogation( source, packets );
                        break;
                    case ASSIGNMENT_MODE_COMMAND:
                        message = new AssignmentModeCommand( source, packets );
                        break;
                    case DGNSS_BROADCAST_BINARY_MESSAGE:
                        message = new DGNSSBroadcastBinaryMessage( source, packets );
                        break;
                    case STANDARD_CLASS_B_CS_POSITION_REPORT:
                        message = new StandardClassBCSPositionReport( source, packets );
                        break;
                    case EXTENDED_CLASS_B_CS_POSITION_REPORT:
                        message = new ExtendedClassBCSPositionReport( source, packets );
                        break;
                    case DATA_LINK_MANAGEMENT_MESSAGE:
                        message = new DataLinkManagementMessage( source, packets );
                        break;
                    case AID_TO_NAVIGATION_REPORT:
                        message = new AidToNavigationReport( source, packets );
                        break;
                    case CHANNEL_MANAGEMENT:
                        message = new ChannelManagement( source, packets );
                        break;
                    case GROUP_ASSIGNMENT_COMMAND:
                        message = new GroupAssignmentCommand( source, packets );
                        break;
                    case STATIC_DATA_REPORT:
                        message = new StaticDataReport( source, packets );
                        break;
                    case SINGLE_SLOT_BINARY_MESSAGE:
                        message = new SingleSlotBinaryMessage( source, packets );
                        break;
                    case MULTIPLE_SLOT_BINARY_MESSAGE:
                        message = new MultipleSlotBinaryMessage( source, packets );
                        break;
                    case POSITION_REPORT_FOR_LONG_RANGE_APPLICATIONS:
                        message = new LongRangeAISBroadcastMessage( source, packets );
                        break;
                    default:
                        LOG.warn( "{} - Unknown or invalid message type ", source );
                        return Optional.empty();
                }

                if( message != null ) {
                    // decode message
                    message.decode( charset );
                }
                
                return Optional.of( message );
            } else {
                throw new AISException( "MessageType is null for message String: " + compositeMsg );
            }
        } catch( AISException t ) {
            // repackage any and all throwables as AISExceptions
            if( strict ) {
                throw new AISException( "Failed to create a valid AISMessage from packet \"" + compositeMsg + "\" : " + t.getMessage(), t );
            } else {
                LOG.warn( "Incomplete AISMessage created from packet \"{}\" : {}", compositeMsg, t.getMessage() );
                if( LOG.isTraceEnabled() ) LOG.trace( "Decode Failure: {}", t.getMessage(), t );
            }
        }

        return Optional.empty();
    }

    /**
     *
     * @param source
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static Optional<AISMessage> create( String source, AISPacket... packets ) throws AISException {
        return create( source, true, packets );
    }

    /**
     *
     * @param source
     * @param strict
     * @param packetStrings
     * @return
     * @throws AISException
     */
    public static Optional<AISMessage> create( String source, boolean strict, String... packetStrings ) throws AISException {
        AISPacket[] packets = new AISPacket[packetStrings.length];

        for( int i = 0; i < packetStrings.length; i++ ) {
            packets[i] = new AISPacket( packetStrings[i], source );
        }

        return create( source, strict, packets );
    }

    /**
     *
     * @param source
     * @param packets
     * @return
     * @throws jais.exceptions.AISException
     */
    public static Optional<AISMessage> create( String source, List<AISPacket> packets ) throws AISException {
        AISPacket[] packetArray = new AISPacket[packets.size()];
        packets.toArray( packetArray );
        return create( source, packetArray );
    }
}
