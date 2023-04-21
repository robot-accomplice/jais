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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jais.AISSentence;
import jais.TagBlock;
import jais.Vessel;
import jais.messages.enums.AISMessageType;
import jais.messages.enums.EPFDFixType;
import jais.messages.enums.ManeuverType;
import jais.messages.enums.NavigationStatus;
import jais.messages.enums.ShipType;

/**
 * Unit test for simple decoding tasks
 */
public class AISDecoderTest {

    // initialize log4j
    private final static Logger LOG = LogManager.getLogger(AISDecoderTest.class);

    private static String[] TEST_SENTENCES;

    private final static String[] TEST_COMPOUND_MESSAGE = {
            "!AIVDM,2,1,0,B,55MwNGP08bnIMUCWC?84ppEA@F2222222222220O0h;2540Ht00000000000,0*55",
            "!AIVDM,2,2,0,B,00000000000,2*27",
            "!AIVDM,2,1,1,B,55O5v842<<>1L=SSK7<aDhTF222222222222220PB`N;;6GT0B0QC31H0j0D,0*59",
            "!AIVDM,2,2,1,B,liH0CPj8880,2*1A", };

    private final static int RUN_COUNT = 100000;

    /**
     * Create the test case
     *
     */
    public AISDecoderTest() {
    }

    /**
     * 
     * @throws java.io.IOException if we are unable to load the test sentences
     */
    @BeforeAll
    public static void setup() throws IOException {
        TEST_SENTENCES = Files.lines(Paths.get("src/test/resources/ais_sentences.txt")).filter(t -> !t.isBlank())
                .toArray(String[]::new);
    }

    /**
     *
     * @param sentences the AIS Sentences we want to decode
     */
    private void decodeSentences(AISSentence... sentences) {

        Optional<AISMessage> msg = AISMessageFactory.create("UnitTest", sentences);

        if (msg.isEmpty()) {
            LOG.warn("Factory returned a null message!  May be an unsupported message type.");
        } else {
            // from AISMessageBase
            LOG.debug("Type     : {}", msg.get().getType());
            LOG.debug("Repeat   : {}", msg.get().getRepeat());
            LOG.debug("MMSI     : {}", msg.get().getMmsi());

            switch (msg.get().getType()) {
                case BASE_STATION_REPORT:
                    BaseStationReport bsr = (BaseStationReport) msg.get();

                    LOG.debug("Year     : {}", bsr.getYear());
                    LOG.debug("Month    : {}", bsr.getMonth());
                    LOG.debug("Day      : {}", bsr.getDay());
                    LOG.debug("Hour     : {}", bsr.getHour());
                    LOG.debug("Minute   : {}", bsr.getMinute());
                    LOG.debug("Second   : {}", bsr.getSecond());
                    LOG.debug("EPFD     : {}", bsr.getEpfd());
                    LOG.debug("Lat      : {}", bsr.getLat());
                    LOG.debug("Lon      : {}", bsr.getLon());
                    LOG.debug("Position : {}", bsr.getPosition());
                    LOG.debug("Radio    : {}", bsr.getRadio());
                    break;
                case POSITION_REPORT_CLASS_A:
                case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                    PositionReportBase prb = (PositionReportBase) msg.get();
                    LOG.debug("Decoded a PositionReport Message:\n{}", new Vessel(prb));
                    break;
                case STATIC_AND_VOYAGE_RELATED_DATA:
                    StaticAndVoyageRelatedData savrd = (StaticAndVoyageRelatedData) msg.get();
                    LOG.debug("Decoded a StaticAndVoyageRelatedData Message:\n{}", new Vessel(savrd));
                    break;
                case BINARY_ADDRESSED_MESSAGE:
                    BinaryAddressedMessageBase bamb = (BinaryAddressedMessageBase) msg.get();
                    LOG.debug("Source MMSI     : {}", bamb.getSourceMmsi());
                    LOG.debug("Destination MMSI: {}", bamb.getDestMmsi());
                    LOG.debug("Sequence Number : {}", bamb.getSeqno());
                    LOG.debug("DAC             : {}", bamb.getDac());
                    LOG.debug("FID             : {}", bamb.getFid());

                    // after we have the dac and fid we can determine the
                    // specific subtype of message we're dealing with and decode
                    // further
                    break;
                case BINARY_ACKNOWLEDGE:
                    BinaryAcknowledge ba = (BinaryAcknowledge) msg.get();

                    LOG.debug("Source MMSI: {}", ba.getSourceMmsi());
                    LOG.debug("MMSI 1     : {}", ba.getMmsi1());
                    LOG.debug("MMSI 2     : {}", ba.getMmsi2());
                    LOG.debug("MMSI 3     : {}", ba.getMmsi3());
                    LOG.debug("MMSI 4     : {}", ba.getMmsi4());
                    break;
                case BINARY_BROADCAST_MESSAGE:
                case STANDARD_SAR_AIRCRAFT_POSITION_REPORT:
                case UTC_AND_DATE_INQUIRY:
                case UTC_AND_DATE_RESPONSE:
                case ADDRESSED_SAFETY_RELATED_MESSAGE:
                case SAFETY_RELATED_ACKNOWLEDGEMENT:
                case SAFETY_RELATED_BROADCAST_MESSAGE:
                    break;
                default:
                    LOG.debug("Ignoring new {}", msg.get().getType().getDescription());
            }
        }
    }

