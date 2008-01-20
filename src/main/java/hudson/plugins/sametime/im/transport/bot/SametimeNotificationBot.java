/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport.bot;

import java.io.PrintStream;

import com.lotus.sametime.im.ImEvent;

/**
 * Standard bot that just sends out a message when it is connected.
 * @author Jamie Burrell
 * @since 16 Jan 2008
 * @version 1.0
 */
public class SametimeNotificationBot extends SametimeDefaultBot
{
    @SuppressWarnings("unused")
    private static PrintStream log = System.out;
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
            log.println("InterruptedException caught!");
        }
        ie.getIm().close(0);
    }
}
