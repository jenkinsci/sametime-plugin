/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.model.Descriptor;
import hudson.plugins.sametime.im.IMConnection;
import hudson.plugins.sametime.im.IMException;
import hudson.plugins.sametime.im.IMMessageTargetConversionException;
import hudson.plugins.sametime.im.IMMessageTargetConverter;
import hudson.plugins.sametime.im.IMPublisher;
import hudson.plugins.sametime.tools.Assert;
import hudson.tasks.Publisher;

import java.io.PrintStream;

/**
 * Sametime-specific implementation of the IMPublisher
 * @author Jamie Burrell
 * @since 18 Jan 2008
 * @version 1.0
 */
public class SametimePublisher extends IMPublisher
{
    private static PrintStream log = System.out;

    static final SametimePublisherDescriptor DESCRIPTOR = new SametimePublisherDescriptor();

    private static IMMessageTargetConverter CONVERTER;

    /**
     * Constructor.
     * @param targetsAsString The people to notify
     * @param notificationStrategy When to notify these people
     * @param notifyGroupChatsOnBuildStart Whether to send forum notifications at the start of a build
     * @param notifySuspects Notify people suspected of breaking the build
     * @param notifyFixers Notify people who have fixed the build
     * @throws IMMessageTargetConversionException
     */
    public SametimePublisher(final String targetsAsString, final String notificationStrategy,
    		final boolean notifyGroupChatsOnBuildStart,
    		final boolean notifySuspects,
    		final boolean notifyFixers) throws IMMessageTargetConversionException
    {
        super(targetsAsString, notificationStrategy, notifyGroupChatsOnBuildStart,
        		notifySuspects, notifyFixers);
    }

    /* (non-Javadoc)
     * @see hudson.model.Describable#getDescriptor()
     */
    public Descriptor<Publisher> getDescriptor()
    {
        return SametimePublisher.DESCRIPTOR;
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.IMPublisher#getIMConnection()
     */
    @Override
    protected IMConnection getIMConnection() throws IMException
    {
        return SametimeIMConnectionProvider.getInstance().currentConnection();
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.IMPublisher#getIMMessageTargetConverter()
     */
    @Override
    protected IMMessageTargetConverter getIMMessageTargetConverter()
    {
        if(null == SametimePublisher.CONVERTER)
        {
            try
            {
                SametimeIMConnection conn = (SametimeIMConnection)getIMConnection();
                Assert.isNotNull(conn, "Could not obtain the current connection to Sametime.");
                SametimePublisher.CONVERTER = new SametimeIMMessageTargetConverter(conn.getSession());
            }
            catch (IMException e)
            {
                log.println("IMException caught!");
                e.printStackTrace(log);
            }
        }
        return SametimePublisher.CONVERTER;
    }
}
