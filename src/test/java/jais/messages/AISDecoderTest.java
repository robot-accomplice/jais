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

import jais.AISSentence;
import jais.TagBlock;
import jais.Vessel;
import jais.exceptions.AISException;
import jais.exceptions.AISSentenceException;
import jais.messages.enums.AISMessageType;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

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
     * @throws URISyntaxException
     * @throws java.io.IOException
     */
    @BeforeAll
    public static void setup() throws URISyntaxException, IOException {
        TEST_SENTENCES = Files.lines(Paths.get("src/test/resources/ais_sentences.txt")).toArray(String[]::new);
    }

    /**
     *
     * @param sentences
     */
    private void decodesentences(AISSentence... sentences) throws AISException {

        Optional<AISMessage> msg = AISMessageFactory.create("UnitTest", sentences);

        if (!msg.isPresent()) {
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
                    LOG.debug("Ignoring new {}", msg.get().getType().getDescription());
            }
        }
    }

    /**
     * @throws jais.exceptions.AISSentenceException
     */
    @Test
    public void testsentenceValidation() throws AISSentenceException {
        LOG.info("*** testsentenceValidation()");
        for (String sentenceStr : TEST_SENTENCES) {
            LOG.debug("Validating sentence: {}", sentenceStr);
            String truncStr = AISSentence.truncateSentence(sentenceStr);
            LOG.debug("AISsentence.truncatesentence() produced: \"{}\" from \"{}\"", truncStr, sentenceStr);
            if (truncStr != null && !truncStr.isEmpty())
                Assertions.assertTrue(new AISSentence(truncStr).isValid(), "sentence string is invalid:\n" + truncStr);
            else {
                AISSentence sentence = new AISSentence(sentenceStr);

                Assertions.assertNotNull(sentence, "AISsentence from String is null: " + sentenceStr);
                Assertions.assertTrue(sentence.isValid(),
                        "Truncated sentence is null or empty and original sentenceStr is invalid: "
                                + sentenceStr);
            }
        }
        LOG.info("sentence validation test successful!");
    }

    /**
     * Tests basic AIS decoding
     *
     * @throws AISException
     */
    @Test
    public void testsentenceDecoding() throws AISException {
        LOG.info("*** testsentenceDecoding()");

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
            decodesentences(sentence);
        }

        LOG.info("Testing compound message with two parts");
        AISSentence pOne = new AISSentence(TEST_COMPOUND_MESSAGE[0]);
        AISSentence pTwo = new AISSentence(TEST_COMPOUND_MESSAGE[1]);
        pOne.process();
        pTwo.process();
        AISSentence[] compoundMsg = new AISSentence[] { pOne, pTwo };

        decodesentences(compoundMsg);

        LOG.info("** Subtest of sentence separation logic");
        LOG.info("Testing sentences that are not newline separated");
        String sentenceString = TEST_SENTENCES[0] + TEST_SENTENCES[1];
        for (String ps : sentenceString.split("!AIVD")) {
            if (ps != null && !ps.isEmpty()) {
                ps = "!AIVDM" + ps.trim();
                LOG.debug("Found sentence to test: {}", ps);
                try {
                    decodesentences(new AISSentence(ps));
                } catch (AISException t) {
                    LOG.info(t.getMessage(), t);
                }
            }
        }

        LOG.info("Testing sentences that ARE newline separated");
        sentenceString = TEST_SENTENCES[0] + "\n" + TEST_SENTENCES[1];
        for (String ps : sentenceString.split("!AIVD")) {
            if (ps != null && !ps.trim().isEmpty()) {
                ps = "!AIVD" + ps;
                LOG.debug("Found sentence to test: {}", ps);
                try {
                    decodesentences(new AISSentence(ps));
                } catch (AISException t) {
                    LOG.info(t.getMessage(), t);
                }
            }
        }

        LOG.info("AIS message decode test successful!");
    }

    /**
     * 
     * @throws AISSentenceException
     * @throws AISException
     */
    @Test
    public void testDecodingSpeed() throws AISSentenceException, AISException {
        LOG.fatal("*************************************");
        LOG.fatal("*** testDecodingSpeed() ***");
        LOG.fatal("*************************************");
        long start;
        long stop;

        AISSentence pOne = new AISSentence(TEST_COMPOUND_MESSAGE[0]);
        AISSentence pTwo = new AISSentence(TEST_COMPOUND_MESSAGE[1]);
        start = System.nanoTime();
        pOne.process();
        pTwo.process();
        stop = System.nanoTime();

        LOG.fatal("============================================================================================");
        LOG.fatal("sentence processing for two sentences took {} ms", (stop - start) / 1000000f);
        LOG.fatal("============================================================================================");

        AISSentence[] sentences = new AISSentence[] { pOne, pTwo };
        start = System.nanoTime();
        AISMessageFactory.create("UnitTest", sentences);
        stop = System.nanoTime();

        LOG.fatal("============================================================================================");
        LOG.fatal("Decoding of one compound message took {} ms", (stop - start) / 1000000f);
        LOG.fatal("============================================================================================");

        long processTotalTime = 0;
        long processPerMsgTime = 0;
        long decodeTotalTime = 0;
        long decodePerMsgTime = 0;
        Map<AISMessageType, Integer> counts = new HashMap<>();

        for (int i = 0; i < RUN_COUNT; i++) {
            for (String sentenceStr : TEST_SENTENCES) {
                try {
                    AISSentence sentence = new AISSentence(sentenceStr);
                    start = System.nanoTime();
                    sentence.process();
                    stop = System.nanoTime();
                    processTotalTime += (stop - start);
                    processPerMsgTime += (stop - start) / TEST_SENTENCES.length;

                    start = System.nanoTime();
                    Optional<AISMessage> message = AISMessageFactory.create("UnitTest", sentence);
                    stop = System.nanoTime();
                    decodeTotalTime += (stop - start);
                    decodePerMsgTime += (stop - start) / TEST_SENTENCES.length;
                    if (message.isPresent() && i == 0) {
                        AISMessageType type = message.get().getType();
                        int count = (counts.get(type) == null) ? 0 : counts.get(type);
                        counts.put(type, ++count);
                    }
                } catch (AISException e) {
                    // do nothing
                }
            }

        }

        LOG.fatal("============================================================================================");
        LOG.fatal("Average sentence process time across {} runs for {} messages    :  {} ms",
                RUN_COUNT, TEST_SENTENCES.length, processTotalTime / (1000f * 1000000f));
        LOG.fatal("Average per sentence process time across {} runs                 :  {} ms",
                RUN_COUNT, processPerMsgTime / (1000f * 1000000f));

        LOG.fatal("Average message decode time across {} runs for {} messages    :  {} ms",
                RUN_COUNT, TEST_SENTENCES.length, decodeTotalTime / (1000f * 1000000f));
        LOG.fatal("Average per message decode time across {} runs                 :  {} ms",
                RUN_COUNT, decodePerMsgTime / (1000f * 1000000f));
        LOG.fatal("============================================================================================");

        counts.keySet().forEach((type) -> {
            LOG.printf(Level.FATAL, "%2d x %s", counts.get(type), type);
        });
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
     * @throws AISSentenceException
     */
    @Test
    public void testAISsentenceGenerationFromBinaryString() throws AISSentenceException {
        final String binString = "15P<mB003?L02DPGIfh:F`A<0000";
        LOG.info("*** testAISsentenceGenerationFromBinaryString()");
        AISSentence p = AISSentence.createFromBinaryString(binString, "TEST").process();
        try {
            Assertions.assertTrue(p.isValid(), binString + " is NOT valid.");
        } catch (Exception e) {
            LOG.fatal(e.getMessage(), e);
        }
    }
}
