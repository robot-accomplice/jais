# JAIS

A Java [AIS](https://en.wikipedia.org/wiki/Automatic_identification_system) decoding library

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

Code Usage Example:

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