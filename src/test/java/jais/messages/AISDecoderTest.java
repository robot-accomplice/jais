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
import jais.TagBlock;
import jais.exceptions.AISException;
import jais.exceptions.AISPacketException;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE;
import static jais.messages.enums.AISMessageType.POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION;
import java.util.regex.Matcher;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

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
            LOG.info( "Type     : {}", amb.getType() );
            LOG.info( "Repeat   : {}", amb.getRepeat() );
            LOG.info( "MMSI     : {}", amb.getMmsi() );

            switch( amb.getType() ) {
                case BASE_STATION_REPORT:
                    BaseStationReport bsr = ( BaseStationReport ) amb;

                    LOG.info( "Year     : {}", bsr.getYear() );
                    LOG.info( "Month    : {}", bsr.getMonth() );
                    LOG.info( "Day      : {}", bsr.getDay() );
                    LOG.info( "Hour     : {}", bsr.getHour() );
                    LOG.info( "Minute   : {}", bsr.getMinute() );
                    LOG.info( "Second   : {}", bsr.getSecond() );
                    LOG.info( "EPFD     : {}", bsr.getEpfd() );
                    LOG.info( "Lat      : {}", bsr.getLat() );
                    LOG.info( "Lon      : {}", bsr.getLon() );
                    LOG.info( "Position : {}", bsr.getPosition() );
                    LOG.info( "Radio    : {}", bsr.getRadio() );
                    break;
                case POSITION_REPORT_CLASS_A:
                case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                    PositionReportBase prb = ( PositionReportBase ) amb;

                    // from PositionReportBase
                    LOG.info( "Accuracy : {}", prb.isAccurate() );
                    LOG.info( "Course   : {}", prb.getCourse() );
                    LOG.info( "Heading  : {}", prb.getHeading() );
                    LOG.info( "Latitude : {}", prb.getLat() );
                    LOG.info( "Longitude: {}", prb.getLon() );
                    LOG.info( "Maneuver : {}", prb.getManeuver() );
                    LOG.info( "Position : {}", prb.getPosition() );
                    LOG.info( "Radio    : {}", prb.getRadio() );
                    LOG.info( "RAIM     : {}", prb.isRaim() );
                    LOG.info( "Second   : {}", prb.getSecond() );
                    LOG.info( "Speed    : {}", prb.getSpeed() );
                    LOG.info( "Status   : {}", prb.getStatus() );
                    LOG.info( "Turn     : {}", prb.getTurn() );
                    break;
                case STATIC_AND_VOYAGE_RELATED_DATA:
                    StaticAndVoyageRelatedData savrd
                            = ( StaticAndVoyageRelatedData ) amb;

                    LOG.info( "AIS Version  : {}", savrd.getVersion() );
                    LOG.info( "IMO Number   : {}", savrd.getImo() );
                    LOG.info( "Call Sign    : {}", savrd.getCallsign() );
                    LOG.info( "Ship Name    : {}", savrd.getShipname() );
                    LOG.info( "Ship Type    : {}", savrd.getShiptype() );
                    LOG.info( "To Bow       : {}", savrd.getToBow() );
                    LOG.info( "To Stern     : {}", savrd.getToStern() );
                    LOG.info( "To Port      : {}", savrd.getToPort() );
                    LOG.info( "To Starboard : {}", savrd.getToStarboard() );
                    LOG.info( "EPFD Fix Type: {}", savrd.getEpfd() );
                    LOG.info( "ETA Month    : {}", savrd.getMonth() );
                    LOG.info( "ETA Day      : {}", savrd.getDay() );
                    LOG.info( "ETA Hour     : {}", savrd.getHour() );
                    LOG.info( "ETA Minute   : {}", savrd.getMinute() );
                    LOG.info( "Draught      : {}", savrd.getDraught() );
                    LOG.info( "Destination  : {}", savrd.getDestination() );
                    LOG.info( "DTE Ready    : {}", savrd.dteReady() );
                    break;
                case BINARY_ADDRESSED_MESSAGE:
                    BinaryAddressedMessageBase bamb
                            = ( BinaryAddressedMessageBase ) amb;
                    LOG.info( "Source MMSI     : {}", bamb.getSourceMmsi() );
                    LOG.info( "Destination MMSI: {}", bamb.getDestMmsi() );
                    LOG.info( "Sequence Number : {}", bamb.getSeqno() );
                    LOG.info( "DAC             : {}", bamb.getDac() );
                    LOG.info( "FID             : {}", bamb.getFid() );

                    // after we have the dac and fid we can determine the
                    // specific subtype of message we're dealing with and decode
                    // further
                    break;
                case BINARY_ACKNOWLEDGE:
                    BinaryAcknowledge ba = ( BinaryAcknowledge ) amb;

                    LOG.info( "Source MMSI: {}", ba.getSourceMmsi() );
                    LOG.info( "MMSI 1     : {}", ba.getMmsi1() );
                    LOG.info( "MMSI 2     : {}", ba.getMmsi2() );
                    LOG.info( "MMSI 3     : {}", ba.getMmsi3() );
                    LOG.info( "MMSI 4     : {}", ba.getMmsi4() );
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
                    LOG.info( "Ignoring new {}", amb.getType().getDescription() );
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
            LOG.info( "Validating packet: {}", packetStr );
            try {
                String truncStr = AISPacket.truncatePacket( new StringBuilder( packetStr ) );
                LOG.info( "AISPacket.truncatePacket() produced: \"{}\" from \"{}\"", truncStr, packetStr );
                if( truncStr != null && !truncStr.isEmpty() ) Assert.assertTrue( new AISPacket( truncStr ).isValid() );
                else Assert.assertTrue( new AISPacket( packetStr ).isValid() );
            } catch( AISPacketException t ) {
                LOG.info( t.getMessage(), t );
            }
        }
        LOG.info( "Packet validation test successful!" );
    }

    /**
     * Tests basic AIS decoding
     *
     * @throws jais.exceptions.AISException
     */
    @Test
    public void testPacketDecoding() throws AISException {
        LOG.info( "*** testPacketDecoding()" );

        LOG.info( "Testing with {} packets.", TEST_PACKETS.length );
        for( String packetStr : TEST_PACKETS ) {
            Matcher pm = AISPacket.PACKET_PATTERN.matcher( packetStr );

            if( pm.find() ) {
                LOG.info(  "Found {} groups", pm.groupCount() );
                for( int i = 0; i < pm.groupCount(); i++ ) {
                    LOG.info( "Group {} = \"{}\"", i, pm.group( i ) );
                }
            } else {
                LOG.info( "MATCHER COMPLETELY FAILED! {}", AISPacket.PACKET_PATTERN );
            }
            
            String truncStr = AISPacket.truncatePacket( new StringBuilder( packetStr ) );
            if( truncStr != null && !truncStr.isEmpty() ) packetStr = truncStr;
            
            AISPacket packet = new AISPacket( packetStr.trim() );
            packet.process();
            if( packet.getTagBlock() != null ) {
                Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher( packetStr );
                if( m.find() ) {
                    for( int i = 0; i <= m.groupCount(); i++ ) {
                        LOG.info( "Found: {}", m.group(i) );
                    }
                }
                LOG.info( "\n\n{}", packetStr );
                LOG.info( "TagBlock: {}\n\n", packet.getTagBlock().toString() );
                // assert( false );
            } else {
                LOG.info( "TAGBLOCK is null" );
            }
            processPackets( packet );
        }

        LOG.info( "Testing compound message with two parts" );
        AISPacket pOne = new AISPacket( TEST_COMPOUND_MESSAGE[0] );
        AISPacket pTwo = new AISPacket( TEST_COMPOUND_MESSAGE[1] );
        pOne.process();
        pTwo.process();
        AISPacket[] compoundMsg = new AISPacket[]{pOne, pTwo};

        processPackets( compoundMsg );

        LOG.info( "** Subtest of packet separation logic" );
        LOG.info( "Testing packets that are not newline separated" );
        String packetString = TEST_PACKETS[0] + TEST_PACKETS[1];
        for( String ps : packetString.split( "!AIVD" ) ) {
            if( ps != null && !ps.isEmpty() ) {
                ps = "!AIVDM" + ps.trim();
                LOG.info( "Found packet to test: {}", ps );
                try {
                    processPackets( new AISPacket( ps ) );
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
                LOG.info( "Found packet to test: {}", ps );
                try {
                    processPackets( new AISPacket( ps ) );
                } catch( AISException t ) {
                    LOG.info( t.getMessage(), t );
                }
            }
        }

        LOG.info( "AIS message decode test successful!" );
    }

    /**
     *
     */
    @Test
    public void testImoValidation() {
        LOG.info( "*** testImoValidation()" );
        LOG.info( "\tNumber:" );
        Assert.assertTrue( AISMessageBase.isValidImo( 9074729 ) );
        LOG.info( "\tString:" );
        Assert.assertTrue( AISMessageBase.isValidImo( "IMO 9074729" ) );
    }
}
