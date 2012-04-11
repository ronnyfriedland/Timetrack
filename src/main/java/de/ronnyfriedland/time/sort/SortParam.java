package de.ronnyfriedland.time.sort;

/**
 * Parameter für die Sortierung.
 * 
 * @author Ronny Friedland
 */
public class SortParam {

    private final String attribute;
    private final SortOrder order;

    public SortParam(final String attr, final SortOrder ord) {
        this.attribute = attr;
        this.order = ord;
    }

    public String getAttribute() {
        return attribute;
    }

    public SortOrder getOrder() {
        return order;
    }

    /**
     * Definiert Absteigende oder Aufsteigende Sortierung.
     */
    public enum SortOrder {
        /** aufsteigend sortieren */
        ASC,
        /** absteigend sortieren */
        DESC;
    }
}
