package de.ronnyfriedland.time.logic.plugin.jmx;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;

/**
 * @author Ronny Friedland
 * 
 *         TODO caching !!!
 */
public class DataBeanImpl extends StandardMBean implements IDataBean {

    public DataBeanImpl() throws NotCompliantMBeanException {
        super(IDataBean.class);
    }

    @Override
    public Collection<String> getActiveWorkflowEntries() {
        Set<String> entries = new HashSet<String>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EntryState.PARAM_STATE, EntryState.State.OK);
        params.put(EntryState.PARAM_DATE, Calendar.getInstance().getTime());
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_DATE, params);
        for (EntryState entryState : entryStates) {
            params = new HashMap<String, Object>();
            params.put(Entry.PARAM_STATE, entryState);
            Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                    Entry.QUERY_FIND_BY_STATE, params);
            entries.add(entry.toString());
        }
        return entries;
    }

    @Override
    public int getEntryCount() {
        return EntityController.getInstance().findAll(Entry.class, false).size();
    }

    @Override
    public int getProjectCount() {
        return EntityController.getInstance().findAll(Project.class, false).size();
    }

}
