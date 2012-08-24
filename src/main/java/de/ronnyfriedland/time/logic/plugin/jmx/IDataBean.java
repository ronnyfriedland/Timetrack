package de.ronnyfriedland.time.logic.plugin.jmx;

import java.util.Collection;

/**
 * Interface Definition für die Darstellung der Anwendungsdaten.
 * 
 * @author Ronny Friedland
 */
public interface IDataBean {
    public Collection<String> getActiveWorkflowEntries();

    public int getEntryCount();

    public int getProjectCount();
}
