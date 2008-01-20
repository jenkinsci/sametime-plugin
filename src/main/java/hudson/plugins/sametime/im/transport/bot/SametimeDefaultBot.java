/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport.bot;

import java.io.PrintStream;

import com.lotus.sametime.im.ImEvent;
import com.lotus.sametime.im.ImListener;

/**
 * This is a default bot that simply ignores Im opening, closing and failing, but responds to the reception of messages with a standard brush-off response.
 * @author Jamie Burrell
 * @since 16 Jan 2008
 * @version 1.0
 */
public class SametimeDefaultBot implements ImListener
{
    public static final String DEFAULT_MESSAGE = "Sorry, but I am a bot, and don't respond to input.";

    private static PrintStream log = System.out;

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#dataReceived(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void dataReceived(ImEvent ie)
    {
        ie.getIm().sendText(true, DEFAULT_MESSAGE);
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#imClosed(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void imClosed(ImEvent arg0)
    {

    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#imOpened(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void imOpened(ImEvent arg0)
    {

    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#openImFailed(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void openImFailed(ImEvent ie)
    {
        log.println("Could not open IM session. Error code " + ie.getReason());
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#textReceived(com.lotus.sametime.im.ImEvent)
     */
    @Override
    public void textReceived(ImEvent ie)
    {
        ie.getIm().sendText(true, DEFAULT_MESSAGE);
    }

}