    /**
     */
    @Test
    public void testSentenceValidation() {
        LOG.info("*** testSentenceValidation()");
        for (String sentenceStr : TEST_SENTENCES) {
            if (sentenceStr.isBlank())
                continue;
            LOG.info("Validating sentence: {}", sentenceStr);
            String truncStr = AISSentence.truncateSentence(sentenceStr);
            LOG.info("AISSentence.truncateSentence() produced: \"{}\" from \"{}\"", truncStr, sentenceStr);
            if (truncStr != null && !truncStr.isEmpty())
                Assertions.assertTrue(new AISSentence(truncStr).isValid(), "sentence string is invalid:\n" + truncStr);
            else {
                AISSentence sentence = new AISSentence(sentenceStr);

                Assertions.assertNotNull(sentence, "AISSentence from String is null: " + sentenceStr);
                Assertions.assertTrue(sentence.isValid(),
                        "Truncated sentence is null or empty and original sentenceStr is invalid: "
                                + sentenceStr);
            }
        }
        LOG.info("sentence validation test successful!");
    }

    /**
     * 
     * 
     */
    @Test
    public void testDecodingAccuracy() {
        LOG.info("*** testDecodingAccuracy()");
        final String posStr = "!AIVDM,1,1,,A,15NHl8500pqSdR8A7jnq9oRF0<<P,0*38";
        LOG.info("Testing with sentence: {}", posStr);

        AISSentence sentence = new AISSentence(posStr);
        Optional<AISMessage> msg = AISMessageFactory.create("UnitTest", sentence);
        Assertions.assertNotNull(msg, "Optional for decoded message was null!");
        Assertions.assertTrue(msg.isPresent(), "Optional for decoded message is not present!");
        AISMessage aisMsg = msg.get();
        Assertions.assertEquals(aisMsg.getType(), AISMessageType.POSITION_REPORT_CLASS_A);
        aisMsg.decode();
        PositionReportClassA posDecoded = (PositionReportClassA) aisMsg;
        Assertions.assertEquals(367408160, posDecoded.getMmsi());
        Assertions.assertEquals(29.92249870300293, posDecoded.getLat());
        Assertions.assertEquals(-90.06922149658203, posDecoded.getLon());
        Assertions.assertEquals(234.3000030517578, posDecoded.getCourseOverGround());
        Assertions.assertEquals(241, posDecoded.getHeading());
        Assertions.assertEquals(ManeuverType.NOT_AVAILABLE, posDecoded.getManeuver());
        Assertions.assertEquals(49952, posDecoded.getRadio());
        Assertions.assertEquals(11, posDecoded.getSecond());
        Assertions.assertEquals(5.599999904632568, posDecoded.getSpeed());
        Assertions.assertEquals(NavigationStatus.MOORED, posDecoded.getStatus());

        final String[] statStr = {
                "!AIVDM,2,1,1,B,55O5v842<<>1L=SSK7<aDhTF222222222222220PB`N;;6GT0B0QC31H0j0D,0*59",
                "!AIVDM,2,2,1,B,liH0CPj8880,2*1A"
        };
        LOG.info("Testing with sentences: {}", (Object[]) statStr);
        Optional<AISMessage> staMsg = AISMessageFactory.create("UnitTest", new AISSentence(statStr[0]),
                new AISSentence(statStr[1]));
        Assertions.assertNotNull(staMsg, "Optional for decoded message was null!");
        Assertions.assertTrue(staMsg.isPresent(), "Optional for decoded message is not present!");
        aisMsg = staMsg.get();
        Assertions.assertEquals(aisMsg.getType(), AISMessageType.STATIC_AND_VOYAGE_RELATED_DATA);
        aisMsg.decode();
        StaticAndVoyageRelatedData staDecoded = (StaticAndVoyageRelatedData) aisMsg;
        Assertions.assertEquals(368148000, staDecoded.getMmsi());
        Assertions.assertEquals("WCX8613", staDecoded.getCallsign());
        Assertions.assertEquals("JULIE", staDecoded.getShipName());
        Assertions.assertEquals(ShipType.TOWING_LONG_OR_WIDE, staDecoded.getShipType());
        Assertions.assertEquals(149, staDecoded.getToBow());
        Assertions.assertEquals(30, staDecoded.getToStern());
        Assertions.assertEquals(11, staDecoded.getToPort());
        Assertions.assertEquals(EPFDFixType.GPS, staDecoded.getEpfd());
        Assertions.assertEquals("BELLE CHASSE ANCH", staDecoded.getDestination());
        Assertions.assertTrue(staDecoded.dteReady());
        Assertions.assertEquals(9187552, staDecoded.getImo());
        Assertions.assertEquals(7.199999809265137, staDecoded.getDraught());
        Assertions.assertEquals(9, staDecoded.getMonth());
        Assertions.assertEquals(15, staDecoded.getDay());
        Assertions.assertEquals(4, staDecoded.getHour());
        Assertions.assertEquals(0, staDecoded.getMinute());
    }

