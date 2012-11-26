package de.ronnyfriedland.time.logic.plugin.jmx;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;
import de.ronnyfriedland.time.logic.plugin.PluginException;

/**
 * Implementierung von {@link IConfigBean} zur Darstellung der
 * Anwendungskonfiguration.
 * 
 * @author Ronny Friedland
 */
public class ConfigBeanImpl extends StandardMBean implements IConfigBean {

    /**
     * Erzeugt eine neue {@link ConfigBeanImpl} - Instanz.
     * 
     * @throws NotCompliantMBeanException
     *             Fehler beim Erzeugen
     */
    public ConfigBeanImpl() throws NotCompliantMBeanException {
        super(IConfigBean.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.ronnyfriedland.time.logic.plugin.jmx.IConfigBean#getConfiguration()
     */
    @Override
    @SuppressWarnings("rawtypes")
    public CompositeData getConfiguration() throws PluginException {
        try {
            int i = ConfiguratorKeys.values().length;
            String[] itemNames = new String[i];
            Object[] items = new Object[i];
            OpenType[] itemTypes = new OpenType[i];
            for (ConfiguratorKeys key : ConfiguratorKeys.values()) {
                i--;
                itemNames[i] = key.getKey();
                items[i] = String.format("%1$s", Configurator.CONFIG.getString(key.getKey()));
                itemTypes[i] = SimpleType.STRING;
            }
            return new CompositeDataSupport(new CompositeType(String.class.getName(), "configuration", itemNames,
                    itemNames, itemTypes), itemNames, items);
        } catch (Exception e) {
            throw new PluginException("error providing jmx data", e);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see de.ronnyfriedland.time.logic.plugin.jmx.IConfigBean#gc()
     */
    @Override
    public void gc() {
        System.gc();
    }

}
