package de.ronnyfriedland.time.logic.plugin.jmx;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import de.ronnyfriedland.time.entity.Entry;
import de.ronnyfriedland.time.entity.EntryState;
import de.ronnyfriedland.time.entity.Project;
import de.ronnyfriedland.time.logic.EntityController;
import de.ronnyfriedland.time.logic.plugin.PluginException;

/**
 * @author Ronny Friedland
 */
public class DataBeanImpl extends StandardMBean implements IDataBean {

    public DataBeanImpl() throws NotCompliantMBeanException {
        super(IDataBean.class);
    }

    @Override
    public CompositeData getActiveWorkflowEntries() throws PluginException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(EntryState.PARAM_STATE, EntryState.State.OK);
        params.put(EntryState.PARAM_DATE, Calendar.getInstance().getTime());
        Collection<EntryState> entryStates = EntityController.getInstance().findResultlistByParameter(EntryState.class,
                EntryState.QUERY_FIND_BY_STATE_AND_DATE, params);
        try {
            if (entryStates.isEmpty()) {
                return null;
            } else {
                int i = 0;
                String[] itemNames = new String[entryStates.size()];
                Object[] items = new Object[entryStates.size()];
                OpenType[] itemTypes = new OpenType[entryStates.size()];
                for (EntryState entryState : entryStates) {
                    params = new HashMap<String, Object>();
                    params.put(Entry.PARAM_STATE, entryState);
                    Entry entry = EntityController.getInstance().findSingleResultByParameter(Entry.class,
                            Entry.QUERY_FIND_BY_STATE, params);

                    itemNames[i] = entry.getUuid();
                    items[i] = String.format("%s (project: %s)", entry.getDescription(), entry.getProject().getName());
                    itemTypes[i] = SimpleType.STRING;
                    i++;
                }
                return new CompositeDataSupport(new CompositeType(String.class.getName(), "active workflow entries",
                        itemNames, itemNames, itemTypes), itemNames, items);
            }
        } catch (Exception e) {
            throw new PluginException("error providing jmx data", e);
        }
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
