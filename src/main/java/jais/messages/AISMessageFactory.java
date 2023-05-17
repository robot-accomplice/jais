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
package jais.messages;

import jais.AISSentence;
import jais.ByteArrayUtils;
import jais.messages.enums.AISMessageType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class AISMessageFactory {

    /**
     *
     * @param source          the source of the message
     * @param sentenceStrings the array of packet strings from which we will compose
     *                        our AISMessage
     * @return An Optional which may contain the decoded AISMessage
     */
    public static Optional<AISMessage> create(String source, String... sentenceStrings) {
        List<AISSentence> sentences = new ArrayList<>();
        AISSentence[] sentenceArray = null;

        for (String s : sentenceStrings) {
            Optional<AISSentence[]> os = AISSentence.splitOrTruncate(s);

            if (os.isPresent()) {
                sentences.addAll(Arrays.asList(os.get()));
                sentenceArray = new AISSentence[sentences.size()];
            }
        }

        return (sentenceArray != null) ? create(source, sentences.toArray(sentenceArray)) : Optional.empty();
    }

    /**
     *
     * @param source    the source of this message
     * @param sentences the AISsentences from which we will compose our AISMessage
     * @return An Optional which may contain our decoded AISMessage
     */
    public static Optional<AISMessage> create(String source, AISSentence... sentences) {
        try {
            if (sentences == null || sentences.length < 1) {
                return Optional.empty();
            }

            byte[] compositeBytes = AISSentence.concatenate(sentences);
            if (compositeBytes == null || compositeBytes.length == 0) {
                return Optional.empty();
            }

            // we need the message type in order to invoke the reflective constructor
            Optional<AISMessageType> mType = AISMessageDecoder.decodeMessageType(ByteArrayUtils.bArray2Str(compositeBytes));

            if (mType.isPresent()) {
                AISMessage message;
                switch (mType.get()) {
                    case POSITION_REPORT_CLASS_A -> message = new PositionReportClassA(source, sentences);
                    case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE ->
                            message = new PositionReportClassAAssignedSchedule(source, sentences);
                    case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION ->
                            message = new PositionReportClassAResponseToInterrogation(source, sentences);
                    case BASE_STATION_REPORT -> message = new BaseStationReport(source, sentences);
                    case STATIC_AND_VOYAGE_RELATED_DATA -> message = new StaticAndVoyageRelatedData(source, sentences);
                    case BINARY_ACKNOWLEDGE -> message = new BinaryAcknowledge(source, sentences);
                    case STANDARD_SAR_AIRCRAFT_POSITION_REPORT ->
                            message = new StandardSARAircraftPositionReport(source, sentences);
                    case UTC_AND_DATE_INQUIRY -> message = new UTCDateInquiry(source, sentences);
                    case UTC_AND_DATE_RESPONSE -> message = new UTCDateResponse(source, sentences);
                    case ADDRESSED_SAFETY_RELATED_MESSAGE ->
                            message = new AddressedSafetyRelatedMessage(source, sentences);
                    case SAFETY_RELATED_ACKNOWLEDGEMENT ->
                            message = new SafetyRelatedAcknowledgement(source, sentences);
                    case SAFETY_RELATED_BROADCAST_MESSAGE ->
                            message = new SafetyRelatedBroadcastMessage(source, sentences);
                    case INTERROGATION -> message = new Interrogation(source, sentences);
                    case ASSIGNMENT_MODE_COMMAND -> message = new AssignmentModeCommand(source, sentences);
                    case DGNSS_BROADCAST_BINARY_MESSAGE -> message = new DGNSSBroadcastBinaryMessage(source, sentences);
                    case STANDARD_CLASS_B_CS_POSITION_REPORT ->
                            message = new StandardClassBCSPositionReport(source, sentences);
                    case EXTENDED_CLASS_B_CS_POSITION_REPORT ->
                            message = new ExtendedClassBCSPositionReport(source, sentences);
                    case DATA_LINK_MANAGEMENT_MESSAGE -> message = new DataLinkManagementMessage(source, sentences);
                    case AID_TO_NAVIGATION_REPORT -> message = new AidToNavigationReport(source, sentences);
                    case CHANNEL_MANAGEMENT -> message = new ChannelManagement(source, sentences);
                    case GROUP_ASSIGNMENT_COMMAND -> message = new GroupAssignmentCommand(source, sentences);
                    case STATIC_DATA_REPORT -> message = new StaticDataReport(source, sentences);
                    case SINGLE_SLOT_BINARY_MESSAGE -> message = new SingleSlotBinaryMessage(source, sentences);
                    case MULTIPLE_SLOT_BINARY_MESSAGE -> message = new MultipleSlotBinaryMessage(source, sentences);
                    case POSITION_REPORT_FOR_LONG_RANGE_APPLICATIONS ->
                            message = new LongRangeAISBroadcastMessage(source, sentences);
                    case BINARY_BROADCAST_MESSAGE -> message = new BinaryBroadcastMessage(source, sentences);
                    default -> {
                        // invalid, unknown message type
                        return Optional.empty();
                    }
                }

                message.decode(); // decode message

                return Optional.of(message);
            } else {
                return Optional.empty();
            }
        } catch (NullPointerException npe) {
            return Optional.empty();
        }
    }
}
