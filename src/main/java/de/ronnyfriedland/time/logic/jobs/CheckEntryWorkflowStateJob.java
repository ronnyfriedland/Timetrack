package de.ronnyfriedland.time.logic.jobs;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.config.Messages;
import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.EntryState.State;
import de.ronnyfriedland.time.logic.EntityController;

/**
 * Scheduler-Job für die Prüfung des Status.
 * 
 * @author Ronny Friedland
 */
@DisallowConcurrentExecution
public class CheckEntryWorkflowStateJob extends AbstractJob {

    public final static String JOB = "checkentryworkflowstatejob";
    public final static String TRIGGER = "checkentryworkflowstate";

    private static final Logger LOG = Logger.getLogger(CheckEntryWorkflowStateJob.class.getName());

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
        // 1. Statustabelle abfragen, ob es einen Eintrag im Status 'OK' gibt,
        // dessen lastModifiedDate < als x ist.
        // 2. Status auf WARN stellen
        // 3. Wenn lastModifiedDate < y ist, dann Hinweis bringen
        // 4. Wenn Status auf WARN steht und lastModifiedDate < y, dann Status
        // auf STOPPED stellen und Hinweis bringen

        Integer x = Configurator.CONFIG.getInt(ConfiguratorKeys.WORKFLOW_WARN.getKey());
        Integer y = Configurator.CONFIG.getInt(ConfiguratorKeys.WORKFLOW_STOP.getKey());

        Boolean showPopup = Configurator.CONFIG.getBoolean(ConfiguratorKeys.SHOW_POPUP.getKey());

        Map<String, Object> params = new HashMap<String, Object>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -y);
        params.clear();
        params.put(EntryState.PARAM_DATE, cal.getTime());
        params.put(EntryState.PARAM_STATE, State.WARN);

        Collection<String> stop = getEntryStatesInStateWarnAndStop(params);

        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -x);
        params.clear();
        params.put(EntryState.PARAM_DATE, cal.getTime());
        params.put(EntryState.PARAM_STATE, State.OK);
        Collection<String> warn = setEntryStatesToWarn(params);

        // show entries in state WARN
        if (!warn.isEmpty()) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Show warn popup ... ");
            }
            String messageText = String.format("%1$s\n%2$s\n", Messages.WORKFLOW_WARN.getMessage(),
                    StringUtils.join(warn, "\n"));
            showPopup(showPopup, messageText);
        }
        // show stopped entries
        if (!stop.isEmpty()) {
            if (LOG.isLoggable(Level.INFO)) {
                LOG.info("Show stop popup ... ");
            }
            String messageText = String.format("%1$s\n%2$s\n", Messages.WORKFLOW_FINISH.getMessage(),
                    StringUtils.join(stop, "\n"));
            showPopup(showPopup, messageText);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.ronnyfriedland.time.logic.jobs.AbstractJob#runOnShutdown()
     */
    @Override
    public boolean runOnShutdown() {
        return false;
    }

    private Collection<String> getEntryStatesInStateWarnAndStop(Map<String, Object> params) {
        Set<String> entries = new HashSet<String>();
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_STARTDATE, params);
        for (EntryState entryState : entryStates) {
            entryState.setEnd(Calendar.getInstance().getTime());
            entryState.setState(State.STOPPED);
            EntityController.getInstance().update(entryState);

            params = new HashMap<String, Object>();
            params.put(Entry.PARAM_STATE, entryState);
            Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                    Entry.QUERY_FIND_BY_STATE, params);
            entry.setDuration(EntryState.getDuration(entryState.getStart(), entryState.getEnd(), entry.getDuration()));

            EntityController.getInstance().update(entry);

            entries.add(entry.getDescription());
        }
        return entries;
    }

    private Collection<String> setEntryStatesToWarn(final Map<String, Object> params) {
        Set<String> entries = new HashSet<String>();
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_STARTDATE, params);
        for (EntryState entryState : entryStates) {
            entryState.setState(State.WARN);
            EntityController.getInstance().update(entryState);
            // get entry of entrystate and add description to result
            Map<String, Object> entryParams = new HashMap<String, Object>();
            entryParams.put(Entry.PARAM_STATE, entryState);
            Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                    Entry.QUERY_FIND_BY_STATE, entryParams);
            entries.add(entry.getDescription());
        }
        return entries;
    }
}