    /**
     * Tests basic AIS decoding
     *
     */
    @Test
    public void testSentenceDecoding() {
        LOG.info("*** testSentenceDecoding()");

        LOG.info("Testing with {} sentences.", TEST_SENTENCES.length);
        for (String sentenceStr : TEST_SENTENCES) {
            Matcher pm = AISSentence.SENTENCE_PATTERN.matcher(sentenceStr);

            if (pm.find()) {
                LOG.debug("Found {} groups", pm.groupCount());
                for (int i = 0; i < pm.groupCount(); i++) {
                    LOG.debug("Group {} = \"{}\"", i, pm.group(i));
                }
            } else {
                LOG.debug("MATCHER COMPLETELY FAILED! {}", AISSentence.SENTENCE_PATTERN);
            }

            String truncStr = AISSentence.truncateSentence(sentenceStr);
            if (truncStr != null && !truncStr.isEmpty())
                sentenceStr = truncStr;

            LOG.debug("Processing \"{}\"", sentenceStr);
            AISSentence sentence = new AISSentence(sentenceStr.trim());
            sentence.process();
            if (sentence.getTagBlock() != null) {
                Matcher m = TagBlock.TAGBLOCK_PATTERN.matcher(sentenceStr);
                if (m.find()) {
                    for (int i = 0; i <= m.groupCount(); i++)
                        LOG.debug("Found: {}", m.group(i));
                }
                LOG.info("\n\n{}", sentenceStr);
                LOG.info("TagBlock: {}\n\n", sentence.getTagBlock().toString());
            } else {
                LOG.info("TAGBLOCK is null");
            }
            decodeSentences(sentence);
        }

        LOG.info("Testing compound message with two parts");
        AISSentence pOne = new AISSentence(TEST_COMPOUND_MESSAGE[0]);
        AISSentence pTwo = new AISSentence(TEST_COMPOUND_MESSAGE[1]);
        pOne.process();
        pTwo.process();
        AISSentence[] compoundMsg = new AISSentence[] { pOne, pTwo };

        decodeSentences(compoundMsg);

        LOG.info("** Subtest of sentence separation logic");
        LOG.info("Testing sentences that are not newline separated");
        String sentenceString = TEST_SENTENCES[0] + TEST_SENTENCES[1];
        for (String ps : sentenceString.split("!AIVD")) {
            if (ps != null && !ps.isEmpty()) {
                ps = "!AIVDM" + ps.trim();
                LOG.debug("Found sentence to test: {}", ps);

                decodeSentences(new AISSentence(ps));
            }
        }

        LOG.info("Testing sentences that ARE newline separated");
        sentenceString = TEST_SENTENCES[0] + "\n" + TEST_SENTENCES[1];
        for (String ps : sentenceString.split("!AIVD")) {
            if (ps != null && !ps.trim().isEmpty()) {
                ps = "!AIVD" + ps;
                LOG.debug("Found sentence to test: {}", ps);

                decodeSentences(new AISSentence(ps));
            }
        }

        LOG.info("AIS message decode test successful!");
    }

