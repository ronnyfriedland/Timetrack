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
        Assert.assertEquals("timetable-export.xlsx", file);
    }

}
