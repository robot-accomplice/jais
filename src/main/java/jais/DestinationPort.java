package jais;

import jais.messages.enums.PortAction;

public record DestinationPort(String UNLOCODE, String portID, PortAction action) {

    public String getUNLOCODE() { return this.UNLOCODE; }

    public String getPortID() { return this.portID; }

    public PortAction getAction() { return this.action; }
}
