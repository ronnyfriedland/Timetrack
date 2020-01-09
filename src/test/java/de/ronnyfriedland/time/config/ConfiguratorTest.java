package de.ronnyfriedland.time.config;

import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.MapConfiguration;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

public class ConfiguratorTest {

    @Test
    public void testExportPath() {
        String path = Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey());
        Assert.assertNotNull(path);
        Assert.assertEquals("target", path);
    }

    @Test
    public void testExportFile() {
        String file = Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey());
        Assert.assertNotNull(file);
        Assert.assertEquals("timetrack-export.xlsx", file);
    }

    @Test
    public void testSystemConfiguration() {
        List<Configuration> configurations = Configurator.CONFIG.getConfigurations();
        try {

            for (String value : new String[]{"false", "true"}) {
                Properties props = new Properties();
                props.put(ConfiguratorKeys.SHOW_POPUP.getKey(), value);

                Configurator.CONFIG.clear();
                Configurator.CONFIG.addConfiguration(new MapConfiguration(props), "test");

                Assert.assertEquals(Boolean.valueOf(value),
                        Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey()));
            }
        } finally {
            configurations.forEach(Configurator.CONFIG::addConfiguration);
        }
    }
}