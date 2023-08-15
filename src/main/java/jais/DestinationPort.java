package jais;

import jais.messages.enums.PortAction;
import lombok.Getter;

@Getter
public record DestinationPort(String UNLOCODE, String portID, PortAction action) {
}
