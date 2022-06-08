# JAIS
A Java [AIS](https://en.wikipedia.org/wiki/Automatic_identification_system) decoding library

JAIS can also be executed like a binary enabling ad hoc decoding:


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
