package de.ronnyfriedland.time.sort;

/**
 * Parameter für die Sortierung.
 * 
 * @author Ronny Friedland
 */
public class SortParam {

    private final String attribute;
    private final SortOrder order;

    /**
     * Erzeugt eine neue {@link SortParam} Instanz.
     * 
     * @param attr
     *            the sort attribute
     * @param ord
     *            the sort order
     */
    public SortParam(final String attr, final SortOrder ord) {
        this.attribute = attr;
        this.order = ord;
    }

    /**
     * Liefert das Suchattribut
     * 
     * @return das Suchattribut
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Die Reihenfolge der Suchergebnisse
     * 
     * @return die Reihenfolge der Suchergebnisse
     */
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
