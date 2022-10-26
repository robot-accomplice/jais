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
        AISSentence sentence = (AISSentence.validatePreamble(packetString))
                ? new AISSentence(packetString)
                : AISSentence.createFromBinaryString(packetString, null);
        sentence.process();

        Optional<AISMessage> omsg;

        omsg = AISMessageFactory.create("My AIS Source", sentence);
        if (!omsg.isPresent()) {
            System.out.println("No parsable AISMessage in string");
            return;
        }

        AISMessage message = omsg.get();

        switch (message.getType()) {
            case POSITION_REPORT_CLASS_A:
            case POSITION_REPORT_CLASS_A_ASSIGNED_SCHEDULE:
            case POSITION_REPORT_CLASS_A_RESPONSE_TO_INTERROGATION:
                PositionReportBase pmsg = (PositionReportBase) message;
                
                break;
            case STANDARD_CLASS_B_CS_POSITION_REPORT:
            case EXTENDED_CLASS_B_CS_POSITION_REPORT:
            case STATIC_AND_VOYAGE_RELATED_DATA:
            case STATIC_DATA_REPORT:
                break;
            default:
                LOG.trace("Unhandled message type: {} {}", message.getType(),
                    message.getType().getDescription());
        }
    }
}
```