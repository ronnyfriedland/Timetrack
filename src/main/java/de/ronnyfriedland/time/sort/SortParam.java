package de.ronnyfriedland.time.sort;

/**
 * @author ronnyfriedland
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

    public enum SortOrder {
        /** aufsteigend sortieren */
        ASC,
        /** absteigend sortieren */
        DESC;
    }
}
