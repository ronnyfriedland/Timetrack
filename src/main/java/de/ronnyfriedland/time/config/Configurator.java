package de.ronnyfriedland.time.config;

import org.apache.commons.configuration2.CombinedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * Zentrale Konfigurationsklasse.
 *
 * @author Ronny Friedland
 */
public final class Configurator {

    /** the configuration of the application */
    public static final CombinedConfiguration CONFIG = new CombinedConfiguration();
    static {
        try {
            PropertiesConfiguration config = new Configurations().properties(Thread.currentThread().getContextClassLoader().getResource("application.properties"));

            CONFIG.addConfiguration(new SystemConfiguration());
            CONFIG.addConfiguration(config);
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Konfigurationsparameter
     */
    public enum ConfiguratorKeys {
        /** Pfad zur Export-Datei */
        PATH("timetrack.path"),
        /** Name der Export-Datei */
        EXPORT_FILE("timetrack.export.file"),
        /** Cron-Ausdruck für Popup */
        CRON_EXPRESSION_POPUP("timetrack.popup.cron"),
        /** Cron-Ausdruck für Entry-Workflow */
        CRON_EXPRESSION_ENTRYWORKFLOW("timetrack.entryworkflow.cron"),
        /** Cron-Ausdruck für Entry-Workflow */
        CRON_EXPRESSION_PROTOCOLWRITER("timetrack.protocolwriter.cron"),
        /**
         * Flag, ob Popup anstelle von "Nachrichtenblase" dargestellt werden soll
         */
        SHOW_POPUP("timetrack.popup.show"),
        /** Dauer bis Eintrag im Workflow in Status WARN geht */
        WORKFLOW_WARN("timetrack.workflow.warn"),
        /** Dauer bis Eintrag im Workflow in Status STOPPED geht */
        WORKFLOW_STOP("timetrack.workflow.stop");

        private final String key;

        ConfiguratorKeys(final String aKey) {
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
