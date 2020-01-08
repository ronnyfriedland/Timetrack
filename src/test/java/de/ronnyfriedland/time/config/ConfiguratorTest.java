package de.ronnyfriedland.time.config;

import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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
    @Ignore
    public void testSystemConfiguration() {
        for (String value : new String[] { "false", "true" }) {
            System.setProperty(ConfiguratorKeys.SHOW_POPUP.getKey(), value);
            Assert.assertEquals(Boolean.valueOf(value),
                    Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey()));
        }
    }
}
