/*
 * Copyright 2016-2019 Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}.
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
package jais;

import jais.AISSentence.Preamble;
import jais.messages.AISMessageFactory;
import jais.messages.AISMessage;
import jais.messages.BaseStationReport;
import jais.messages.ExtendedClassBCSPositionReport;
import jais.messages.PositionReportBase;
import jais.messages.StandardClassBCSPositionReport;
import jais.messages.StaticAndVoyageRelatedData;
import jais.messages.enums.MMSIType;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class ConsoleController implements Initializable {

    static final Logger LOG = LogManager.getLogger(ConsoleController.class);

    private final static String BORDER_DOUBLE = "====================================================";
    private final static String BORDER_SINGLE = "---------------------------------------------";

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML
    private TextArea outputArea;

    @FXML
    private TextArea inputArea;

    @FXML
    private Button decodeButton;

    @FXML
    private Button clearButton;

    /**
     *
     * @param event the UI event that has taken place
     */
    @FXML
    private void handleDecodeAction(ActionEvent event) {
        String inText = inputArea.getText(); // retrieve input
        inputArea.clear(); // clear the field

        try {
            appendLineToOutput(BORDER_DOUBLE);
            appendLineToOutput("Processing:\n" + inText);

            if (inText == null || inText.isEmpty())
                throw new Exception("Null Input");

            String[] sentenceStrings = inText.split("\n");
            AISSentence[] sentences = new AISSentence[sentenceStrings.length];
            for (int i = 0; i < sentenceStrings.length; i++) {
                AISSentence sentence = (AISSentence.validatePreamble(sentenceStrings[i]))
                        ? new AISSentence(sentenceStrings[i])
                        : AISSentence.createFromPayload(sentenceStrings[i], null);
                sentence.process();
                appendLineToOutput(BORDER_SINGLE);
                TagBlock tb = sentence.getTagBlock();
                if (tb != null) {
                    appendLineToOutput("TagBlock: " + ByteArrayUtils.bArray2Str(tb.rawTagBlock));
                    appendLineToOutput(BORDER_SINGLE);
                    appendLineToOutput("\tsource           : " + tb.getSource());
                    appendLineToOutput("\tdestination      : " + ((tb.destination == null) ? "null"
                            : ByteArrayUtils.bArray2Str(tb.destination)));
                    appendLineToOutput("\ttimestamp        : " + tb.getTimestamp());
                    appendLineToOutput("\trelative time    : " + tb.getRelativeTime());
                    appendLineToOutput("\tsentence grouping: " + ((tb.sentenceGrouping == null) ? "null"
                            : ByteArrayUtils.bArray2Str(tb.sentenceGrouping)));
                    appendLineToOutput("\tline count       : " + tb.getLineCount());
                    appendLineToOutput("\ttext string      : " + ((tb.textBytes == null) ? "null"
                            : tb.getText()));
                } else {
                    appendLineToOutput("TagBlock: ");
                    appendLineToOutput(BORDER_SINGLE);
                    appendLineToOutput("\t- none -");
                }
                Preamble pre = Preamble.parse(sentence.getUnparsedSentence());
                appendLineToOutput(BORDER_SINGLE);
                appendLineToOutput("Preamble: " + ByteArrayUtils.bArray2Str(pre.parsed));
                appendLineToOutput(BORDER_SINGLE);
                appendLineToOutput("\tformat       : " + ByteArrayUtils.bArray2Str(pre.format));
                appendLineToOutput("\tencapsulated : " + pre.isEncapsulated);
                appendLineToOutput("\tproprietary  : " + pre.isProprietary);
                appendLineToOutput("\tquery        : " + pre.isQuery);
                appendLineToOutput("\ttalker       : " + pre.talker.description);
                if (pre.manufacturer == null)
                    appendLineToOutput("\tmanufacturer : null");
                else {
                    appendLineToOutput("\tmanufacturer : " + pre.manufacturer.fullName);
                }
                appendLineToOutput("\tvalid sentence: " + sentence.isValid());
                sentences[i] = sentence;
            }
            appendLineToOutput("\tfragments req     : " + sentences[0].getFragmentCount());
            appendLineToOutput("\tfragments provided:");
            for (AISSentence s : sentences) {
                appendLineToOutput("\t" + s.getFragmentNumber());
            }
            appendLineToOutput(BORDER_SINGLE);

            Optional<AISMessage> msgOpt = AISMessageFactory.create("CONSOLE", sentences);
            if (msgOpt.isPresent()) {
                AISMessage msg = msgOpt.get();
                // from AISMessageBase
                appendLineToOutput("Type   : " + msg.getType());
                appendLineToOutput("Repeat : " + msg.getRepeat());
                appendLineToOutput("MMSI   : " + msg.getMmsi() + ((msg.hasValidMmsi()) ? " (VALID)" : " (INVALID)"));
                appendLineToOutput(BORDER_SINGLE);

                MMSIType type = MMSIType.forMMSI(msg.getMmsi());
                appendLineToOutput("MMSI Src: " + (type != null ? type.name() : "INVALID"));

                switch (msg.getType()) {
                    case BASE_STATION_REPORT -> {
                        BaseStationReport bsr = (BaseStationReport) msg;
                        appendLineToOutput("Year     : " + bsr.getYear());
                        appendLineToOutput("Month    : " + bsr.getMonth());
                        appendLineToOutput("Day      : " + bsr.getDay());
                        appendLineToOutput("Hour     : " + bsr.getHour());
                        appendLineToOutput("Minute   : " + bsr.getMinute());
                        appendLineToOutput("Second   : " + bsr.getSecond());
                        appendLineToOutput("EPFD     : " + bsr.getEpfd());
                        appendLineToOutput("Lat      : " + bsr.getLat());
                        appendLineToOutput("Lon      : " + bsr.getLon());
                        appendLineToOutput("Position : " + bsr.getPosition());
                        appendLineToOutput("Radio    : " + bsr.getRadio());
                    }
                    case POSITION_REPORT_CLASS_A, POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE, POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION -> {
                        PositionReportBase prb = (PositionReportBase) msg;

                        // from PositionReportBase
                        appendLineToOutput("Accuracy : " + prb.isAccuracy());
                        appendLineToOutput("Course   : " + prb.getCourseOverGround());
                        appendLineToOutput("Heading  : " + prb.getHeading());
                        appendLineToOutput("Latitude : " + prb.getLat());
                        appendLineToOutput("Longitude: " + prb.getLon());
                        appendLineToOutput("Maneuver : " + prb.getManeuver());
                        if (AISMessage.isValidPosition(prb.getLat(), prb.getLon()))
                            appendLineToOutput("Position : " + prb.getPosition());
                        else
                            appendLineToOutput("Position : INVALID");
                        appendLineToOutput("Radio    : " + prb.getRadio());
                        appendLineToOutput("RAIM     : " + prb.isRaim());
                        appendLineToOutput("Second   : " + prb.getSecond());
                        appendLineToOutput("Speed    : " + prb.getSpeed());
                        appendLineToOutput("Status   : " + prb.getStatus());
                        appendLineToOutput("Turn     : " + prb.getRateOfTurn());
                    }
                    case STATIC_AND_VOYAGE_RELATED_DATA -> {
                        StaticAndVoyageRelatedData savrd = (StaticAndVoyageRelatedData) msg;
                        appendLineToOutput("AIS Version  : " + savrd.getVersion());
                        appendLineToOutput("IMO Number   : " + savrd.getImo() +
                                ((AISMessage.isValidImo(savrd.getImo())) ? " (VALID)" : " (INVALID)"));
                        appendLineToOutput("Call Sign    : " + savrd.getCallSign());
                        appendLineToOutput("Ship Name    : " + savrd.getShipName());
                        appendLineToOutput("Ship Type    : " + savrd.getShipType());
                        appendLineToOutput("To Bow       : " + savrd.getToBow());
                        appendLineToOutput("To Stern     : " + savrd.getToStern());
                        appendLineToOutput("To Port      : " + savrd.getToPort());
                        appendLineToOutput("To Starboard : " + savrd.getToStarboard());
                        appendLineToOutput("EPFD Fix Type: " + savrd.getEpfd());
                        appendLineToOutput("ETA Month    : " + savrd.getMonth());
                        appendLineToOutput("ETA Day      : " + savrd.getDay());
                        appendLineToOutput("ETA Hour     : " + savrd.getHour());
                        appendLineToOutput("ETA Minute   : " + savrd.getMinute());
                        appendLineToOutput("Draught      : " + savrd.getDraught());
                        appendLineToOutput("Destination  : " + savrd.getDestination());
                        appendLineToOutput("DTE Ready    : " + savrd.dteReady());
                        appendLineToOutput("DTE Value    : " + savrd.isDte());
                        appendLineToOutput("Spare        : " + savrd.isSpare());
                    }
                    case STANDARD_CLASS_B_CS_POSITION_REPORT -> {
                        StandardClassBCSPositionReport scbpr = (StandardClassBCSPositionReport) msg;
                        appendLineToOutput("Course   : " + scbpr.getCourseOverGround());
                        appendLineToOutput("Heading  : " + scbpr.getHeading());
                        appendLineToOutput("Latitude : " + scbpr.getLat());
                        appendLineToOutput("Longitude: " + scbpr.getLon());
                        appendLineToOutput("Radio    : " + scbpr.getRadio());
                        appendLineToOutput("Second   : " + scbpr.getSecond());
                        appendLineToOutput("Speed    : " + scbpr.getSpeed());
                    }
                    case EXTENDED_CLASS_B_CS_POSITION_REPORT -> {
                        ExtendedClassBCSPositionReport ecbpr = (ExtendedClassBCSPositionReport) msg;
                        appendLineToOutput("Assigned    : " + ecbpr.isAssigned());
                        appendLineToOutput("Course      : " + ecbpr.getCourseOverGround());
                        appendLineToOutput("Heading     : " + ecbpr.getHeading());
                        appendLineToOutput("Latitude    : " + ecbpr.getLat());
                        appendLineToOutput("Longitude   : " + ecbpr.getLon());
                        appendLineToOutput("Second      : " + ecbpr.getSecond());
                        appendLineToOutput("Speed       : " + ecbpr.getSpeed());
                        appendLineToOutput("EPFD        : " + ecbpr.getEpfd().name());
                        appendLineToOutput("Ship Type   : " + ecbpr.getShipType().name());
                        appendLineToOutput("To Bow      : " + ecbpr.getToBow());
                        appendLineToOutput("To Stern    : " + ecbpr.getToStern());
                        appendLineToOutput("To Port     : " + ecbpr.getToPort());
                        appendLineToOutput("To Starboard: " + ecbpr.getToStarboard());
                    }
                    default -> appendLineToOutput("Not breaking out fields of " + msg.getType().getDescription());
                }
            } else {
                appendLineToOutput("AISMessageFactory returned a null message!");
                LOG.warn("AISMessageFactory returned a null message!");
            }
        } catch (Exception e) {
            appendLineToOutput("Unable to decode sentence: \"" + inText + "\": " + e.getMessage());
            appendLineToOutput("StackTrace:\n" + e);
            LOG.error("Unable to decode sentence: \"{}\": {}", inText, e.getMessage());
            if (LOG.isTraceEnabled())
                LOG.trace("StackTrace:", e);
        } finally {
            appendLineToOutput(BORDER_DOUBLE);
        }
    }

    /**
     *
     * @param s The String to append
     */
    private void appendLineToOutput(String s) {
        outputArea.appendText(s + "\n");
    }

    /**
     *
     * @param event
     */
    @FXML
    private void handleClearAction(ActionEvent event) {
        outputArea.clear();
    }

    /**
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        location = url;
        resources = rb;

        /* nothing to do */
        Font fixedWidthFont = null;
        String osName = System.getProperty("os.name");

        System.out.printf("Selecting proper font for OS %s\n", osName);

        if (osName.contains("Windows")) {
            fixedWidthFont = new Font("Consolas", 12);
        } else if (osName.contains("Mac OS")) {
            fixedWidthFont = new Font( "Courier New Bold", 11);
        } else if (osName.contains("Linux")){
            fixedWidthFont = new Font("Ubuntu Monospace", 12);
        }
        outputArea.setFont(fixedWidthFont);
        inputArea.setFont(fixedWidthFont);
    }
}
