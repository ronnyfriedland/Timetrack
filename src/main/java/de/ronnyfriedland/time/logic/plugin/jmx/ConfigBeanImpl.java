package de.ronnyfriedland.time.logic.plugin.jmx;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import de.ronnyfriedland.time.config.Configurator;
import de.ronnyfriedland.time.config.Configurator.ConfiguratorKeys;

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
     */
    public ConfigBeanImpl() throws NotCompliantMBeanException {
        super(IConfigBean.class);
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.ronnyfriedland.time.logic.plugin.jmx.IConfigBean#getConfiguration()
     */
    @Override
    public Collection<String> getConfiguration() {
        Set<String> configuration = new HashSet<String>();
        for (ConfiguratorKeys key : new ConfiguratorKeys[] { ConfiguratorKeys.CRON_EXPRESSION_ENTRYWORKFLOW,
                ConfiguratorKeys.CRON_EXPRESSION_POPUP, ConfiguratorKeys.EXPORT_FILE, ConfiguratorKeys.PATH,
                ConfiguratorKeys.SHOW_POPUP, ConfiguratorKeys.WORKFLOW_STOP, ConfiguratorKeys.WORKFLOW_WARN }) {
            configuration.add(String.format("%1$s : %2$s", key, Configurator.CONFIG.getString(key.getKey())));
        }
        return configuration;
    }

    /**
     * (non-Javadoc)
     * 
     * @see de.ronnyfriedland.time.logic.plugin.jmx.IConfigBean#gc()
     */
    @Override
    public void gc() {
        System.gc();
    }

}
