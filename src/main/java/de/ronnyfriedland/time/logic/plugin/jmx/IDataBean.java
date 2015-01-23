package de.ronnyfriedland.time.logic.plugin.jmx;

import javax.management.openmbean.CompositeData;

import de.ronnyfriedland.time.logic.plugin.PluginException;

/**
 * Interface Definition für die Darstellung der Anwendungsdaten.
 *
 * @author Ronny Friedland
 */
public interface IDataBean {
    /**
     * Liefert die aktiven Workflows für Einträge
     *
     * @return Liste der Einträge
     * @throws PluginException bei einem Fehler
     */
    CompositeData getActiveWorkflowEntries() throws PluginException;

    /**
     * Liefert die Anzahl der gespeicherten Einträge
     *
     * @return Anzahl der Einträge
     */
    int getEntryCount();

    /**
     * Liefert die Anzahl der gespeicherten Projekte
     *
     * @return Anzahl der Projekte
     */
    int getProjectCount();
}