    /**
     * 
     */
    @Test
    public void testDecodingSpeed() {
        LOG.info("*************************************");
        LOG.info("*** testDecodingSpeed() ***");
        LOG.info("*************************************");
        long start;
        long stop;

        AISSentence pOne = new AISSentence(TEST_COMPOUND_MESSAGE[0]);
        AISSentence pTwo = new AISSentence(TEST_COMPOUND_MESSAGE[1]);
        start = System.nanoTime();
        pOne.process();
        pTwo.process();
        stop = System.nanoTime();

        LOG.info("============================================================================================");
        LOG.info("sentence processing for two sentences took {} ms", (stop - start) / 1000000f);
        LOG.info("============================================================================================");

        AISSentence[] sentences = new AISSentence[] { pOne, pTwo };
        start = System.nanoTime();
        AISMessageFactory.create("UnitTest", sentences);
        stop = System.nanoTime();

        LOG.info("============================================================================================");
        LOG.info("Decoding of one compound message took {} ms", (stop - start) / 1000000f);
        LOG.info("============================================================================================");

        long processTotalTime = 0;
        long processPerMsgTime = 0;
        long decodeTotalTime = 0;
        long decodePerMsgTime = 0;
        Map<AISMessageType, Integer> counts = new HashMap<>();

        for (int i = 0; i < RUN_COUNT; i++) {
            for (String sentenceStr : TEST_SENTENCES) {
                AISSentence sentence = new AISSentence(sentenceStr);
                start = System.nanoTime();
                sentence = sentence.process();
                stop = System.nanoTime();
                processTotalTime += (stop - start);
                processPerMsgTime += (stop - start) / TEST_SENTENCES.length;

                start = System.nanoTime();
                Optional<AISMessage> message;
                message = AISMessageFactory.create("UnitTest", sentence);
                stop = System.nanoTime();
                decodeTotalTime += (stop - start);
                decodePerMsgTime += (stop - start) / TEST_SENTENCES.length;
                if (message.isPresent() && i == 0) {
                    AISMessageType type = message.get().getType();
                    int count = (counts.get(type) == null) ? 0 : counts.get(type);
                    counts.put(type, ++count);
                }
            }
        }

        System.out.println("============================================================================================");
        System.out.printf("Overall average sentence process time across %d runs for %d messages :  %f ms\n",
                RUN_COUNT, TEST_SENTENCES.length, processTotalTime / (1000f * 1000000f));
        System.out.printf("Running average per sentence process time across %d runs             :  %f ms\n",
                RUN_COUNT, processPerMsgTime / (1000f * 1000000f));

        System.out.printf("Overall average message decode time across %d runs for %d messages   :  %f ms\n",
                RUN_COUNT, TEST_SENTENCES.length, decodeTotalTime / (1000f * 1000000f));
        System.out.printf("Running average per message decode time across %d runs               :  %f ms\n",
                RUN_COUNT, decodePerMsgTime / (1000f * 1000000f));
        System.out.println("============================================================================================");

        counts.keySet().forEach((type) -> System.out.printf("%2d x %s\n", counts.get(type), type));
    }

    /**
     *
     */
    @Test
    public void testImoValidation() {
        LOG.info("*** testImoValidation()");
        LOG.info("\tNumber:");
        Assertions.assertTrue(AISMessage.isValidImo(9074729));
        LOG.info("\tString:");
        Assertions.assertTrue(AISMessage.isValidImo("IMO 9074729"));
    }

    /**
     * 
     */
    @Test
    public void testAISSentenceGenerationFromBinaryString() {
        final String binString = "15P<mB003?L02DPGIfh:F`A<0000";
        LOG.info("*** testAISSentenceGenerationFromBinaryString()");
        AISSentence p = AISSentence.createFromBinaryString(binString, "TEST").process();
        try {
            Assertions.assertTrue(p.isValid(), binString + " is NOT valid.");
        } catch (Exception e) {
            LOG.info(e.getMessage(), e);
        }
    }
}
