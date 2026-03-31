package jais.messages;

import jais.AISSentence;
import jais.messages.binaryaddressed.IMO289AreaNotice;
import jais.messages.binaryaddressed.IMO289DangerousCargoIndication;
import jais.messages.binaryaddressed.IMO289RouteInformation;
import jais.messages.binaryaddressed.IMO289TidalWindow;
import jais.messages.enums.AreaNoticeType;
import jais.messages.enums.CargoCode;
import jais.messages.enums.CargoUnitCode;
import jais.messages.enums.RouteType;
import jais.messages.enums.SubareaType;
import org.junit.jupiter.api.Test;

import java.util.BitSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BinaryAddressedMessageSubtypeTest {

    private static final String SAMPLE_AIS_SENTENCE = "!AIVDM,1,1,,A,18KVnN002RKd40PFnA03aBf>0000,0*18";

    @Test
    public void decodesRouteInformation() {
        IMO289RouteInformation message = new IMO289RouteInformation("UnitTest", new AISSentence(SAMPLE_AIS_SENTENCE));
        BitSet bits = new BitSet();

        setUnsigned(bits, 88, 97, 12);
        setUnsigned(bits, 98, 100, 1);
        setUnsigned(bits, 101, 105, 2);
        setUnsigned(bits, 106, 109, 4);
        setUnsigned(bits, 110, 114, 15);
        setUnsigned(bits, 115, 119, 9);
        setUnsigned(bits, 120, 125, 30);
        setUnsigned(bits, 126, 143, 120);
        setUnsigned(bits, 144, 148, 1);
        setSigned(bits, 149, 176, encodeScaledCoordinate(12.3456, 600000, 28));
        setSigned(bits, 177, 203, encodeScaledCoordinate(54.321, 600000, 27));
        bits.set(203);

        message.setBits(bits);
        message.decode();

        assertEquals(12, message.getLinkageId());
        assertEquals(RouteType.RECOMMENDED, message.getRouteType());
        assertEquals(1, message.getWaypoints().size());
        assertEquals(12.3456, message.getWaypoints().get(0).getLongitude(), 0.0001);
        assertEquals(54.321, message.getWaypoints().get(0).getLatitude(), 0.0001);
    }

    @Test
    public void decodesDangerousCargoIndication() {
        IMO289DangerousCargoIndication message =
                new IMO289DangerousCargoIndication("UnitTest", new AISSentence(SAMPLE_AIS_SENTENCE));
        BitSet bits = new BitSet();

        setUnsigned(bits, 88, 89, 2);
        setUnsigned(bits, 90, 99, 321);
        setUnsigned(bits, 100, 103, 5);
        setUnsigned(bits, 104, 116, 1 << 10);
        setUnsigned(bits, 117, 120, 1);
        setUnsigned(bits, 121, 133, 77);

        message.setBits(bits);
        message.decode();

        assertEquals(CargoUnitCode.METRIC_TONS, message.getCargoUnit());
        assertEquals(321, message.getAmount());
        assertEquals(2, message.getCargos().size());
        assertEquals(CargoCode.MARPOL_ANNEX_II, message.getCargos().get(0).getCode());
        assertEquals("Category X", message.getCargos().get(0).getDescription());
        assertEquals(CargoCode.IMDG_CODE, message.getCargos().get(1).getCode());
    }

    @Test
    public void decodesAreaNoticeSubareas() {
        IMO289AreaNotice message = new IMO289AreaNotice("UnitTest", new AISSentence(SAMPLE_AIS_SENTENCE));
        BitSet bits = new BitSet();

        setUnsigned(bits, 88, 97, 44);
        setUnsigned(bits, 98, 104, 8);
        setUnsigned(bits, 105, 108, 5);
        setUnsigned(bits, 109, 113, 6);
        setUnsigned(bits, 114, 118, 7);
        setUnsigned(bits, 119, 124, 8);
        setUnsigned(bits, 125, 142, 90);
        setUnsigned(bits, 143, 145, 5);
        setString(bits, 146, 229, "CAUTION ALERTS");

        message.setBits(bits);
        message.decode();

        assertEquals(44, message.getLinkageId());
        assertEquals(AreaNoticeType.CAUTION_TRAFFIC_CONGESTION, message.getNoticeType());
        assertEquals(1, message.getSubareas().size());
        assertEquals(SubareaType.ASSOCIATED_TEXT, message.getSubareas().get(0).getType());
        assertEquals("CAUTION ALERTS", message.getSubareas().get(0).getAssociatedText());
    }

    @Test
    public void decodesTidalWindowPredictions() {
        IMO289TidalWindow message = new IMO289TidalWindow("UnitTest", new AISSentence(SAMPLE_AIS_SENTENCE));
        BitSet bits = new BitSet();

        setUnsigned(bits, 88, 91, 6);
        setUnsigned(bits, 92, 96, 21);
        setSigned(bits, 97, 121, encodeScaledCoordinate(8.5, 60000, 25));
        setSigned(bits, 122, 145, encodeScaledCoordinate(47.25, 60000, 24));
        setUnsigned(bits, 146, 150, 9);
        setUnsigned(bits, 151, 156, 15);
        setUnsigned(bits, 157, 161, 10);
        setUnsigned(bits, 162, 167, 45);
        setUnsigned(bits, 168, 176, 180);
        setUnsigned(bits, 177, 184, 42);

        message.setBits(bits);
        message.decode();

        assertEquals(6, message.getMonth());
        assertEquals(21, message.getDay());
        assertEquals(1, message.getPredictions().size());
        assertEquals(8.5, message.getPredictions().get(0).getLongitude(), 0.0001);
        assertEquals(47.25, message.getPredictions().get(0).getLatitude(), 0.0001);
        assertEquals(4.2f, message.getPredictions().get(0).getCurrentSpeed(), 0.0001f);
    }

    private static void setUnsigned(BitSet bits, int startBit, int endBit, int value) {
        int width = endBit - startBit + 1;
        for (int i = 0; i < width; i++) {
            int mask = 1 << (width - i - 1);
            bits.set(startBit + i, (value & mask) != 0);
        }
    }

    private static void setSigned(BitSet bits, int startBit, int endBit, int value) {
        int width = endBit - startBit + 1;
        int normalized = value;
        if (value < 0) {
            normalized = (1 << width) + value;
        }
        setUnsigned(bits, startBit, endBit, normalized);
    }

    private static void setString(BitSet bits, int startBit, int endBit, String value) {
        int bit = startBit;
        for (char c : value.toCharArray()) {
            int sixBit = c - 64;
            if (c >= ' ' && c <= '?') {
                sixBit = c;
            }
            setUnsigned(bits, bit, bit + 5, sixBit);
            bit += 6;
            if (bit > endBit) {
                break;
            }
        }
    }

    private static int encodeScaledCoordinate(double value, int scale, int width) {
        int encoded = (int) Math.round(value * scale);
        if (value < 0) {
            encoded = (1 << width) + encoded;
        }
        return encoded;
    }
}
