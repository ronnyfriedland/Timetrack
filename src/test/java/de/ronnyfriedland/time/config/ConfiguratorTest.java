package de.ronnyfriedland.time.config;

import junit.framework.Assert;

import org.junit.Test;

import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;

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
        for (String value : new String[] { "true", "false" }) {
            System.setProperty(ConfiguratorKeys.SHOW_POPUP.getKey(), value);
            Assert.assertEquals(Boolean.valueOf(value).booleanValue(),
                    Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey()));
        }
    }
}
