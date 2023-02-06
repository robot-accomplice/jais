package jais.messages;

import java.util.Arrays;
import java.util.BitSet;

import org.junit.jupiter.api.Test;

import jais.AISSentence;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AISMessageDecoderTest {

    @Test
    public void testDecodeSignedInt() {
        BitSet b = new BitSet(8);
        b.set(0, true);
        b.set(1, true);
        b.set(2, true);
        b.set(3, true);
        b.set(4, true);
        b.set(5, false);
        b.set(6, false);
        b.set(7, false);

        int result = AISMessageDecoder.decodeSignedInt(b, 0, 7);

        assertEquals(-8, result, "result should be -8");

        AISSentence sentence = new AISSentence( "!AIVDM,1,1,,A,15NHl8500pqSdR8A7jnq9oRF0<<P,0*38");
        sentence.process();
        byte[] bytes = {49, 53, 78, 72, 108, 56, 53, 48, 48, 112, 113, 83, 100, 82, 56, 65, 55, 106, 110, 113, 57, 111, 82, 70, 48, 60, 60, 80};
        assertEquals(Arrays.toString(bytes), Arrays.toString("15NHl8500pqSdR8A7jnq9oRF0<<P".getBytes()));
        assertEquals(Arrays.toString(bytes), Arrays.toString(sentence.getSentenceBody()));
        System.err.printf("bytes:%s\n", Arrays.toString(sentence.getSentenceBody()));
    }
}
