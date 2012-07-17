package de.ronnyfriedland.time.logic;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Controller für die Scheduler-Steuerung.
 * 
 * @author Ronny Friedland
 */
public final class QuartzController {

	private static final Logger LOG = Logger.getLogger(QuartzController.class.getName());

	private static QuartzController instance;

	/**
	 * Liefert eine Instanz von {@link EntityController}.
	 * 
	 * @return the {@link EntityController}
	 * @throws SchedulerException
	 *             Fehler beim Initialisieren des Schedulers
	 */
	public static QuartzController getInstance() throws SchedulerException {
		synchronized (QuartzController.class) {
			if (null == instance) {
				instance = new QuartzController();
			}
		}
		return instance;
	}

	private QuartzController() throws SchedulerException {
		StdSchedulerFactory schdFact = new StdSchedulerFactory(Thread.currentThread().getContextClassLoader()
		        .getResource("quartz.properties").getFile());
		sched = schdFact.getScheduler();
	}

	private final Scheduler sched;

	/**
	 * Initialisiert den Scheduler.
	 * 
	 * @param jobClazz
	 *            Jobklasse
	 * @param cronExpression
	 *            Cron-Ausdruck für Trigger
	 */
	public void initScheduler(final Class<? extends Job> jobClazz, final String cronExpression) {
		try {
			String jobname = (String) jobClazz.getDeclaredField("JOB").get(null);
			String triggername = (String) jobClazz.getDeclaredField("TRIGGER").get(null);
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Scheduling job %s.", jobname));
			}

			JobDetail job = JobBuilder.newJob(jobClazz).withIdentity(jobname).build();

			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggername).forJob(jobname)
			        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

			sched.scheduleJob(job, trigger);
			if (!sched.isStarted()) {
				sched.start();
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Error creating scheduler.", ex);
		}
	}

	/**
	 * Beendet den Scheduler.
	 * 
	 * @throws SchedulerException
	 *             Fehler während des Shutdown
	 */
	public void shutdownScheduler() throws SchedulerException {
		sched.shutdown();
	}
}
