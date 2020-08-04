/*
 * Copyright 2016-2019 Jonathan Machen <jonathan.machen@robotaccomplice.com>.
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
import jais.TagBlock;
import jais.Vessel;
import jais.exceptions.AISException;
import jais.exceptions.AISPacketException;
import jais.messages.enums.AISMessageType;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import org.apache.logging.log4j.Level;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Unit test for simple decoding tasks
 */
public class AISDecoderTest {

    // initialize log4j
    private final static Logger LOG = LogManager.getLogger( AISDecoderTest.class );

    private static String [] TEST_PACKETS;
            
    private final static String[] TEST_COMPOUND_MESSAGE = {
        "!AIVDM,2,1,0,B,55MwNGP08bnIMUCWC?84ppEA@F2222222222220O0h;2540Ht00000000000,0*55",
        "!AIVDM,2,2,0,B,00000000000,2*27",
        "!AIVDM,2,1,1,B,55O5v842<<>1L=SSK7<aDhTF222222222222220PB`N;;6GT0B0QC31H0j0D,0*59",
        "!AIVDM,2,2,1,B,liH0CPj8880,2*1A",};

    /**
     * Create the test case
     *
     */
    public AISDecoderTest() { }
    
    /**
     * 
     * @throws URISyntaxException 
     * @throws java.io.IOException 
     */
    @BeforeClass
    public static void setup() throws URISyntaxException, IOException {
        TEST_PACKETS = Files.lines(Paths.get("src/test/resources/ais_packets.txt")).toArray(String[]::new);
    }

    /**
     *
     * @param packets
     */
    private void decodePackets( AISPacket... packets ) throws AISException {

        Optional<AISMessage> msg = AISMessageFactory.create( "UnitTest", packets );

        if( !msg.isPresent() ) {
            LOG.warn( "Factory returned a null message!  May be an unsupported message type." );
        } else {
            // from AISMessageBase
            LOG.debug( "Type     : {}", msg.get().getType() );
            LOG.debug( "Repeat   : {}", msg.get().getRepeat() );
            LOG.debug( "MMSI     : {}", msg.get().getMmsi() );

            switch( msg.get().getType() ) {
                case BASE_STATION_REPORT:
                    BaseStationReport bsr = ( BaseStationReport )msg.get();

                    LOG.debug( "Year     : {}", bsr.getYear() );
                    LOG.debug( "Month    : {}", bsr.getMonth() );
                    LOG.debug( "Day      : {}", bsr.getDay() );
                    LOG.debug( "Hour     : {}", bsr.getHour() );
                    LOG.debug( "Minute   : {}", bsr.getMinute() );
                    LOG.debug( "Second   : {}", bsr.getSecond() );
                    LOG.debug( "EPFD     : {}", bsr.getEpfd() );
                    LOG.debug( "Lat      : {}", bsr.getLat() );
                    LOG.debug( "Lon      : {}", bsr.getLon() );
                    LOG.debug( "Position : {}", bsr.getPosition() );
                    LOG.debug( "Radio    : {}", bsr.getRadio() );
                    break;
                case POSITION_REPORT_CLASS_A:
                case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                    PositionReportBase prb = ( PositionReportBase )msg.get();
                    LOG.debug( "Decoded a PositionReport Message:\n{}", new Vessel( prb ) );
                    break;
                case STATIC_AND_VOYAGE_RELATED_DATA:
                    StaticAndVoyageRelatedData savrd = ( StaticAndVoyageRelatedData )msg.get();
                    LOG.debug( "Decoded a StaticAndVoyageRelatedData Message:\n{}", new Vessel( savrd ) );
                    break;
                case BINARY_ADDRESSED_MESSAGE:
                    BinaryAddressedMessageBase bamb = ( BinaryAddressedMessageBase )msg.get();
                    LOG.debug( "Source MMSI     : {}", bamb.getSourceMmsi() );
                    LOG.debug( "Destination MMSI: {}", bamb.getDestMmsi() );
                    LOG.debug( "Sequence Number : {}", bamb.getSeqno() );
                    LOG.debug( "DAC             : {}", bamb.getDac() );
                    LOG.debug( "FID             : {}", bamb.getFid() );

                    // after we have the dac and fid we can determine the
                    // specific subtype of message we're dealing with and decode
                    // further
                    break;
                case BINARY_ACKNOWLEDGE:
                    BinaryAcknowledge ba = ( BinaryAcknowledge )msg.get();

                    LOG.debug( "Source MMSI: {}", ba.getSourceMmsi() );
                    LOG.debug( "MMSI 1     : {}", ba.getMmsi1() );
                    LOG.debug( "MMSI 2     : {}", ba.getMmsi2() );
                    LOG.debug( "MMSI 3     : {}", ba.getMmsi3() );
                    LOG.debug( "MMSI 4     : {}", ba.getMmsi4() );
                    break;
                case BINARY_BROADCAST_MESSAGE:
                    break;
                case STANDARD_SAR_AIRCRAFT_POSITION_REPORT:
                    break;
                case UTC_AND_DATE_INQUIRY:
                    break;
                case UTC_AND_DATE_RESPONSE:
                    break;
                case ADDRESSED_SAFETY_RELATED_MESSAGE:
                    break;
                case SAFETY_RELATED_ACKNOWLEDGEMENT:
                    break;
                case SAFETY_RELATED_BROADCAST_MESSAGE:
                    break;
                default:
                    LOG.debug( "Ignoring new {}", msg.get().getType().getDescription() );
            }
        }
    }

