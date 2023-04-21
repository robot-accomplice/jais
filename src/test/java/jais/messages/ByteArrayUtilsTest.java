package jais.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import jais.ByteArrayUtils;

public class ByteArrayUtilsTest {
    
    @Test
    public void testBArray2Str() {
        byte[] bytes = "15NHl8500pqSdR8A7jnq9oRF0<<P".getBytes();

        assertEquals(new String(bytes), ByteArrayUtils.bArray2Str(bytes));
    }

    @Test
    public void testStr2bArray() {
        byte[] bytes = ByteArrayUtils.str2bArray("15NHl8500pqSdR8A7jnq9oRF0<<P");
        for (byte aByte : bytes) {
            System.out.print(aByte + ", ");
        }
        System.out.println();
    }
}
