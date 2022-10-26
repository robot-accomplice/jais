package jais;

import jais.exceptions.ParseException;
import jais.messages.enums.SentenceType;

public interface Sentence {

    void parse() throws ParseException;

    boolean isValid();

    static boolean isValid(SentenceType sentenceType, String preamble, int fieldCount) {
        return (fieldCount == sentenceType.getFieldCount()) && preamble.equals(sentenceType.getPreamble());
    }
}