    /**
     * @throws jais.exceptions.AISPacketException
     */
    @Test
    public void testPacketValidation() throws AISPacketException {
        LOG.info( "*** testPacketValidation()" );
        for( String packetStr : TEST_PACKETS ) {
            LOG.debug( "Validating packet: {}", packetStr );
            String truncStr = AISPacket.truncatePacket( packetStr );
            LOG.debug( "AISPacket.truncatePacket() produced: \"{}\" from \"{}\"", truncStr, packetStr );
            if( truncStr != null && !truncStr.isEmpty() )
                Assert.assertTrue( "Packet string is invalid:\n" + truncStr, new AISPacket( truncStr ).isValid() );
            else {
                AISPacket packet = new AISPacket(packetStr);

                Assert.assertNotNull("AISPacket from String is null: " + packetStr, packet);
                Assert.assertTrue( "Truncated packet is null or empty and original packetStr is invalid: "
                        + packetStr, packet.isValid() );
            }
        }
        LOG.info( "Packet validation test successful!" );
    }

    /**
     * Tests basic AIS decoding
     *
     * @throws AISException
     */
    @Test
    public void testPacketDecoding() throws AISException {
        LOG.info( "*** testPacketDecoding()" );

        LOG.info( "Testing with {} packets.", TEST_PACKETS.length );
        for( String packetStr : TEST_PACKETS ) {
            Matcher pm = AISPacket.PACKET_PATTERN.matcher( packetStr );

            if( pm.find() ) {
                LOG.debug(  "Found {} groups", pm.groupCount() );
                for( int i = 0; i < pm.groupCount(); i++ ) {
                    LOG.debug( "Group {} = \"{}\"", i, pm.group( i ) );
                }
            } else {
                LOG.debug( "MATCHER COMPLETELY FAILED! {}", AISPacket.PACKET_PATTERN );
            }
            
            String truncStr = AISPacket.truncatePacket( packetStr );
            if( truncStr != null && !truncStr.isEmpty() ) packetStr = truncStr;
            
            LOG.debug( "Processing \"{}\"", packetStr );
            AISPacket packet = new AISPacket( packetStr.trim() );
            packet.process();
            if( packet.getTagBlock() != null ) {
                Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( packetStr );
                if( m.find() ) {
                    for( int i = 0; i <= m.groupCount(); i++ ) LOG.debug( "Found: {}", m.group( i ) );
                }
                LOG.info( "\n\n{}", packetStr );
                LOG.info( "TagBlock: {}\n\n", packet.getTagBlock().toString() );
            } else {
                LOG.info( "TAGBLOCK is null" );
            }
            decodePackets( packet );
        }

        LOG.info( "Testing compound message with two parts" );
        AISPacket pOne = new AISPacket( TEST_COMPOUND_MESSAGE[0] );
        AISPacket pTwo = new AISPacket( TEST_COMPOUND_MESSAGE[1] );
        pOne.process();
        pTwo.process();
        AISPacket[] compoundMsg = new AISPacket[]{pOne, pTwo};

        decodePackets( compoundMsg );

        LOG.info( "** Subtest of packet separation logic" );
        LOG.info( "Testing packets that are not newline separated" );
        String packetString = TEST_PACKETS[0] + TEST_PACKETS[1];
        for( String ps : packetString.split( "!AIVD" ) ) {
            if( ps != null && !ps.isEmpty() ) {
                ps = "!AIVDM" + ps.trim();
                LOG.debug( "Found packet to test: {}", ps );
                try {
                    decodePackets( new AISPacket( ps ) );
                } catch( AISException t ) {
                    LOG.info( t.getMessage(), t );
                }
            }
        }

        LOG.info( "Testing packets that ARE newline separated" );
        packetString = TEST_PACKETS[0] + "\n" + TEST_PACKETS[1];
        for( String ps : packetString.split( "!AIVD" ) ) {
            if( ps != null && !ps.trim().isEmpty() ) {
                ps = "!AIVD" + ps;
                LOG.debug( "Found packet to test: {}", ps );
                try {
                    decodePackets( new AISPacket( ps ) );
                } catch( AISException t ) {
                    LOG.info( t.getMessage(), t );
                }
            }
        }

        LOG.info( "AIS message decode test successful!" );
    }

