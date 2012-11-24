package de.ronnyfriedland.time.logic;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.Project;

public class ExportControllerTest {

    private ExportController controller;
    private Collection<Entry> entries;

    private final String path = Configurator.CONFIG.getString(ConfiguratorKeys.PATH.getKey());
    private final String file = Configurator.CONFIG.getString(ConfiguratorKeys.EXPORT_FILE.getKey());

    @Before
    public void setUp() {
        controller = new ExportController();
        entries = new HashSet<Entry>();

        Project p = new Project();
        p.setDescription("Testprojekt für den Unit-Test");
        p.setName("TestProjekt");
        for (int i = 0; i < 10; i++) {
            Entry e = new Entry();
            e.setDescription("lalala" + i);
            e.setDuration(String.valueOf(i));
            e.setProject(p);
            e.setDateString("01.01.2011");
            p.addEntry(e);
            entries.add(e);
        }
    }

    @After
    public void tearDown() {
        new File(file).delete();
    }

    @Test
    public void testLoadOrCreateWorkbook() {
        Assert.assertFalse(new File(file).exists());

        Workbook wb = null;
        try {
            wb = controller.loadOrCreateWorkbook(path, file);
            controller.persistWorkbook(wb, path, file);
        } catch (IOException e) {
            Assert.fail("No exception expected : " + e);
        }

        Assert.assertNotNull(wb);
        Assert.assertTrue(new File(path, file).exists());
    }

    @Test
    public void testLoadOrCreateSheet() {
        Workbook wb = null;
        Sheet sheet1 = null;
        Sheet sheet2 = null;
        Sheet sheet3 = null;
        try {
            wb = controller.loadOrCreateWorkbook(path, file);
            sheet1 = controller.loadOrCreateSheet(wb, "test-1", entries);
            controller.persistWorkbook(wb, path, file);
            wb = controller.loadOrCreateWorkbook(path, file);
            sheet2 = controller.loadOrCreateSheet(wb, "test-2", entries);
            controller.persistWorkbook(wb, path, file);
            wb = controller.loadOrCreateWorkbook(path, file);
            sheet3 = controller.loadOrCreateSheet(wb, "test-3", entries);
            controller.persistWorkbook(wb, path, file);
        } catch (IOException e) {
            Assert.fail("No exception expected : " + e);
        }
        Assert.assertNotNull(sheet1);
        Assert.assertNotNull(sheet2);
        Assert.assertNotNull(sheet3);
    }

    @Test
    public void testAddSheetToOverview() {
        Workbook wb = null;
        Sheet sheet1 = null;
        try {
            wb = controller.loadOrCreateWorkbook(path, file);
            sheet1 = controller.loadOrCreateSheet(wb, "test-1", entries);
            controller.addSheetToOverview(wb, "test-1");
            controller.persistWorkbook(wb, path, file);
        } catch (IOException e) {
            Assert.fail("No exception expected : " + e);
        }

        Assert.assertNotNull(wb);
        Assert.assertNotNull(sheet1);
    }
}
