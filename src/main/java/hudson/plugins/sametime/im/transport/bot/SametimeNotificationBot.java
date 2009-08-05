/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport.bot;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lotus.sametime.im.ImEvent;

/**
 * Standard bot that just sends out a message when it is connected.
 * @author Jamie Burrell
 * @since 16 Jan 2008
 * @version 1.0
 */
public class SametimeNotificationBot extends SametimeDefaultBot
{
    private static final Logger log = Logger.getLogger(SametimeNotificationBot.class.getName());
    private final String notificationMessage;

    /**
     * Constructor.
     * @param notificationMessage
     */
    public SametimeNotificationBot(String notificationMessage)
    {
        super();
        this.notificationMessage = notificationMessage;
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.transport.bot.SametimeDefaultBot#imOpened(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void imOpened(ImEvent ie)
    {
    	ie.getIm().sendText(true, notificationMessage);
        // give it time to arrive
        try
        {
            wait(500);
        }
        catch (InterruptedException e)
        {
            log.log(Level.SEVERE, "InterruptedException caught!", e);
        }
        ie.getIm().close(0);
    }
}
