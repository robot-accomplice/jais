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

package jais.messages;

import jais.AISPacket;
import jais.TagBlock;
import jais.exceptions.AISException;
import jais.exceptions.AISPacketException;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION;
import java.util.regex.Matcher;
import org.junit.Test;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import static junit.framework.Assert.assertTrue;

/**
 * Unit test for simple decoding tasks
 */
public class AISDecoderTest {

    // initialize log4j
    private final static Logger LOG = LogManager.getLogger( AISDecoderTest.class );

    private final static String[] TEST_PACKETS = {
        "!AIVDM,1,1,,B,15N9W:0P00ISR5hA7<A8:OvT0498,0*2F",
        "!AIVDM,1,1,,A,15N09TPP1BISMQhA7B2D:?vR00SL,0*50",
        "!AIVDM,1,1,,B,18UvCJ0000ISJpPA7OO4t9HR0h:i,0*68",
        "!AIVDM,1,1,,A,369AaiU000IS`PTA7WpbnGdT0000,0*62",
        "!AIVDM,1,1,,B,15Mu6kPP00ISfutA7o>De?vV0@;V,0*3D",
        "!AIVDM,1,1,,B,Dh3OwhQTUN>5NTfGMMhNfqMd200,2*6C",
        "!AIVDM,1,1,,A,Dh3OwhQbaN>5N@fGMMhNfqMP200,2*4D",
        "!AIVDM,1,1,,A,4h3OwhQunE1BDqSkhdA8<gw02D7L,0*0C",
        "!AIVDM,1,1,,B,4h3OwhQunE1BDqSkhdA8<gw02D7L,0*0F",
        "!AIVDM,1,1,,A,18156I@01TISOQ4A7D5:e8RV0D3R,0*00",
        "!AIVDM,1,1,,B,35N2v2gP00qSRPTA7<WJ?gvd27?3,0*4C",
        "!AIVDM,1,1,,A,15N0H40P10ISGC:A8Uh=j?w600Sq,0*2A",
        "!AIVDM,1,1,,B,15Mk`tPP00ISG<nA8SFFewwd00S2,0*56",
        "!AIVDM,1,1,,B,15NUKbP000qScUVA7U37JQsh0t5j,0*0D",
        "!AIVDM,1,1,,A,15N6nr0P00ISa=RA74c00?wf0@Q6,0*2A",
        "!AIVDM,1,1,,A,15NN`o?P00qSPo<A7:h`TOwj2<5g,0*7D",
        "!AIVDM,1,1,,A,15MvWGgP01ISWe>A7TqWaOwl20Rk,0*23",
        "!AIVDM,1,1,,A,15MmOb?P1IIS`HnA7T1b6wwl2@Qe,0*09",
        "!AIVDM,1,1,,B,1532Ov001TqSPNFA7C5Jhpcj05pD,0*6C",
        "!AIVDM,1,1,,A,15MlJePP00IS`s6A79KFUwwl08R>,0*0C",
        "!AIVDM,1,1,,A,15NLp8?P00qS`=RA7D?GkOv020Rs,0*21",
        "!AIVDM,1,1,,A,15NaBg?P0OISRLlA7>g9qwwn00Rm,0*66",
        "!AIVDM,1,1,,A,D03OwhP6mN>40lfGL00Nfr<T200,2*4D",
        "!AIVDM,1,1,,B,D03OwhP<qN>40PfGL00Nfr<H200,2*78",
        "!AIVDM,1,1,,B,403OwhQuvGg60qSkhLA8<aO02@0<,0*7E",
        "!AIVDM,1,1,,A,15N7H2?P00IS`CHA7C3H:Ov00@0Q,0*53",
        "!AIVDM,1,1,,B,15MTbUgP00ISbCbA7e5RPgwl00RN,0*25",
        "!AIVDM,1,1,,A,15NLp5PP00IS`?DA7D=VdOv205pH,0*6F",
        "!AIVDM,1,1,,B,15MuH>PP00ISRgfA7K9G5gv00811,0*79",
        "!AIVDM,1,1,,B,15N0Ef?P00ISS8dA7KabPOv420Ru,0*40",
        "!AIVDM,1,1,,A,15NLp0PP00qSg7rA7p6`e?v62D5O,0*26",
        "!AIVDM,1,1,,A,15Mu5`P015qSTd6A7J2af86>0L<P,0*26",
        "!AIVDM,1,1,,B,15NVNMPP07IS`eBA7W40dOv40H1Q,0*34",
        "!AIVDM,1,1,,A,15MrOCPP00ISaDtA7aHq@wv60H1o,0*25",
        "!AIVDM,1,1,,A,15NKjSP000qSN64A7:rnr`t80H2C,0*17",
        "!AIVDM,1,1,,A,15Mk`tPP00ISG=8A8SHVvwv805pH,0*0E",
        "!AIVDM,1,1,,A,15NUKbP000qScUTA7U36SQr:0d5j,0*57",
        "!AIVDM,1,1,,B,15MvWGgP01ISWe@A7TrWbwv<2<5I,0*7E",
        "!AIVDM,1,1,,B,15N6nr0P00ISa=RA74c00?v:0<40,0*6B",
        "!AIVDM,1,1,,A,1532Ov001TqSPEHA7C;bjHb<0@4:,0*6E",
        "!AIVDM,1,1,,B,15MmOb?P1JIS`?vA7Sob>Ov@2@4R,0*70",
        "!AIVDM,1,1,,B,15NLp8?P00qS`=PA7D@7gwvB20SQ,0*4A",
        "!AIVDM,1,1,,A,15Ms5N0P01ISRWPA7JvGQOv>00RU,0*15",
        "!AIVDM,1,1,,B,15NaBg?P0OISRIlA7>a:1OvB0<3J,0*7A",
        "!AIVDM,1,1,,B,15MtsB0000qSa<:A6wfnJ56>0<38,0*14",
        "!AIVDM,1,1,,B,15N7H2?P00IS`C6A7C5GTwvB085n,0*41",
        "!AIVDM,1,1,,B,15NLp5PP00IS`?bA7DAFj?vD0@6=,0*68",
        "!AIVDM,1,1,,A,15N0Ef?P00ISS8bA7KaJIOvF2<59,0*21",
        "!AIVDM,1,1,,A,15MublPP00ISa:DA6wdUfOvD0L41,0*42",
        "!AIVDM,1,1,,A,15MTbUgP00ISbCjA7e6P0?vB085K,0*52",
        "!AIVDM,1,1,,A,15MuH>PP00ISRgfA7K97`gvD0@6W,0*32",
        "!AIVDM,1,1,,A,15NHl8500pqSdR8A7jnq9oRF0<<P,0*38",
        "!ABVDM,1,1,,A,34Ses01Oh8Jm?Ll;ERH<b1Gd0000,0*61",
        "!AIVDM,1,1,,A,15NHl8500pqSdR8A7jnq9oRF0<<P,0*38!ABVDM,1,1,,A,34Ses01Oh8Jm?Ll;ERH<b1Gd0000,0*61",
        "\\g:1-2-73874,n:157036,s:r003669945,c:1241544035*4A\\!AIVDM,1,1,,B,15N4cJ`005Jrek0H@9n`DW5608EP,0*13"
    };

