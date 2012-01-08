package de.ronnyfriedland.time.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author ronnyfriedland
 */
public class Configurator {

    public static CompositeConfiguration CONFIG = new CompositeConfiguration();
    static {
        try {
            CONFIG.addConfiguration(new PropertiesConfiguration(Thread.currentThread().getContextClassLoader()
                    .getResource("application.properties")));
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public enum ConfiguratorKeys {
        /** Pfad zur Export-Datei */
        PATH("timetable.path"),
        /** Name der Export-Datei */
        EXPORT_FILE("timetable.export.file"),
        /** Cron-Ausdruck für Popup */
        CRON_EXPRESSION_POPUP("timetable.popup.cron");

        private final String key;

        private ConfiguratorKeys(final String aKey) {
            this.key = aKey;
        }

        public String getKey() {
            return key;
        }
    }
}
