package jais;

import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;

public interface Sentence {

    void parse() throws ParseException;

    SentenceType getSentenceType();
}