    private final static String[] TEST_COMPOUND_MESSAGE = {
        "!AIVDM,2,1,0,B,55MwNGP08bnIMUCWC?84ppEA@F2222222222220O0h;2540Ht00000000000,0*55",
        "!AIVDM,2,2,0,B,00000000000,2*27",
        "!AIVDM,2,1,1,B,55O5v842<<>1L=SSK7<aDhTF222222222222220PB`N;;6GT0B0QC31H0j0D,0*59",
        "!AIVDM,2,2,1,B,liH0CPj8880,2*1A",};

    /**
     * Create the test case
     *
     */
    public AISDecoderTest() {
    }

    /**
     *
     * @param packets
     */
    private void processPackets( AISPacket... packets ) throws AISException {

        AISMessageBase amb = ( AISMessageBase ) AISMessageFactory.create( "UnitTest", packets );

        if( amb == null ) {
            LOG.warn( "Factory returned a null message!  May be an unsupported message type." );
        } else {
            // from AISMessageBase
            LOG.fatal( "Type     : {}", amb.getType() );
            LOG.fatal( "Repeat   : {}", amb.getRepeat() );
            LOG.fatal( "MMSI     : {}", amb.getMmsi() );

            switch( amb.getType() ) {
                case BASE_STATION_REPORT:
                    BaseStationReport bsr = ( BaseStationReport ) amb;

                    LOG.fatal( "Year     : {}", bsr.getYear() );
                    LOG.fatal( "Month    : {}", bsr.getMonth() );
                    LOG.fatal( "Day      : {}", bsr.getDay() );
                    LOG.fatal( "Hour     : {}", bsr.getHour() );
                    LOG.fatal( "Minute   : {}", bsr.getMinute() );
                    LOG.fatal( "Second   : {}", bsr.getSecond() );
                    LOG.fatal( "EPFD     : {}", bsr.getEpfd() );
                    LOG.fatal( "Lat      : {}", bsr.getLat() );
                    LOG.fatal( "Lon      : {}", bsr.getLon() );
                    LOG.fatal( "Position : {}", bsr.getPosition() );
                    LOG.fatal( "Radio    : {}", bsr.getRadio() );
                    break;
                case POSITION_REPORT_CLASS_A:
                case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                    PositionReportBase prb = ( PositionReportBase ) amb;

                    // from PositionReportBase
                    LOG.fatal( "Accuracy : {}", prb.isAccurate() );
                    LOG.fatal( "Course   : {}", prb.getCourse() );
                    LOG.fatal( "Heading  : {}", prb.getHeading() );
                    LOG.fatal( "Latitude : {}", prb.getLat() );
                    LOG.fatal( "Longitude: {}", prb.getLon() );
                    LOG.fatal( "Maneuver : {}", prb.getManeuver() );
                    LOG.fatal( "Position : {}", prb.getPosition() );
                    LOG.fatal( "Radio    : {}", prb.getRadio() );
                    LOG.fatal( "RAIM     : {}", prb.isRaim() );
                    LOG.fatal( "Second   : {}", prb.getSecond() );
                    LOG.fatal( "Speed    : {}", prb.getSpeed() );
                    LOG.fatal( "Status   : {}", prb.getStatus() );
                    LOG.fatal( "Turn     : {}", prb.getTurn() );
                    break;
                case STATIC_AND_VOYAGE_RELATED_DATA:
                    StaticAndVoyageRelatedData savrd
                            = ( StaticAndVoyageRelatedData ) amb;

                    LOG.fatal( "AIS Version  : {}", savrd.getVersion() );
                    LOG.fatal( "IMO Number   : {}", savrd.getImo() );
                    LOG.fatal( "Call Sign    : {}", savrd.getCallsign() );
                    LOG.fatal( "Ship Name    : {}", savrd.getShipname() );
                    LOG.fatal( "Ship Type    : {}", savrd.getShiptype() );
                    LOG.fatal( "To Bow       : {}", savrd.getToBow() );
                    LOG.fatal( "To Stern     : {}", savrd.getToStern() );
                    LOG.fatal( "To Port      : {}", savrd.getToPort() );
                    LOG.fatal( "To Starboard : {}", savrd.getToStarboard() );
                    LOG.fatal( "EPFD Fix Type: {}", savrd.getEpfd() );
                    LOG.fatal( "ETA Month    : {}", savrd.getMonth() );
                    LOG.fatal( "ETA Day      : {}", savrd.getDay() );
                    LOG.fatal( "ETA Hour     : {}", savrd.getHour() );
                    LOG.fatal( "ETA Minute   : {}", savrd.getMinute() );
                    LOG.fatal( "Draught      : {}", savrd.getDraught() );
                    LOG.fatal( "Destination  : {}", savrd.getDestination() );
                    LOG.fatal( "DTE Ready    : {}", savrd.dteReady() );
                    break;
                case BINARY_ADDRESSED_MESSAGE:
                    BinaryAddressedMessageBase bamb
                            = ( BinaryAddressedMessageBase ) amb;
                    LOG.fatal( "Source MMSI     : {}", bamb.getSourceMmsi() );
                    LOG.fatal( "Destination MMSI: {}", bamb.getDestMmsi() );
                    LOG.fatal( "Sequence Number : {}", bamb.getSeqno() );
                    LOG.fatal( "DAC             : {}", bamb.getDac() );
                    LOG.fatal( "FID             : {}", bamb.getFid() );

                    // after we have the dac and fid we can determine the
                    // specific subtype of message we're dealing with and decode
                    // further
                    break;
                case BINARY_ACKNOWLEDGE:
                    BinaryAcknowledge ba = ( BinaryAcknowledge ) amb;

                    LOG.fatal( "Source MMSI: {}", ba.getSourceMmsi() );
                    LOG.fatal( "MMSI 1     : {}", ba.getMmsi1() );
                    LOG.fatal( "MMSI 2     : {}", ba.getMmsi2() );
                    LOG.fatal( "MMSI 3     : {}", ba.getMmsi3() );
                    LOG.fatal( "MMSI 4     : {}", ba.getMmsi4() );
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
                    LOG.fatal( "Ignoring new {}", amb.getType().getDescription() );
            }
        }
    }

