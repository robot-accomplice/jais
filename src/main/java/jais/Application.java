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
package jais;

import jais.exceptions.AISException;
import jais.messages.AISMessage;
import jais.messages.AISMessageFactory;
import jais.messages.PositionReportBase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * Jonathan Machen {@literal <jonathan.machen@robotaccomplice.com>}
 */
public class Application {

    private final static Logger LOG = LogManager.getLogger(Application.class);
    private final static Options OPTIONS = new Options();

    /**
     *
     * @param inputFilePath  the path to the file containing the AIS packets we wish
     *                       to decode
     * @param outputFilePath the path to the file we should create with the decoded
     *                       messages
     */
    private static void decodeFile(String inputFilePath, String outputFilePath) throws IOException {
        Path in = Paths.get(inputFilePath);
        Path out = Paths.get(outputFilePath);

        if (!in.toFile().exists()) {
            showUsage("Input file \"" + inputFilePath + "\" path is invalid (file does not exist)!");
        } else if (out.toFile().exists()) {
            showUsage("\n\nOutput file \"" + outputFilePath + "\" already exists!\n");
        } else if (!in.toFile().exists()) {
            showUsage("Input file \"" + inputFilePath + "\" path cannot be read (check permissions)!");
        } else {
            // add header to file
            FileWriter.initFile(out);

            Files.lines(in).forEach((String s) -> {
                System.out.println("READ: " + s);
                try {
                    AISPacket packet = (AISPacket.validatePreamble(s))
                            ? new AISPacket(s, inputFilePath)
                            : AISPacket.createFromBinaryString(s, inputFilePath);
                    Optional<AISMessage> msg = AISMessageFactory.create(inputFilePath, packet);
                    if (msg.isPresent()) {
                        try {
                            FileWriter.writeln(out, msg.get());
                        } catch (IOException ioe) {
                            throw new RuntimeException("There was an error writing to file " + outputFilePath, ioe);
                        }
                    }
                } catch (AISException ae) {
                    LOG.warn("Encountered an AISException while processing AISPacket from String \"" + s + "\" "
                            + ae.getMessage());
                }
            });
        }
    }

    /**
     * @param errorMsg any specific error message that may have led us here
     */
    private static void showUsage(String errorMsg) {
        if (errorMsg != null && !errorMsg.isEmpty()) {
            System.err.println("ERROR: " + errorMsg);
        }
        new HelpFormatter().printHelp("java -jar <JAIS jar name> [options]", OPTIONS);
        System.exit(-1);
    }

    /**
     *
     * @param args the commandline arguments to parse
     * @return a CommandLine object representing the parsed data
     */
    private static CommandLine parseCmd(String[] args) {
        CommandLine cmd = null;

        OPTIONS.addOption("h", "help", false, "Show this usage screen");
        OPTIONS.addOption("b", "batch", false,
                "Run in \"batch\" mode.  Console mode (the default) provides a JFX interface for"
                        + " decoding one or more (manually entered) AIS strings at a time.  Whereas batch mode (requires -i/--infile &"
                        + " -o/--outile switches) decodes a user specified plain text file of newline separated AIS packets and outputs"
                        + " each decoded message to a new line of comma separated values to a user specified output file location.");
        OPTIONS.addOption("i", "infile", true,
                "The path to a plain text file consisting of newline separated AIS message"
                        + " strings (e.g. !AIVDM,1,1,,B,15N9W:0P00ISR5hA7<A8:OvT0498,0*2F) to be decoded.");
        OPTIONS.addOption("o", "outfile", true,
                "The path to a target file location for decoded messages in CSV format (decoded"
                        + " messages are separated by newline, fields within a message are separated by commas).");

        try {
            cmd = new DefaultParser().parse(OPTIONS, args);
        } catch (ParseException pe) {
            showUsage(pe.getMessage());
        }

        return cmd;
    }

    /**
     *
     * @param args the arguments passed to the program when it was run
     * @throws IOException if we were provided with invalid or unusable file
     *                     location data
     */
    public static void main(String[] args) throws IOException {
        CommandLine cmd = parseCmd(args);

        if (cmd.hasOption("h"))
            showUsage(null);
        else if (cmd.hasOption("b")) {
            if (!cmd.hasOption("i"))
                showUsage("No input file specified!");
            else if (!cmd.hasOption("o"))
                showUsage("No output file location specified!");
            else
                Application.decodeFile(cmd.getOptionValue("i"), cmd.getOptionValue("o"));
        } else
            Console.draw(args);
    }

    /**
     *
     */
    static class FileWriter {

        private final static String HEADER = "Accurate,Course,Heading,Latitude,Longitude,Maneuver,Position,Radio,RAIM,Second,Speed,Status,Turn";

        /**
         * opens file for creation and adds header line
         * 
         * @param path the path at which we should open a new file
         */
        public static void initFile(Path path) throws IOException {
            LOG.info("Initiating file at " + path.toString());
            Files.write(path, HEADER.getBytes(), StandardOpenOption.CREATE_NEW);
            LOG.info("WROTE: " + HEADER);
        }

        /**
         * @param path    the path to the file where we will write the message
         * @param message the AISMessage we will write to the file
         * @throws IOException if we are unable to write to the file for any reason
         */
        public static void writeln(Path path, AISMessage message) throws IOException {
            StringBuilder msgSb = new StringBuilder();

            // build CSV line from message
            switch (message.getType()) {
                case POSITION_REPORT_CLASS_A:
                case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
                case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                    System.out.println("Writing " + message.getType() + " message to file...");
                    PositionReportBase prb = (PositionReportBase) message;
                    msgSb.append(prb.isAccurate());
                    msgSb.append(",").append(prb.getCourse());
                    msgSb.append(",").append(prb.getHeading());
                    msgSb.append(",").append(prb.getLat());
                    msgSb.append(",").append(prb.getLon());
                    msgSb.append(",").append(prb.getManeuver());
                    msgSb.append(",").append(prb.getPosition());
                    msgSb.append(",").append(prb.getRadio());
                    msgSb.append(",").append(prb.isRaim());
                    msgSb.append(",").append(prb.getSecond());
                    msgSb.append(",").append(prb.getSpeed());
                    msgSb.append(",").append(prb.getStatus());
                    msgSb.append(",").append(prb.getTurn());
                    Files.write(path, msgSb.append("\n").toString().getBytes(),
                            StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    LOG.info("WROTE: " + msgSb.toString());
                    break;
                default:
                    LOG.warn("Skipping " + message.getType().name() + " message");
            }
        }
    }
}
