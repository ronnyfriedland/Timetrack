package de.ronnyfriedland.time.logic;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import de.ronnyfriedland.time.logic.jobs.AbstractJob;

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
     * @throws SchedulerException Fehler beim Initialisieren des Schedulers
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
        StdSchedulerFactory schdFact = new StdSchedulerFactory();
        schdFact.initialize(Thread.currentThread().getContextClassLoader().getResourceAsStream(("quartz.properties")));
        sched = schdFact.getScheduler();
        sched.start();
    }

    private final Scheduler sched;

    /**
     * Initialisiert den Scheduler.
     * 
     * @param jobClazz Jobklasse
     * @param cronExpression Cron-Ausdruck für Trigger
     * @param jobData optional data
     */
    public void initScheduler(final Class<? extends AbstractJob> jobClazz, final String cronExpression,
            final Map<? extends String, ? extends Object> jobData) {
        try {
            String jobname = (String) jobClazz.getDeclaredField("JOB").get(null);
            String triggername = (String) jobClazz.getDeclaredField("TRIGGER").get(null);
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("Scheduling job %s.", jobname));
            }

            JobDetail job = JobBuilder.newJob(jobClazz).withIdentity(jobname).build();
            if (null != jobData) {
                job.getJobDataMap().putAll(jobData);
            }

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggername).forJob(jobname)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

            sched.scheduleJob(job, trigger);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Error creating scheduler.", ex);
        }
    }

    /**
     * Beendet den Scheduler.
     * 
     * @throws SchedulerException Fehler während des Shutdown
     */
    public void shutdownScheduler() throws SchedulerException {
        sched.shutdown(true);
    }
}
