package jais.messages.nmea;

import jais.ByteArrayUtils;
import jais.Sentence;
import jais.messages.enums.SentenceType;

public abstract class NMEASentenceBase implements Sentence {

    protected SentenceType sentenceType;
    protected byte[] sentence;

    /**
     * @return
     */
    @Override
    public boolean isValid() {
        byte[][] fields = ByteArrayUtils.fastSplit(this.sentence, ',');
        String preamble = ByteArrayUtils.bArray2Str(fields[0]);
        return Sentence.isValid(this.sentenceType, preamble, fields.length);
    }

    /**
     *
     * @return
     */
    public SentenceType getSentenceType() {
        return this.sentenceType;
    }
}
