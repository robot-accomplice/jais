package jais.messages;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AISMessageTest {

    @Test
    public void testDecodeDestination() {
        String[] destinations = {
                "USNYC>NLRTM",
                "USCIR>USMSY>USCIR",
                "USHOU<>USHOU",
                "US^0Y0P><0Q6L",
                "US^OX6M>OWYY>OX6M",
                "US^0NVR<<",
                "CNSHA>US^0VCY"
        };

        String[] expected = {
                "From UN/LOCODE US, Port NYC, travel to: UN/LOCODE NL, Port RTM",
                "From UN/LOCODE US, Port CIR, travel to: UN/LOCODE US, Port MSY, travel to: UN/LOCODE US, Port CIR, to complete round trip",
                "From UN/LOCODE US, Port HOU: operate within the area of UN/LOCODE US, Port HOU",
                "From UN/LOCODE US, Port 0Y0P: perform scheduled route to Port 0Q6L",
                "From UN/LOCODE US, Port OX6M, travel to: Port OWYY, travel to: Port OX6M, to complete round trip",
                "From UN/LOCODE US, Port 0NVR: remain anchored/moored",
                "From UN/LOCODE CN, Port SHA, travel to: UN/LOCODE US, Port 0VCY"
        };

        int i = 0;
        for (String d : destinations) {
            List<String> movements = AISMessage.decodeDestination(d);
            StringBuilder decoded = new StringBuilder();
            for(String m: movements) {
                decoded.append(m);
            }
            assertEquals(decoded.toString(), expected[i]);
            System.out.printf("%17s = %s\n", d, decoded);
            i++;
        }
    }
}
