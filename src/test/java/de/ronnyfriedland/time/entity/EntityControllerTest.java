package de.ronnyfriedland.time.entity;

import java.util.Collection;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.ronnyfriedland.time.logic.EntityController;

/**
 * @author ronnyfriedland
 * 
 */
public class EntityControllerTest {

    private static Logger LOG = Logger.getLogger(EntityControllerTest.class.getName());

    private EntityController controller;

    @Before
    public void setUp() throws Exception {
        controller = EntityController.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        Collection<Entry> entries = controller.findAll(Entry.class);
        for (Entry entry : entries) {
            controller.delete(entry);
        }
        Collection<Project> projects = controller.findAll(Project.class);
        for (Project project : projects) {
            controller.delete(project);
        }
    }

    @Test
    public void testCreateProject() {
        Project p1 = new Project();
        p1.setName("Testprojekt");
        p1.setDescription("Das ist die Beschreibung für das Testprojekt");

        controller.create(p1);

        Project p2 = controller.findById(Project.class, p1.getUuid());

        Assert.assertEquals(p1.getUuid(), p2.getUuid());
        Assert.assertTrue(p2.getEntries().isEmpty());

        LOG.info("P1: " + p1);
        LOG.info("P2: " + p2);

        Entry entry = new Entry();
        entry.setDuration("2.2");
        entry.setDescription("Testbeschreibung");

        p1.addEntry(entry);

        p2 = controller.findById(Project.class, p1.getUuid());
        Assert.assertFalse(p2.getEntries().isEmpty());

        LOG.info("P1: " + p1);
        LOG.info("P2: " + p2);
    }

    @Test
    public void testUniqueConstraint() {
        Project p1 = new Project();
        p1.setName("Testprojekt");
        p1.setDescription("Das ist die Beschreibung für das Testprojekt");

        controller.create(p1);

        Project p2 = new Project();
        p2.setName("Testprojekt");
        p2.setDescription("Das ist die Beschreibung für ein weiteres Testprojekt mit gleichem Namen");

        try {
            controller.create(p2);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }
}
