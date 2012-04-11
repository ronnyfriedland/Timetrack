package de.ronnyfriedland.time.logic;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
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

import de.ronnyfriedland.time.logic.jobs.ShowMessagePopupJob;

/**
 * Controller für die Scheduler-Steuerung.
 * 
 * @author Ronny Friedland
 */
public final class QuartzController {

    private static final Logger LOG = Logger.getLogger(QuartzController.class.getName());
    private static final Set<Class<? extends Job>> JOBS = new HashSet<Class<? extends Job>>();
    static {
        JOBS.add(ShowMessagePopupJob.class);
    }
    private static QuartzController instance;

    /**
     * Liefert eine Instanz von {@link EntityController}.
     * 
     * @return the {@link EntityController}
     */
    public static QuartzController getInstance() {
        if (null == instance) {
            instance = new QuartzController();
        }
        return instance;
    }

    private QuartzController() {
    }

    private Scheduler sched;

    /**
     * Initialisiert den Scheduler.
     * 
     * @param cronExpression Cron-Ausdruck für Trigger
     * @throws SchedulerException Fehler beim Initialisieren des Schedulers
     * @throws ParseException Fehler beim Parsen des Cron-Ausdrucks
     */
    public void initScheduler(final String cronExpression) throws SchedulerException, ParseException {
        sched = StdSchedulerFactory.getDefaultScheduler();
        for (Class<? extends Job> clazz : JOBS) {
            try {
                String jobname = (String) clazz.getDeclaredField("JOB").get(null);
                String triggername = (String) clazz.getDeclaredField("TRIGGER").get(null);
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine(String.format("Scheduling job %s.", jobname));
                }

                JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobname).build();

                Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggername).forJob(jobname)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();

                sched.scheduleJob(job, trigger);
                sched.start();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Error creting scheduler.", ex);
            }
        }
    }

    /**
     * Beendet den Scheduler.
     * 
     * @throws SchedulerException Fehler während des Shutdown
     */
    public void shutdownScheduler() throws SchedulerException {
        sched.shutdown();
    }
}
