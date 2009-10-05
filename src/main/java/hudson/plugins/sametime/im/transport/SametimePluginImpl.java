/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.Plugin;

/**
 * Plugin entry point used to start/stop the plugin.
 * @author Jamie Burrell
 * @plugin
 */
public class SametimePluginImpl extends Plugin
{
    /* (non-Javadoc)
     * @see hudson.Plugin#stop()
     */
    @Override
    public void stop() throws Exception
    {
        SametimePublisher.DESCRIPTOR.shutdown();
        super.stop();
    }
}
