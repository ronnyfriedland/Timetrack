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

    /** the name of the job */
    public static final String JOB = "checkentryworkflowstatejob";
    /** the name of the trigger */
    public static final String TRIGGER = "checkentryworkflowstate";
    /** the logger for {@link CheckEntryWorkflowStateJob} */
    private static final Logger LOG = Logger.getLogger(CheckEntryWorkflowStateJob.class.getName());

    /**
     * {@inheritDoc}
     *
     * <ol>
     * <li>Statustabelle abfragen, ob es einen Eintrag im Status 'OK' gibt, dessen lastModifiedDate < als x ist.</li>
     * <li>Status auf WARN stellen</li>
     * <li>Wenn lastModifiedDate < y ist, dann Hinweis bringen</li>
     * <li>Wenn Status auf WARN steht und lastModifiedDate < y, dann Status auf STOPPED stellen und Hinweis bringen</li>
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     *
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info("Executing ... " + getClass().getSimpleName());
        }

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

    private Collection<String> getEntryStatesInStateWarnAndStop(final Map<String, Object> params) {
        Set<String> entries = new HashSet<String>();
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_STARTDATE, params);
        for (EntryState entryState : entryStates) {
            entries.add(stopEntryByEntryState(entryState).getDescription());
        }
        return entries;
    }

    private Entry stopEntryByEntryState(final EntryState entryState) {
        entryState.setEnd(Calendar.getInstance().getTime());
        entryState.setState(State.STOPPED);
        EntityController.getInstance().update(entryState);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Entry.PARAM_STATE, entryState);
        Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                Entry.QUERY_FIND_BY_STATE, params);
        entry.setDuration(EntryState.getDuration(entryState.getStart(), entryState.getEnd(), entry.getDuration()));

        EntityController.getInstance().update(entry);

        return entry;
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
