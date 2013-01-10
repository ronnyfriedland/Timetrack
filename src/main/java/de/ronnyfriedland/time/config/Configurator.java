package de.ronnyfriedland.time.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * Zentrale Konfigurationsklasse.
 * 
 * @author Ronny Friedland
 */
public final class Configurator {

    public final static CompositeConfiguration CONFIG = new CompositeConfiguration();
    static {
        try {
            CONFIG.addConfiguration(new SystemConfiguration());
            CONFIG.addConfiguration(new PropertiesConfiguration(Thread.currentThread().getContextClassLoader()
                    .getResource("application.properties")));
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Konfigurationsparameter
     */
    public enum ConfiguratorKeys {
        /** Pfad zur Export-Datei */
        PATH("timetable.path"),
        /** Name der Export-Datei */
        EXPORT_FILE("timetable.export.file"),
        /** Cron-Ausdruck für Popup */
        CRON_EXPRESSION_POPUP("timetable.popup.cron"),
        /** Cron-Ausdruck für Entry-Workflow */
        CRON_EXPRESSION_ENTRYWORKFLOW("timetable.entryworkflow.cron"),
        /**
         * Flag, ob Popup anstelle von "Nachrichtenblase" dargestellt werden
         * soll
         */
        SHOW_POPUP("timetable.popup.show"),
        /** Dauer bis Eintrag im Workflow in Status WARN geht */
        WORKFLOW_WARN("timetable.workflow.warn"),
        /** Dauer bis Eintrag im Workflow in Status STOPPED geht */
        WORKFLOW_STOP("timetable.workflow.stop");

        private final String key;

        private ConfiguratorKeys(final String aKey) {
            this.key = aKey;
        }

        /**
         * Liefert den Schlüssel des Konfigurationsparameters
         * 
         * @return Konfigurationsparameter
         */
        public String getKey() {
            return key;
        }
    }

    private Configurator() {
        // empty
    }
}
