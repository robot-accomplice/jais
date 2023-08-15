package jais.messages;

import jais.DestinationPort;
import jais.messages.enums.PortAction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

        ArrayList<DestinationPort> simple = new ArrayList<>();
        simple.add(new DestinationPort("US", "NYC", PortAction.TRAVEL_TO));
        simple.add(new DestinationPort("NL", "RTM", PortAction.NONE));

        ArrayList<DestinationPort> roundTrip = new ArrayList<>();
        roundTrip.add(new DestinationPort("US", "CIR", PortAction.TRAVEL_TO));
        roundTrip.add(new DestinationPort("US", "MSY", PortAction.TRAVEL_TO));
        roundTrip.add(new DestinationPort("US", "CIR", PortAction.NONE));

        ArrayList<DestinationPort> inArea = new ArrayList<>();
        inArea.add(new DestinationPort("US", "HOU", PortAction.OPERATE_WITHIN_AREA_OF));
        inArea.add(new DestinationPort("US", "HOU", PortAction.NONE));

        ArrayList<DestinationPort> scheduled = new ArrayList<>();
        scheduled.add(new DestinationPort("US", "0Y0P", PortAction.PERFORM_SCHEDULED_ROUTE));
        scheduled.add(new DestinationPort(null, "0Q6L", PortAction.NONE));

        ArrayList<DestinationPort> guidRoundTrip = new ArrayList<>();
        guidRoundTrip.add(new DestinationPort("US", "OX6M", PortAction.TRAVEL_TO));
        guidRoundTrip.add(new DestinationPort(null, "OWYY", PortAction.TRAVEL_TO));
        guidRoundTrip.add(new DestinationPort(null, "OX6M", PortAction.NONE));

        ArrayList<DestinationPort> anchored = new ArrayList<>();
        anchored.add(new DestinationPort("US", "0NVR", PortAction.ANCHORED_MOORED));

        ArrayList<DestinationPort> mixed = new ArrayList<>();
        mixed.add(new DestinationPort("CN", "SHA", PortAction.TRAVEL_TO));
        mixed.add(new DestinationPort("US", "0VCY", PortAction.NONE));

        ArrayList<DestinationPort>[] want = new ArrayList[]{simple, roundTrip, inArea, scheduled, guidRoundTrip, anchored, mixed};

        for (int i = 0; i < destinations.length; i++) {
            System.out.println("Decoding: " + destinations[i]);
            List<DestinationPort> got = AISMessage.decodeDestination(destinations[i]);
            assertEquals(want[i].size(), got.size());
            for (int j = 0; j < got.size(); j++) {
                assertEquals(want[i].get(j).getUNLOCODE(), got.get(j).UNLOCODE());
                assertEquals(want[i].get(j).getPortID(), got.get(j).getPortID());
                assertEquals(want[i].get(j).getAction(), got.get(j).getAction());
            }
        }
    }
}
