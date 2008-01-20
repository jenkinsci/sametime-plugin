/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.plugins.sametime.im.IMConnection;
import hudson.plugins.sametime.im.IMException;
import hudson.plugins.sametime.im.IMPresence;
import hudson.plugins.sametime.tools.Assert;

/**
 * A factory for the connection to thne Sametime service.
 * @author Jamie Burrell
 * @since 18 Jan 2008
 * @version 1.0
 */
final class SametimeIMConnectionProvider
{
    private static final SametimeIMConnectionProvider INSTANCE = new SametimeIMConnectionProvider();

    /**
     * Returns the singleton instance of this factory.
     * @return The instance
     */
    static final SametimeIMConnectionProvider getInstance()
    {
        return SametimeIMConnectionProvider.INSTANCE;
    }

    private IMConnection imConnection;
    private SametimePublisherDescriptor descriptor;

    /**
     * Constructor.  Private to try for singleton status
     */
    private SametimeIMConnectionProvider()
    {
    }

    /**
     * Create a connection to Sametime
     * @param desc The configuration for the Sametime service
     * @return The connection
     * @throws IMException
     */
    synchronized IMConnection createConnection(final SametimePublisherDescriptor desc) throws IMException
    {
        Assert.isNotNull(desc, "Parameter 'desc' must not be null.");
        this.descriptor = desc;

        releaseConnection();

        if (desc.getHostname() != null)
        {
            this.imConnection = new SametimeIMConnection(desc);
            this.imConnection.setPresence(desc.isExposePresence() ? IMPresence.AVAILABLE : IMPresence.UNAVAILABLE);
        }
        return this.imConnection;
    }

    /**
     * Return the current connection, creating one if it doesn't exist.
     * @throws IMException on any underlying communication Exception
     */
    synchronized IMConnection currentConnection() throws IMException
    {
        return this.imConnection != null ? this.imConnection : createConnection(this.descriptor);
    }

    /**
     * releases (and thus closes) the current connection
     */
    synchronized void releaseConnection()
    {
        if (this.imConnection != null)
        {
            this.imConnection.close();
            this.imConnection = null;
        }
    }
}