    /**
     * @throws jais.exceptions.AISPacketException
     */
    @Test
    public void testPacketValidation() throws AISPacketException {
        LOG.fatal( "*** testPacketValidation()" );
        for( String packetStr : TEST_PACKETS ) {
            LOG.fatal( "Validating packet: {}", packetStr );
            try {
                String truncStr = AISPacket.truncatePacket( new StringBuilder( packetStr ) );
                LOG.fatal( "AISPacket.truncatePacket() produced: \"{}\" from \"{}\"", truncStr, packetStr );
                if( truncStr != null && !truncStr.isEmpty() ) assertTrue( new AISPacket( truncStr ).isValid() );
                else assertTrue( new AISPacket( packetStr ).isValid() );
            } catch( AISPacketException t ) {
                LOG.fatal( t.getMessage(), t );
            }
        }
        LOG.fatal( "Packet validation test successful!" );
    }

    /**
     * Tests basic AIS decoding
     *
     * @throws jais.exceptions.AISException
     */
    @Test
    public void testPacketDecoding() throws AISException {
        LOG.fatal( "*** testPacketDecoding()" );

        LOG.fatal( "Testing with {} packets.", TEST_PACKETS.length );
        for( String newMessage : TEST_PACKETS ) {
            Matcher pm = AISPacket.PACKET_PATTERN.matcher( newMessage );

            if( pm.find() ) {
                LOG.fatal(  "Found {} groups", pm.groupCount() );
                for( int i = 0; i < pm.groupCount(); i++ ) {
                    LOG.fatal( "Group {} = \"{}\"", i, pm.group( i ) );
                }
            } else {
                LOG.fatal( "MATCHER COMPLETELY FAILED! {}", AISPacket.PACKET_PATTERN );
            }
            
            String truncStr = AISPacket.truncatePacket( new StringBuilder( newMessage ) );
            if( truncStr != null && !truncStr.isEmpty() ) newMessage = truncStr;
            
            AISPacket packet = new AISPacket( newMessage.trim() );
            packet.process();
            if( packet.getTagBlock() != null ) {
                Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( newMessage );
                if( m.find() ) {
                    for( int i = 0; i <= m.groupCount(); i++ ) {
                        LOG.fatal( "Found: {}", m.group(i) );
                    }
                }
                LOG.fatal( "\n\n{}", newMessage );
                LOG.fatal( "TagBlock: {}\n\n", packet.getTagBlock().toString() );
                // assert( false );
            } else {
                LOG.fatal( "TAGBLOCK is null" );
            }
            processPackets( packet );
        }

        LOG.fatal( "Testing compound message with two parts" );
        AISPacket pOne = new AISPacket( TEST_COMPOUND_MESSAGE[0] );
        AISPacket pTwo = new AISPacket( TEST_COMPOUND_MESSAGE[1] );
        pOne.process();
        pTwo.process();
        AISPacket[] compoundMsg = new AISPacket[]{pOne, pTwo};

        processPackets( compoundMsg );

        LOG.fatal( "** Subtest of packet separation logic" );
        LOG.fatal( "Testing packets that are not newline separated" );
        String packetString = TEST_PACKETS[0] + TEST_PACKETS[1];
        for( String ps : packetString.split( "!AIVD" ) ) {
            if( ps != null && !ps.isEmpty() ) {
                ps = "!AIVDM" + ps.trim();
                LOG.fatal( "Found packet to test: {}", ps );
                try {
                    processPackets( new AISPacket( ps ) );
                } catch( AISException t ) {
                    LOG.fatal( t.getMessage(), t );
                }
            }
        }

        LOG.fatal( "Testing packets that ARE newline separated" );
        packetString = TEST_PACKETS[0] + "\n" + TEST_PACKETS[1];
        for( String ps : packetString.split( "!AIVD" ) ) {
            if( ps != null && !ps.trim().isEmpty() ) {
                ps = "!AIVD" + ps;
                LOG.fatal( "Found packet to test: {}", ps );
                try {
                    processPackets( new AISPacket( ps ) );
                } catch( AISException t ) {
                    LOG.fatal( t.getMessage(), t );
                }
            }
        }

        LOG.fatal( "AIS message decode test successful!" );
    }

    /**
     *
     */
    @Test
    public void testImoValidation() {
        LOG.fatal( "*** testImoValidation()" );
        LOG.fatal( "\tNumber:" );
        assertTrue( AISMessageBase.isValidImo( 9074729 ) );
        LOG.fatal( "\tString:" );
        assertTrue( AISMessageBase.isValidImo( "IMO 9074729" ) );
    }
}
