package de.ronnyfriedland.time.logic.plugin.jmx;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import de.ronnyfriedland.time.logic.plugin.Plugin;

/**
 * Plugin, welches diverse Informationen über MBeans / JMX zur Verfügung stellt.
 * 
 * @author Ronny Friedland
 */
@Plugin
public class JmxPlugin {
    /**
     * Erzeugt eine neue Instanz von {@link JmxPlugin}.
     */
    public JmxPlugin() {
        try {
            ConfigBeanImpl configBean = new ConfigBeanImpl();
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean(configBean, new ObjectName("de.ronnyfriedland.time:name=Config"));

            // wait forever
            wait();
        } catch (NotCompliantMBeanException e) {
            throw new RuntimeException(e);
        } catch (InstanceAlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (MBeanRegistrationException e) {
            throw new RuntimeException(e);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
