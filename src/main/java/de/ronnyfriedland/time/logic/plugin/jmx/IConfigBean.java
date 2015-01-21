package de.ronnyfriedland.time.logic.plugin.jmx;

import javax.management.openmbean.CompositeData;

import de.ronnyfriedland.time.logic.plugin.PluginException;

/**
 * Interface Definition für die Darstellung der Anwendungskonfiguration.
 *
 * @author Ronny Friedland
 */
public interface IConfigBean {
    /**
     * Liefert die Konfiguration der Anwendung
     *
     * @return Liste der Konfigurationsparameter
     * @throws PluginException bei einem Fehler
     */
    CompositeData getConfiguration() throws PluginException;

    /**
     * Ausführen von GC
     */
    void gc();
}
