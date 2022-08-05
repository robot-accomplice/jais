package jais.messages.nmea;

import jais.exceptions.ParseException;

public interface NMEASentence extends jais.Sentence {

    public void parse() throws ParseException;

    public ENMEAType getType();
}