    /**
     * 
     * @throws AISPacketException
     * @throws AISException 
     */
    @Test
    public void testDecodingSpeed() throws AISPacketException, AISException {
        LOG.fatal( "*************************************" );
        LOG.fatal( "*** testDecodingSpeed() ***" );
        LOG.fatal( "*************************************" );
        long start;
        long stop;
        
        AISPacket pOne = new AISPacket( TEST_COMPOUND_MESSAGE[0] );
        AISPacket pTwo = new AISPacket( TEST_COMPOUND_MESSAGE[1] );
        start = System.nanoTime();
        pOne.process();
        pTwo.process();
        stop = System.nanoTime();
        
        LOG.fatal( "============================================================================================" );
        LOG.fatal( "Packet processing for two packets took {} ms", ( stop - start ) / 1000000f );
        LOG.fatal( "============================================================================================" );
        
        AISPacket[] packets = new AISPacket[]{pOne, pTwo};
        start = System.nanoTime();
        AISMessageFactory.create( "UnitTest", packets );
        stop = System.nanoTime();
        
        LOG.fatal( "============================================================================================" );
        LOG.fatal( "Decoding of one compound message took {} ms", ( stop - start ) / 1000000f );
        LOG.fatal( "============================================================================================" );

        long processTotalTime = 0;
        long processPerMsgTime = 0;
        long decodeTotalTime = 0;
        long decodePerMsgTime = 0;
        Map<AISMessageType,Integer> counts = new HashMap<>();

        for( int i = 0; i < 1000; i++ ) {
            for( String packetStr : TEST_PACKETS ) {
                try {
                    AISPacket packet = new AISPacket( packetStr );
                    start = System.nanoTime();
                    packet.process();
                    stop = System.nanoTime();
                    processTotalTime += ( stop - start );
                    processPerMsgTime += ( stop - start ) / TEST_PACKETS.length;
                    
                    start = System.nanoTime();
                    Optional<AISMessage> message = AISMessageFactory.create( "UnitTest", packet );
                    stop = System.nanoTime();
                    decodeTotalTime  += ( stop - start );
                    decodePerMsgTime += ( stop - start ) / TEST_PACKETS.length;
                    if( message.isPresent() && i == 0 ) {
                        AISMessageType type = message.get().getType();
                        int count = ( counts.get( type ) ==  null ) ? 0 : counts.get( type );
                        counts.put( type, ++count );
                    }
                } catch( AISException e ) {
                    // do nothing
                }
            }
            
        }
        
        LOG.fatal( "============================================================================================" );
        LOG.fatal( "Average packet process time across 1000 runs for {} messages    :  {} ms", 
                TEST_PACKETS.length, processTotalTime  / ( 1000f * 1000000f ) );
        LOG.fatal( "Average per packet process time across 1000 runs                 :  {} ms", 
                processPerMsgTime / ( 1000f * 1000000f ) );
        
        LOG.fatal( "Average message decode time across 1000 runs for {} messages    :  {} ms", 
                TEST_PACKETS.length, decodeTotalTime  / ( 1000f * 1000000f ) );
        LOG.fatal( "Average per message decode time across 1000 runs                 :  {} ms", 
                decodePerMsgTime / ( 1000f * 1000000f ) );
        LOG.fatal( "============================================================================================" );

        counts.keySet().forEach( ( type ) -> {
            LOG.printf( Level.FATAL, "%2d x %s", counts.get( type ), type );
        } );
    }

    /**
     *
     */
    @Test
    public void testImoValidation() {
        LOG.info( "*** testImoValidation()" );
        LOG.info( "\tNumber:" );
        Assert.assertTrue( AISMessage.isValidImo( 9074729 ) );
        LOG.info( "\tString:" );
        Assert.assertTrue( AISMessage.isValidImo( "IMO 9074729" ) );
    }

    /**
     * 
     * @throws AISPacketException 
     */
    @Test
    public void testAISPacketGenerationFromBinaryString() throws AISPacketException {
        final String binString = "15P<mB003?L02DPGIfh:F`A<0000";
        LOG.info( "*** testAISPacketGenerationFromBinaryString()" );
        AISPacket p = AISPacket.createFromBinaryString( binString, "TEST" ).process();
        try {
            Assert.assertTrue( binString + " is NOT valid.", p.isValid() );
        } catch( Exception e ) {
            LOG.fatal(e.getMessage(), e);
        }
    }
}
