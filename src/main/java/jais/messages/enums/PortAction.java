package jais.messages.enums;

import lombok.Getter;

@Getter
public enum PortAction {
    NONE("", "no action specified"),
    TRAVEL_TO(">", "travel to"),
    OPERATE_WITHIN_AREA_OF("<>", "operate within the area of"),
    PERFORM_SCHEDULED_ROUTE("><", "perform scheduled route to"),
    ANCHORED_MOORED("<<", "remain anchored/moored");

    private final String symbol;
    private final String description;

    PortAction(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public static PortAction getForSymbol(String symbol) {
        for (PortAction pa : PortAction.values()) {
            if(pa.symbol.equals(symbol)) return pa;
        }

        return NONE;
    }
}
