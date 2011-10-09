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
public class EntityTest {

    private static Logger LOG = Logger.getLogger(EntityTest.class.getName());

    private EntityController controller;

    @Before
    public void setUp() throws Exception {
        controller = EntityController.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        Collection<Project> projects = controller.findAll(Project.class);
        for (Project project : projects) {
            controller.delete(project);
        }
        Collection<Entry> entries = controller.findAll(Entry.class);
        for (Entry entry : entries) {
            controller.delete(entry);
        }
    }

    @Test
    public void testCreateProject() {
        Project p1 = new Project();
        p1.setName("Testprojekt");
        p1.setDescription("Das ist die Beschreibung f�r das Testprojekt");

        controller.create(p1);

        Project p2 = controller.findById(Project.class, p1.getUuid());

        Assert.assertEquals(p1.getUuid(), p2.getUuid());
        Assert.assertTrue(p2.getEntries().isEmpty());

        LOG.info("P1: " + p1);
        LOG.info("P2: " + p2);

        Entry entry = new Entry();
        entry.setDuration(2.2F);
        entry.setDescription("Testbeschreibung");
        entry.setProject(p1);

        p1.addEntry(entry);

        controller.update(p1);

        p2 = controller.findById(Project.class, p1.getUuid());
        Assert.assertFalse(p2.getEntries().isEmpty());

        LOG.info("P1: " + p1);
        LOG.info("P2: " + p2);
    }

    @Test
    public void testUniqueConstraint() {
        Project p1 = new Project();
        p1.setName("Testprojekt");
        p1.setDescription("Das ist die Beschreibung f�r das Testprojekt");

        controller.create(p1);

        Project p2 = new Project();
        p2.setName("Testprojekt");
        p2.setDescription("Das ist die Beschreibung f�r ein weiteres Testprojekt mit gleichem Namen");

        try {
            controller.create(p2);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            // ok
        }
    }
}
