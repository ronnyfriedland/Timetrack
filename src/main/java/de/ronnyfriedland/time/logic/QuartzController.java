/**
 * 
 */
package de.ronnyfriedland.time.logic;

import java.util.HashSet;
import java.util.Set;

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

    private void initScheduler(String message) {
        try {
            sched = StdSchedulerFactory.getDefaultScheduler();
            for (Class<? extends Job> clazz : JOBS) {
                String jobname = clazz.getDeclaredField("JOB").getName();
                String groupname = clazz.getDeclaredField("GROUP").getName();
                String triggername = clazz.getDeclaredField("TRIGGER").getName();

                // Define job instance
                JobDetail job = new JobDetail(jobname, groupname, clazz);

                // Define a Trigger that will fire "now"
                Trigger trigger = new CronTrigger(triggername, groupname, message);

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
            ex.printStackTrace();
        }
    }
}
