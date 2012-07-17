package de.ronnyfriedland.time.logic.jobs;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;

public class ShowMessagePopupJobTest {

	private EntityController entityController;
	private Project project;
	private Entry entry;

	class MockContext implements JobExecutionContext {

		private Date previousFireTime;

		@Override
		public Scheduler getScheduler() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Trigger getTrigger() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Calendar getCalendar() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isRecovering() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getRefireCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public JobDataMap getMergedJobDataMap() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public JobDetail getJobDetail() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Job getJobInstance() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getScheduledFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getPreviousFireTime() {
			return previousFireTime;
		}

		public void setPreviousFireTime(Date date) {
			previousFireTime = date;
		}

		@Override
		public Date getNextFireTime() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getFireInstanceId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getResult() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setResult(Object result) {
			// TODO Auto-generated method stub

		}

		@Override
		public long getJobRunTime() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void put(Object key, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object get(Object key) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class MockPopupJob extends ShowMessagePopupJob {
		private boolean show = false;

		@Override
		protected void showPopup(JobExecutionContext context) {
			setShow(true);
		}

		public boolean isShow() {
			return show;
		}

		public void setShow(boolean show) {
			this.show = show;
		}
	}

	@Before
	public void setUp() throws Exception {
		entityController = EntityController.getInstance();
		project = new Project();
		project.setDescription("test");
		project.setName("test");
		entityController.create(project);

		entry = new Entry();
		entry.setDate(new Date());
		entry.setDescription("test");
		entry.setDuration("1");
		entry.setProject(project);
		entry.setState(new EntryState(new Date()));
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

		java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
		ctx.setPreviousFireTime(cal.getTime());

		MockPopupJob job1 = new MockPopupJob();
		job1.execute(ctx);

		Assert.assertTrue(job1.isShow());

		cal.add(java.util.Calendar.DAY_OF_MONTH, -2);
		ctx.setPreviousFireTime(cal.getTime());

		MockPopupJob job2 = new MockPopupJob();
		job2.execute(ctx);

		Assert.assertFalse(job2.isShow());
	}
}
