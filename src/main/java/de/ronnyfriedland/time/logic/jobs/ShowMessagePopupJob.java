package de.ronnyfriedland.time.logic.jobs;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.logic.EntityController;

/**
 * @author ronnyfriedland
 */
@DisallowConcurrentExecution
public class ShowMessagePopupJob implements Job {

    public final static String JOB = "showpopupjob";
    public final static String TRIGGER = "showpopuptrigger";

    private static final Logger LOG = Logger.getLogger(ShowMessagePopupJob.class.getName());

    /**
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Date previousFireTime = context.getPreviousFireTime();
        Date now = new Date();
        Entry lastEntry = EntityController.getInstance().findLast(Entry.class);

        boolean showPopup = true;
        long diff = Long.MAX_VALUE;
        if (null != previousFireTime) {
            diff = now.getTime() - previousFireTime.getTime();
        }
        if (null != lastEntry) {
            if (diff > now.getTime() - lastEntry.getLastModifiedDate().getTime()) {
                showPopup = false;
            }
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
    protected void showPopup(JobExecutionContext context) {
        JOptionPane.showMessageDialog(null, Messages.MESSAGE_POPUP.getText());
    }
}
