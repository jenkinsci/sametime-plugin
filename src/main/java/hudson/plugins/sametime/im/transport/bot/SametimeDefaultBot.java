/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport.bot;

import java.util.logging.Level;
import java.util.logging.Logger;

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

    private static final Logger log = Logger.getLogger(SametimeDefaultBot.class.getName());

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#dataReceived(com.lotus.sametime.im.ImEvent)
     */
    public void dataReceived(ImEvent ie)
    {
    	//FIXME Handle WHAT text is recieved. In SAmetime Connect 7.5.1 sends default message with notifications.
        ie.getIm().sendText(true, DEFAULT_MESSAGE);
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#imClosed(com.lotus.sametime.im.ImEvent)
     */
    public void imClosed(ImEvent arg0)
    {

    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#imOpened(com.lotus.sametime.im.ImEvent)
     */
    public void imOpened(ImEvent arg0)
    {

    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#openImFailed(com.lotus.sametime.im.ImEvent)
     */
    public void openImFailed(ImEvent ie)
    {
    	//FIXME Handle when IMTarget is offline.
        log.log(Level.SEVERE, "Could not open IM session with ["+ ie.getIm().getPartner().getName()+ "]. Error code: " + ie.getReason(), ie);
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.im.ImListener#textReceived(com.lotus.sametime.im.ImEvent)
     */
    public void textReceived(ImEvent ie)
    {
    	//FIXME Handle WHAT text is recieved. In SAmetime Connect 7.5.1 sends default message with notifications.
        ie.getIm().sendText(true, DEFAULT_MESSAGE);
    }

}
