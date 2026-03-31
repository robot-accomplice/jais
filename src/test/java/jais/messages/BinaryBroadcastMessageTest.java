package jais.messages;

import jais.AISSentence;
import jais.messages.binarybroadcast.IMO289Environmental;
import jais.messages.enums.BinaryBroadcastMessageEnvironmentalType;
import jais.messages.enums.BinaryBroadcastMessageType;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BinaryBroadcastMessageTest {

    private static final String SAMPLE_AIS_SENTENCE = "!AIVDM,1,1,,A,18KVnN002RKd40PFnA03aBf>0000,0*18";

    @Test
    public void fetchesKnownSubtypeByDacAndFid() {
        assertEquals(BinaryBroadcastMessageType.IMO289_TEXT_DESCRIPTION,
                BinaryBroadcastMessageType.fetch(1, 29, 168));
    }

    @Test
    public void decodesEnvironmentalReportHeaders() {
        IMO289Environmental message = new IMO289Environmental("UnitTest", new AISSentence(SAMPLE_AIS_SENTENCE));

        BitSet data = new BitSet(112);
        setUnsigned(data, 0, 3, 9);
        setUnsigned(data, 4, 8, 31);
        setUnsigned(data, 9, 13, 23);
        setUnsigned(data, 14, 19, 59);
        setUnsigned(data, 20, 26, 42);
        data.set(27);
        data.set(111);

        message.setData(data);
        message.decode();

        assertEquals(1, message.getReports().size());
        assertEquals(BinaryBroadcastMessageEnvironmentalType.WEATHER, message.getReports().get(0).getType());
        assertEquals(31, message.getReports().get(0).getUtcDay());
        assertEquals(23, message.getReports().get(0).getUtcHour());
        assertEquals(59, message.getReports().get(0).getUtcMinute());
        assertEquals(42, message.getReports().get(0).getSiteId());
        assertNotNull(message.getReports().get(0).getSensorData());
    }

    private static void setUnsigned(BitSet bits, int startBit, int endBit, int value) {
        int width = endBit - startBit + 1;
        for (int i = 0; i < width; i++) {
            int mask = 1 << (width - i - 1);
            bits.set(startBit + i, (value & mask) != 0);
        }
    }
}
