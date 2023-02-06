package jais.messages;

import java.util.BitSet;

import org.junit.jupiter.api.Test;

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
    }
}
