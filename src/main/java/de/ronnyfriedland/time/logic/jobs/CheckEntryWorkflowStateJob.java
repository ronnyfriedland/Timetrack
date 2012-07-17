package de.ronnyfriedland.time.logic.jobs;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

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
public class CheckEntryWorkflowStateJob implements Job {

    public final static String JOB = "checkentryworkflowstatejob";
    public final static String TRIGGER = "checkentryworkflowstate";

    private static final Logger LOG = Logger.getLogger(CheckEntryWorkflowStateJob.class.getName());

    /**
     * (non-Javadoc)
     * 
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        LOG.info("executing ... " + CheckEntryWorkflowStateJob.class.getSimpleName());
        /*
         * 1. Statustabelle abfragen, ob es einen Eintrag im Status 'OK' gibt,
         * dessen lastModifiedDate < als x ist. 2. Status auf WARN stellen 3.
         * Wenn lastModifiedDate < y ist, dann Hinweis bringen 4. wenn status
         * auf WARN steht und lastModifiedDate < y, dann Status auf STOPPED
         * stellen und Hinweis bringen
         */
        // TODO: parameter auslagern
        Integer x = 60 * 60; // 1h
        Integer y = x * 2; // 2h

        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<String, Object>();

        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -y);
        params.clear();
        params = new HashMap<String, Object>();
        params.put(EntryState.PARAM_DATE, cal.getTime());
        params.put(EntryState.PARAM_STATE, State.WARN);

        Collection<String> stop = getEntryStatesInStateWarnAndStop(params);

        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -x);
        params.clear();
        params.put(EntryState.PARAM_DATE, cal.getTime());
        params.put(EntryState.PARAM_STATE, State.WARN);

        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, -x);
        params.clear();
        params.put(EntryState.PARAM_DATE, cal.getTime());
        params.put(EntryState.PARAM_STATE, State.OK);
        setEntryStatesToWarn(params);

        Collection<String> warn = getEntryStatesInStateWarn(params);

        if (!warn.isEmpty()) {
            // FIXME: hier sollte für jeden Eintrag ein Popup erscheinen, auf
            // die man reagieren kann
            JOptionPane.showMessageDialog(null, "Die folgenden Einträge laufen seit einiger Zeit - bitte prüfen:\n"
                    + StringUtils.join(warn, "\n"));
        }

        if (!stop.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Die folgenden Einträge wurden gestoppt:\n" + StringUtils.join(stop, "\n"));
        }

    }

    private Collection<String> getEntryStatesInStateWarnAndStop(Map<String, Object> params) {
        Set<String> entries = new HashSet<String>();
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_DATE, params);
        for (EntryState entryState : entryStates) {
            entryState.setEnd(Calendar.getInstance().getTime());
            entryState.setState(State.STOPPED);
            EntityController.getInstance().update(entryState);

            params = new HashMap<String, Object>();
            params.put(Entry.PARAM_STATE, entryState);
            Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                    Entry.QUERY_FIND_BY_STATE, params);
            entry.setDuration(EntryState.getDuration(entryState.getStart(), entryState.getEnd()));

            EntityController.getInstance().update(entry);

            entries.add(entry.getDescription());
        }
        return entries;
    }

    private Collection<String> getEntryStatesInStateWarn(Map<String, Object> params) {
        Set<String> entries = new HashSet<String>();
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_DATE, params);
        for (EntryState entryState : entryStates) {
            params = new HashMap<String, Object>();
            params.put(Entry.PARAM_STATE, entryState);
            Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                    Entry.QUERY_FIND_BY_STATE, params);
            entries.add(entry.getDescription());
        }
        return entries;
    }

    private void setEntryStatesToWarn(Map<String, Object> params) {
        Collection<EntryState> entryStates;
        entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_DATE, params);

        for (EntryState entryState : entryStates) {
            entryState.setState(State.WARN);
            EntityController.getInstance().update(entryState);
        }
    }
}
