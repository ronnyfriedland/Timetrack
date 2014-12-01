package de.ronnyfriedland.time.ui.to;

/**
 * Transfer object for {@link de.ronnyfriedland.time.entity.Entry}.
 * 
 * @author Ronny Friedland
 */
public class EntryData {

    public final transient String entryUuid;
    public final transient String entryDescription;
    public final transient String entryDate;

    /**
     * Creates a new EntryData instance.
     * 
     * @param entryUuid the uuid of the {@link de.ronnyfriedland.time.entity.Entry}
     * @param entryDescription the description of the {@link de.ronnyfriedland.time.entity.Entry}
     * @param entryDate the date of the {@link de.ronnyfriedland.time.entity.Entry}
     */
    public EntryData(final String entryUuid, final String entryDescription, final String entryDate) {
        this.entryUuid = entryUuid;
        this.entryDescription = entryDescription;
        this.entryDate = entryDate;
    }

}
