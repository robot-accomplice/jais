# JAIS

A Java [AIS](https://en.wikipedia.org/wiki/Automatic_identification_system) decoding library.

JAIS targets Java 17 and can be used either as a library or as a command-line decoder. The current codebase includes concrete decoders for the core AIS sentence types plus addressed and broadcast application-specific message subtypes such as route information, dangerous cargo indication, area notices, tidal windows, clearance-to-enter-port, text descriptions, and environmental reports.

## Maven

```xml
<dependency>
    <groupId>com.robotaccomplice</groupId>
    <artifactId>jais</artifactId>
    <version>3.1.0</version>
</dependency>
```

## Build

```shell
mvn test
```

## Decoded Message Coverage

JAIS currently instantiates and decodes these top-level AIS message types:

- `1` Position Report Class A
- `2` Position Report Class A (Assigned Schedule)
- `3` Position Report Class A (Response to Interrogation)
- `4` Base Station Report
- `5` Static and Voyage Related Data
- `6` Binary Addressed Message
- `7` Binary Acknowledge
- `8` Binary Broadcast Message
- `9` Standard SAR Aircraft Position Report
- `10` UTC and Date Inquiry
- `11` UTC and Date Response
- `12` Addressed Safety Related Message
- `13` Safety Related Acknowledgement
- `14` Safety Related Broadcast Message
- `15` Interrogation
- `16` Assignment Mode Command
- `17` DGNSS Broadcast Binary Message
- `18` Standard Class B CS Position Report
- `19` Extended Class B CS Position Report
- `20` Data Link Management Message
- `21` Aid to Navigation Report
- `22` Channel Management
- `23` Group Assignment Command
- `24` Static Data Report
- `25` Single Slot Binary Message
- `26` Multiple Slot Binary Message
- `27` Position Report for Long Range Applications

For binary addressed application-specific messages, JAIS currently decodes these DAC/FID subtypes:

- `DAC 1 / FID 12` Dangerous Cargo Indication (IMO236, deprecated)
- `DAC 1 / FID 14` Tidal Window (IMO236, deprecated)
- `DAC 1 / FID 16` Number of Persons on Board, deprecated and current IMO289 forms
- `DAC 1 / FID 18` Clearance Time to Enter Port
- `DAC 1 / FID 23` Area Notice (addressed)
- `DAC 1 / FID 25` Dangerous Cargo Indication
- `DAC 1 / FID 28` Route Information
- `DAC 1 / FID 30` Text Description
- `DAC 1 / FID 32` Tidal Window

For binary broadcast application-specific messages, JAIS currently decodes these DAC/FID subtypes:

- `DAC 1 / FID 11` Meteorological and Hydrological Data (IMO236, deprecated)
- `DAC 1 / FID 13` Fairway Closed
- `DAC 1 / FID 15` Extended Ship Static and Voyage Related Data (IMO236, deprecated)
- `DAC 1 / FID 17` VTS Generated Synthetic Targets
- `DAC 1 / FID 19` Marine Traffic Signal
- `DAC 1 / FID 22` Area Notice
- `DAC 1 / FID 24` Extended Ship Static and Voyage Related Data
- `DAC 1 / FID 26` Environmental
- `DAC 1 / FID 27` Route Information
- `DAC 1 / FID 29` Text Description
- `DAC 1 / FID 31` Meteorological and Hydrological Data
- `DAC 1 / FID 33` Weather Observation Report from Ship

## CLI

JAIS can also be executed like a binary enabling ad hoc decoding:

```shell
usage: java -jar <JAIS jar name> [options]

 -b,--batch           Run in "batch" mode.  Console mode (the default)
                      provides a JFX interface for decoding one or more
                      (manually entered) AIS strings at a time.  Whereas
                      batch mode (requires -i/--infile & -o/--outile
                      switches) decodes a user specified plain text file
                      of newline separated AIS packets and outputs each
                      decoded message to a new line of comma separated
                      values to a user specified output file location.

 -h,--help            Show this usage screen

 -i,--infile <arg>    The path to a plain text file consisting of newline
                      separated AIS message strings (e.g.
                      !AIVDM,1,1,,B,15N9W:0P00ISR5hA7<A8:OvT0498,0*2F) to
                      be decoded.

 -o,--outfile <arg>   The path to a target file location for decoded
                      messages in CSV format (decoded messages are
                      separated by newline, fields within a message are
                      separated by commas).
```

## Code Usage Example

```java
import jais.AISSentence;
import jais.messages.*;

public class Example {

    public static void main(String[] args) {

        String packetString = null;
        if(ags.length == 0) {
            System.err.println("Please provide an AIS packet string as an argument!");
        }

        Optional<AISMessage> omsg = AISMessageFactory.create("My AIS Source", args);
        if (!omsg.isPresent()) {
            System.out.println("No parsable AISMessage in string");
            return;
        }

        AISMessage message = omsg.get();

        switch (message.getType()) {
            case POSITION_REPORT_CLASS_A:
            case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
            case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                System.out.println("Decoded a new Position Report");
                PositionReportBase prb = (PositionReportBase)message;
                System.out.printf("MMSI: %d\n", prb.getMmsi());
                System.out.printf("Country of Origin: %s\n", prb.getCountryOfOrigin());
                System.out.printf("Course Over Ground: %f\n", prb.getCourseOverGround());
                System.out.printf("Heading: %d\n", prb.getHeading());
                System.out.printf("Latitude: %f\n", prb.getLat());
                System.out.printf("Longitude: %f\n", prb.getLon());
                System.out.printf("Maneuver: %s\n", prb.getManeuver().name());
                System.out.printf("Radio: %d\n", prb.getRadio());
                System.out.printf("Rate of Turn: %f\n", prb.getRateOfTurn());
                System.out.printf("Repeat: %d\n", prb.getRepeat());
                System.out.printf("Second: %d\n", prb.getSecond());
                System.out.printf("Source: %s\n", prb.getSource());
                System.out.printf("Speed: %f\n", prb.getSpeed());
                System.out.printf("Status: %s\n", prb.getStatus().name());
                System.out.printf("Time Received: %s\n", prb.getTimeReceived());
                break;
            case STANDARD_CLASS_B_CS_POSITION_REPORT:
            case EXTENDED_CLASS_B_CS_POSITION_REPORT:
            case STATIC_AND_VOYAGE_RELATED_DATA:
            case STATIC_DATA_REPORT:
                System.out.println("Decoded a new Static & Voyage Related Data Message");
                StaticAndVoyageRelatedData savrd = (StaticAndVoyageRelatedData)message;
                System.out.printf("MMSI: %d\n", savrd.getMmsi());
                System.out.printf("IMO: %d\n", savrd.getImo());
                System.out.printf("Callsign: %s\n", savrd.getCallsign());
                System.out.printf("Country of Origin: %s\n", savrd.getCountryOfOrigin());
                System.out.printf("Destination: %s\n", savrd.getDestination());
                System.out.printf("ETA: %s\n", savrd.getETA());
                System.out.printf("Month: %d\n", savrd.getMonth());
                System.out.printf("Day: %d\n", savrd.getDay());
                System.out.printf("Hour: %d\n", savrd.getHour());
                System.out.printf("Minute: %d\n", savrd.getMinute());
                System.out.printf("Draught: %f\n", savrd.getDraught());
                System.out.printf("EPFD: %s\n", savrd.getEpfd().name());
                System.out.printf("Repeat: %d\n", savrd.getRepeat());
                System.out.printf("Ship Type: %s\n", savrd.getShipType().name());
                System.out.printf("To Bow: %d\n", savrd.getToBow());
                System.out.printf("To Port: %d\n", savrd.getToPort());
                System.out.printf("To Starboard: %d\n", savrd.getToStarboard());
                System.out.printf("To Stern: %d\n", savrd.getToStern());
                break;
            default:
                LOG.trace("Unhandled message type: {} {}", message.getType(),
                    message.getType().getDescription());
        }
    }
}
```

# Contributors
- [Jonathan Machen](https://gitlab/robot_accomplice)
- [Sririsha Tankash](https://gitlab.com/stankashala)
- [Tanvir Mobin Islam](https://gitlab.com/mobintanvir) [LinkedIn](https://www.linkedin.com/in/tanvirsyead/)
