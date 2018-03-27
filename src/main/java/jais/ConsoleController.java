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

package jais;

import jais.AISPacket.Preamble;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Jonathan Machen
 */
public class ConsoleController implements Initializable {
    
    static final Logger LOG = LogManager.getLogger( ConsoleController.class );

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
     * @param event 
     */
    @FXML
    private void handleDecodeAction( ActionEvent event ) {
        String inText = inputArea.getText();  // retrieve input
        inputArea.clear();  // clear the field
        
        try {
            appendLineToOutput( "****************************************************" );
            appendLineToOutput( "Processing:\n" + inText );
            
            if( inText == null || inText.isEmpty() ) throw new Exception( "Null Input" );
            
            String [] packetStrings = inText.split( "\n" );
            AISPacket [] packets = new AISPacket[ inText.length() ];
            for( int i = 0; i < packetStrings.length; i++ ) {
                AISPacket packet = ( AISPacket.validatePreamble( packetStrings[i] ) ) ? 
                        new AISPacket( packetStrings[i] ) : 
                        AISPacket.createFromBinaryString( packetStrings[i], null );
                packet.process();
                    appendLineToOutput( "---------------------------------------------" );
                TagBlock tb = packet.getTagBlock();
                if( tb != null ) {
                    appendLineToOutput( "TagBlock: " + AISPacket.bArray2Str( tb.rawTagBlock ) );
                    appendLineToOutput( "---------------------------------------------" );
                    appendLineToOutput( "\tsource           : " + AISPacket.bArray2Str( tb.getSource() ) );
                    appendLineToOutput( "\tdestination      : " + ( ( tb.destination == null ) ? "null" : AISPacket.bArray2Str( tb.destination ) ) );
                    appendLineToOutput( "\ttimestamp        : " + tb.getTimestamp() );
                    appendLineToOutput( "\trelative time    : " + tb.getRelativeTime() );
                    appendLineToOutput( "\tsentence grouping: " + ( ( tb.sentenceGrouping == null ) ? "null" : AISPacket.bArray2Str( tb.sentenceGrouping) ) );
                    appendLineToOutput( "\tline count       : " + tb.getLineCount() );
                    appendLineToOutput( "\ttext string      : " + ( ( tb.textStr == null ) ? "null" : AISPacket.bArray2Str( tb.textStr ) ) );
                } else {
                    appendLineToOutput( "TagBlock: " );
                    appendLineToOutput( "---------------------------------------------" );
                    appendLineToOutput( "\t- none -" );
                }
                Preamble pre = Preamble.parse( packet.getRawPacket() );
                appendLineToOutput( "---------------------------------------------" );
                appendLineToOutput( "Preamble: " + AISPacket.bArray2Str( pre.parsed ) );
                appendLineToOutput( "---------------------------------------------" );
                appendLineToOutput( "\tformat       : " + AISPacket.bArray2Str( pre.format ) );
                appendLineToOutput( "\tencapsulated : " + pre.isEncapsulated );
                appendLineToOutput( "\tproprietary  : " + pre.isProprietary );
                appendLineToOutput( "\tquery        : " + pre.isQuery );
                appendLineToOutput( "\ttalker       : " + pre.talker.description );
                if( pre.manufacturer == null ) {
                    appendLineToOutput( "\tmanufacturer : null" );
                } else {
                    appendLineToOutput( "\tmanufacturer : " + pre.manufacturer.fullName );
                }
                appendLineToOutput( "\tvalid packet: " + packet.isValid() );
                packets[i] = packet;
            }
            appendLineToOutput( "---------------------------------------------" );
            
            Optional<AISMessage> msgOpt = AISMessageFactory.create( "CONSOLE", false, packets );
            if( msgOpt.isPresent() ) {
                AISMessage msg = msgOpt.get();
                // from AISMessageBase
                appendLineToOutput( "Type   : " + msg.getType() );
                appendLineToOutput( "Repeat : " + msg.getRepeat() );
                appendLineToOutput( "MMSI   : " + msg.getMmsi() + ( ( msg.hasValidMmsi() ) ? " (VALID)" : " (INVALID)" ) );
                appendLineToOutput( "---------------------------------------------" );
                
                MMSIType type = MMSIType.forMMSI( msg.getMmsi() );
                appendLineToOutput( "MMSI Src: " + ( type != null ? type.name() : "INVALID" ) );

                switch( msg.getType() ) {
                    case BASE_STATION_REPORT:
                        BaseStationReport bsr = ( BaseStationReport ) msg;

                        appendLineToOutput( "Year     : " + bsr.getYear() );
                        appendLineToOutput( "Month    : " + bsr.getMonth() );
                        appendLineToOutput( "Day      : " + bsr.getDay() );
                        appendLineToOutput( "Hour     : " + bsr.getHour() );
                        appendLineToOutput( "Minute   : " + bsr.getMinute() );
                        appendLineToOutput( "Second   : " + bsr.getSecond() );
                        appendLineToOutput( "EPFD     : " + bsr.getEpfd() );
                        appendLineToOutput( "Lat      : " + bsr.getLat() );
                        appendLineToOutput( "Lon      : " + bsr.getLon() );
                        appendLineToOutput( "Position : " + bsr.getPosition() );
                        appendLineToOutput( "Radio    : " + bsr.getRadio() );
                        break;
                    case POSITION_REPORT_CLASS_A:
                    case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                    case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                        PositionReportBase prb = ( PositionReportBase ) msg;

                        // from PositionReportBase
                        appendLineToOutput( "Accuracy : " + prb.isAccurate() );
                        appendLineToOutput( "Course   : " + prb.getCourse() );
                        appendLineToOutput( "Heading  : " + prb.getHeading() );
                        appendLineToOutput( "Latitude : " + prb.getLat() );
                        appendLineToOutput( "Longitude: " + prb.getLon() );
                        appendLineToOutput( "Maneuver : " + prb.getManeuver() );
                        if( prb.isPositionValid() ) appendLineToOutput( "Position : " + prb.getPosition() );
                        else appendLineToOutput( "Position : INVALID" );
                        appendLineToOutput( "Radio    : " + prb.getRadio() );
                        appendLineToOutput( "RAIM     : " + prb.isRaim() );
                        appendLineToOutput( "Second   : " + prb.getSecond() );
                        appendLineToOutput( "Speed    : " + prb.getSpeed() );
                        appendLineToOutput( "Status   : " + prb.getStatus() );
                        appendLineToOutput( "Turn     : " + prb.getTurn() );
                        break;
                    case STATIC_AND_VOYAGE_RELATED_DATA:
                        StaticAndVoyageRelatedData savrd =
                                ( StaticAndVoyageRelatedData ) msg;

                        appendLineToOutput( "AIS Version  : " + savrd.getVersion() );
                        appendLineToOutput( "IMO Number   : " + savrd.getImo() );
                        appendLineToOutput( "Call Sign    : " + savrd.getCallsign() );
                        appendLineToOutput( "Ship Name    : " + savrd.getShipname() );
                        appendLineToOutput( "Ship Type    : " + savrd.getShiptype() );
                        appendLineToOutput( "To Bow       : " + savrd.getToBow() );
                        appendLineToOutput( "To Stern     : " + savrd.getToStern() );
                        appendLineToOutput( "To Port      : " + savrd.getToPort() );
                        appendLineToOutput( "To Starboard : " + savrd.getToStarboard() );
                        appendLineToOutput( "EPFD Fix Type: " + savrd.getEpfd() );
                        appendLineToOutput( "ETA Month    : " + savrd.getMonth() );
                        appendLineToOutput( "ETA Day      : " + savrd.getDay() );
                        appendLineToOutput( "ETA Hour     : " + savrd.getHour() );
                        appendLineToOutput( "ETA Minute   : " + savrd.getMinute() );
                        appendLineToOutput( "Draught      : " + savrd.getDraught() );
                        appendLineToOutput( "Destination  : " + savrd.getDestination() );
                        appendLineToOutput( "DTE Ready    : " + savrd.dteReady() );
                        break;
                    case STANDARD_CLASS_B_CS_POSITION_REPORT:
                        StandardClassBCSPositionReport scbpr = 
                                ( StandardClassBCSPositionReport )msg;
                        
                        appendLineToOutput( "Course   : " + scbpr.getCourse() );
                        appendLineToOutput( "Heading  : " + scbpr.getHeading() );
                        appendLineToOutput( "Latitude : " + scbpr.getLat() );
                        appendLineToOutput( "Longitude: " + scbpr.getLon() );
                        appendLineToOutput( "Radio    : " + scbpr.getRadio() );
                        appendLineToOutput( "Second   : " + scbpr.getSecond() );
                        appendLineToOutput( "Speed    : " + scbpr.getSpeed() );
                        break;
                    case EXTENDED_CLASS_B_CS_POSITION_REPORT:
                        ExtendedClassBCSPositionReport ecbpr = 
                                ( ExtendedClassBCSPositionReport )msg;
                        
                        appendLineToOutput( "Assigned    : " + ecbpr.getAssigned() );
                        appendLineToOutput( "Course      : " + ecbpr.getCourse() );
                        appendLineToOutput( "Heading     : " + ecbpr.getHeading() );
                        appendLineToOutput( "Latitude    : " + ecbpr.getLat() );
                        appendLineToOutput( "Longitude   : " + ecbpr.getLon() );
                        appendLineToOutput( "Second      : " + ecbpr.getSecond() );
                        appendLineToOutput( "Speed       : " + ecbpr.getSpeed() );
                        appendLineToOutput( "EPFD        : " + ecbpr.getEpfd().name() );
                        appendLineToOutput( "Ship Type   : " + ecbpr.getShipType().name() );
                        appendLineToOutput( "To Bow      : " + ecbpr.getToBow() );
                        appendLineToOutput( "To Stern    : " + ecbpr.getToStern() );
                        appendLineToOutput( "To Port     : " + ecbpr.getToPort() );
                        appendLineToOutput( "To Starboard: " + ecbpr.getToStarboard() );
                        break;
                    default:
                        appendLineToOutput( "Not breaking out fields of " + msg.getType().getDescription() );
                }
            } else {
                appendLineToOutput( "AISMessageFactory returned a null message!" );
                LOG.warn( "AISMessageFactory returned a null message!" );
            }
        } catch( Exception e ) {
            appendLineToOutput( "Unable to decode packet: \"" + inText + "\": " + e.getMessage() );
            appendLineToOutput( "StackTrace:\n" + e );
            LOG.error( "Unable to decode packet: \"{}\": {}", inText, e.getMessage() );
            if( LOG.isTraceEnabled() ) LOG.trace( "StackTrace:", e );
        } finally {
            appendLineToOutput( "****************************************************" );
        }
    }
    
    /**
     * 
     * @param s 
     */
    private void appendLineToOutput( String s ) {
        outputArea.appendText( s + "\n" );
    }
    
    /**
     * 
     * @param event 
     */
    @FXML
    private void handleClearAction( ActionEvent event ) {
        outputArea.clear();
    }
    
    /**
     * 
     * @param url
     * @param rb 
     */
    @Override
    public void initialize( URL url, ResourceBundle rb ) {
    }    
}
