/**
 * 
 */
package de.ronnyfriedland.time.logic;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.logic.jobs.ShowMessagePopupJob;

/**
 * @author ronnyfriedland
 */
public class QuartzController {

    private static final Logger LOG = Logger.getLogger(QuartzController.class.getName());

    private static QuartzController INSTANCE;

    private final Set<Class<? extends Job>> JOBS = new HashSet<Class<? extends Job>>();
    {
        JOBS.add(ShowMessagePopupJob.class);
    }

    public static QuartzController getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new QuartzController(
                    Configurator.CONFIG.getString(Configurator.ConfiguratorKeys.CRON_EXPRESSION_POPUP.getKey()));
        }
        return INSTANCE;
    }

    private QuartzController(String message) {
        initScheduler(message);
    }

    private Scheduler sched;

    private void initScheduler(String cronExpression) {
        try {
            sched = StdSchedulerFactory.getDefaultScheduler();
            for (Class<? extends Job> clazz : JOBS) {
                String jobname = clazz.getDeclaredField("JOB").getName();
                String groupname = clazz.getDeclaredField("GROUP").getName();
                String triggername = clazz.getDeclaredField("TRIGGER").getName();
                if (LOG.isLoggable(Level.FINE)) {
                    LOG.fine(String.format("Scheduling job %s.", jobname));
                }
                JobDetail job = new JobDetail(jobname, groupname, clazz);
                Trigger trigger = new CronTrigger(triggername, groupname, cronExpression);
                // Tell quartz to schedule the job using our trigger
                sched.scheduleJob(job, trigger);
            }
            sched.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdownScheduler() {
        try {
            sched.shutdown();
        } catch (SchedulerException ex) {
            LOG.log(Level.SEVERE, "Error on scheduler shutdown.", ex);
        }
    }
}
