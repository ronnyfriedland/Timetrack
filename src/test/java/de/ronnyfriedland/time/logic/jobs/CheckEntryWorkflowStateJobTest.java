package de.ronnyfriedland.time.logic.jobs;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.EntryState.State;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;

public class CheckEntryWorkflowStateJobTest {
    private EntityController entityController;
    private Project project;
    private Entry entry;
    private EntryState state;

    class MockPopupJob extends CheckEntryWorkflowStateJob {
        private boolean show = false;

        public boolean isShow() {
            return show;
        }

        @Override
        public void showPopup(boolean show, String text) {
            this.show = true;
        }
    }

    @Before
    public void setUp() throws Exception {
        entityController = EntityController.getInstance();
        project = new Project();
        project.setDescription("test");
        project.setName("test");
        entityController.create(project);

        state = new EntryState();
        state.setStart(new Date(0));
        state.setState(State.OK);

        entry = new Entry();
        entry.setDate(new Date());
        entry.setDescription("test");
        entry.setDuration("1");
        entry.setProject(project);
        entry.setState(state);
        entityController.create(entry);
    }

    @After
    public void tearDown() throws Exception {
        entityController.deleteDetached(entry);
        entityController.deleteDetached(project);
    }

    @Test
    public void testJobExecution() throws Exception {
        MockContext ctx = new MockContext();

        MockPopupJob job1 = new MockPopupJob();
        job1.execute(ctx);
        Assert.assertTrue(job1.isShow());

        Entry entryInStateWarn = entityController.findById(Entry.class, entry.getUuid());
        Assert.assertEquals(State.WARN, entryInStateWarn.getState().getState());

        MockPopupJob job2 = new MockPopupJob();
        job2.execute(ctx);
        Assert.assertTrue(job1.isShow());

        Entry entryInStateStopped = entityController.findById(Entry.class, entry.getUuid());
        Assert.assertEquals(State.STOPPED, entryInStateStopped.getState().getState());

        MockPopupJob job3 = new MockPopupJob();
        job3.execute(ctx);
        Assert.assertFalse(job3.isShow());

    }
}