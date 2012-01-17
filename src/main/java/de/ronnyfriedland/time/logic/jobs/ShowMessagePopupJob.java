package de.ronnyfriedland.time.logic.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.config.Messages;

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
        JOptionPane.showMessageDialog(null, Messages.MESSAGE_POPUP.getText());
        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Job %s executed successfully.", context.getJobDetail().getKey()));
        }
    }
}
