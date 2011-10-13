package de.ronnyfriedland.time.config;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;

/**
 * @author ronnyfriedland
 */
public class Configurator {

    public static CompositeConfiguration CONFIG = new CompositeConfiguration();
    static {
        try {
            CONFIG.addConfiguration(new SystemConfiguration());
            CONFIG.addConfiguration(new PropertiesConfiguration("application.properties"));
        } catch (ConfigurationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public enum ConfiguratorKeys {
        /** Pfad zur Export-Datei */
        EXPORT_PATH("export.path"),
        /** Name der Export-Datei */
        EXPORT_FILE("export.file");

        private final String key;

        private ConfiguratorKeys(final String aKey) {
            this.key = aKey;
        }

        public String getKey() {
            return key;
        }
    }
}