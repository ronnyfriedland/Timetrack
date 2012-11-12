package de.ronnyfriedland.time.logic.plugin.jmx;

import javax.management.openmbean.CompositeData;

/**
 * Interface Definition für die Darstellung der Anwendungsdaten.
 * 
 * @author Ronny Friedland
 */
public interface IDataBean {
    public CompositeData getActiveWorkflowEntries();

    public int getEntryCount();

    public int getProjectCount();
}
