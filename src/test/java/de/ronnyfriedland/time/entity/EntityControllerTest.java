package de.ronnyfriedland.time.entity;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;

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
        Collection<Entry> entries = controller.findAll(Entry.class, false);
        for (Entry entry : entries) {
            controller.delete(entry);
        }
        Collection<Project> projects = controller.findAll(Project.class, false);
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
        entry.setState(new EntryState(new Date()));

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

    @Test
    public void testProjectsOrder() {
        Project p1 = new Project();
        p1.setName("Zum Anfang ein Projekt");
        p1.setDescription("Testbeschreibung");
        controller.create(p1);

        Project p2 = new Project();
        p2.setName("Alles nur ein Test");
        p2.setDescription("Testbeschreibung");
        controller.create(p2);

        SortParam sortParam = new SortParam(Project.PARAM_NAME, SortOrder.ASC);
        Collection<Project> projects = controller.findAll(Project.class, sortParam, false);

        Assert.assertEquals(2, projects.size());

        int i = 0;
        for (Project project : projects) {
            if (i == 0) {
                Assert.assertEquals(p2.getName(), project.getName());
            }
            if (i == 1) {
                Assert.assertEquals(p1.getName(), project.getName());
            }
            i++;
        }
    }

    @Test
    public void testDurationCalculation() {
        Calendar start = Calendar.getInstance();
        Calendar stop = Calendar.getInstance();
        stop.add(Calendar.MINUTE, 30);
        String duration = EntryState.getDuration(start.getTime(), stop.getTime(), 0);
        Assert.assertEquals(String.format("%.2f", 0.5), duration);
        stop.add(Calendar.MINUTE, 15);
        duration = EntryState.getDuration(start.getTime(), stop.getTime(), 0);
        Assert.assertEquals(String.format("%.2f", 0.75), duration);
        duration = EntryState.getDuration(start.getTime(), stop.getTime(), "0.25");
        Assert.assertEquals(String.format("%.2f", 1f), duration);
    }
}
