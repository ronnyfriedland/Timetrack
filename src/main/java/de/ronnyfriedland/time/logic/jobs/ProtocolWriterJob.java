package de.ronnyfriedland.time.logic.jobs;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.entity.Protocol;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.ProtocolController;

/**
 * Persistiert die vorhandenen {@link Protocol} Einträge.
 * 
 * @author Ronny Friedland
 */
@DisallowConcurrentExecution
public class ProtocolWriterJob extends AbstractJob {
    /** the name of the job */
    public final static String JOB = "protocolwriterjob";
    /** the trigger of the job */
    public final static String TRIGGER = "protocolwritertrigger";
    /** the logger for {@link ProtocolWriterJob} */
    private final static Logger LOG = Logger.getLogger(ProtocolWriterJob.class.getName());

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

        Set<Protocol> protocols = ProtocolController.getInstance().getProtocols();
        if ((null != protocols) && (0 < protocols.size())) {
            // persist
            EntityController.getInstance().create(protocols);
            // delete
            ProtocolController.getInstance().removeProtocol(protocols.toArray(new Protocol[protocols.size()]));
        }

        if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(String.format("Job %s executed successfully.", context.getJobDetail().getKey()));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.ronnyfriedland.time.logic.jobs.AbstractJob#runOnShutdown()
     */
    @Override
    public boolean runOnShutdown() {
        return true;
    }
}