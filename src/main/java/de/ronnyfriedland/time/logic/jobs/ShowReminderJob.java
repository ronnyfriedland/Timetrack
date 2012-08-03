package de.ronnyfriedland.time.logic.jobs;

import java.awt.TrayIcon.MessageType;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;

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
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Executing ... " + getClass().getSimpleName());
        }
        Date previousFireTime = context.getPreviousFireTime();
        Date now = new Date();
        Entry lastEntry = EntityController.getInstance().findLast(Entry.class);

        boolean showPopup = true;
        long diff = Long.MAX_VALUE;
        if (null != previousFireTime) {
            diff = now.getTime() - previousFireTime.getTime();
        }
        if (null != lastEntry && (diff > (now.getTime() - lastEntry.getLastModifiedDate().getTime()))) {
            showPopup = false;
        }
        if (showPopup) {
            showPopup(context);
        }
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Job %s executed successfully.", context.getJobDetail().getKey()));
        }
    }

    /**
     * Darstellung des Popups
     * 
     * @param context
     *            der {@link JobExecutionContext}
     */
    protected void showPopup(final JobExecutionContext context) {
        Boolean showPopup = Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey());
        if (showPopup || null == getTrayIcon()) {
            JOptionPane.showMessageDialog(null, Messages.MESSAGE_POPUP.getMessage());
        } else {
            getTrayIcon().displayMessage(null, Messages.MESSAGE_POPUP.getMessage(), MessageType.INFO);
        }
    }
}
