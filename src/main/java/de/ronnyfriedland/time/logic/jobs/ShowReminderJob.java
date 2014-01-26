package de.ronnyfriedland.time.logic.jobs;

import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.sort.SortParam;
import de.ronnyfriedland.time.sort.SortParam.SortOrder;

/**
 * Scheduler-Job für die Anzeige eines Hinweistextes.
 * 
 * @author Ronny Friedland
 */
@DisallowConcurrentExecution
public class ShowReminderJob extends AbstractJob {
    public final static String JOB = "showpopupjob";
    public final static String TRIGGER = "showpopuptrigger";

    private static final Logger LOG = Logger.getLogger(ShowReminderJob.class.getName());

    /**
     * {@inheritDoc}
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Executing ... " + getClass().getSimpleName());
        }
        Boolean showPopup = Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey());
        Date previousFireTime = context.getPreviousFireTime();

        if (null == previousFireTime) {
            previousFireTime = new Date(0);
        }
        Collection<Entry> entries = EntityController.getInstance().findAll(Entry.class,
                new SortParam("date", SortOrder.DESC), 1, true);
        Entry lastEntry = null;
        if ((null != entries) && (0 < entries.size())) {
            lastEntry = entries.iterator().next();
        }
        if ((null == lastEntry) || (lastEntry.getLastModifiedDate().before(previousFireTime))) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Show popup ... ");
            }
            showPopup(showPopup, Messages.MESSAGE_POPUP.getMessage());
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Job %s executed successfully.", context.getJobDetail().getKey()));
        }
    }
}