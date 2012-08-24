package de.ronnyfriedland.time.logic.plugin.jmx;

import java.util.Collection;

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
     */
    public Collection<String> getConfiguration();

    /**
     * Ausführen von GC
     */
    public void gc();
}
