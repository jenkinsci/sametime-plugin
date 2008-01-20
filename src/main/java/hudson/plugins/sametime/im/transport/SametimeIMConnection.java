/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.plugins.sametime.im.IMConnection;
import hudson.plugins.sametime.im.IMException;
import hudson.plugins.sametime.im.IMMessageTarget;
import hudson.plugins.sametime.im.IMPresence;
import hudson.plugins.sametime.im.transport.bot.SametimeNotificationBot;

import java.io.PrintStream;

import com.lotus.sametime.community.CommunityService;
import com.lotus.sametime.community.Login;
import com.lotus.sametime.community.LoginEvent;
import com.lotus.sametime.community.LoginListener;
import com.lotus.sametime.core.comparch.DuplicateObjectException;
import com.lotus.sametime.core.comparch.STSession;
import com.lotus.sametime.core.constants.EncLevel;
import com.lotus.sametime.core.constants.ImTypes;
import com.lotus.sametime.core.types.STPrivacyList;
import com.lotus.sametime.im.Im;
import com.lotus.sametime.im.InstantMessagingService;

/**
 * ST Toolkit-specific implementation of IMConnection.
 * @author Jamie Burrell
 */
class SametimeIMConnection implements IMConnection, LoginListener
{
    private STSession session;
    private CommunityService commService;
    private Login login;
    private InstantMessagingService imService;
    private static PrintStream log = System.out;

    /**
     * Constructor.
     * @param desc The configuration
     */
    public SametimeIMConnection(SametimePublisherDescriptor desc)
    {
        try
        {
            log.println("Creating ST Session.");
            session = new STSession("HudsonNotifierSession");
            log.println("Loading ST Components.");
            session.loadSemanticComponents();
            log.println("Starting ST Session.");
            session.start();

            commService = (CommunityService) session.getCompApi(CommunityService.COMP_NAME);
            commService.addLoginListener(this);
            log.println("Attempting login.");
            commService.loginByPassword(desc.getHostname(), desc.getHudsonNickname(), desc.getHudsonPassword());
        }
        catch (DuplicateObjectException e)
        {
            log.println("DuplicateObjectException caught!");
            e.printStackTrace(log);
        }
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.IMConnection#close()
     */
    @Override
    public void close()
    {
        commService.logout();
        session.stop();
        session.unloadSession();
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.IMConnection#send(hudson.plugins.sametime.im.IMMessageTarget, java.lang.String)
     */
    @Override
    public void send(IMMessageTarget target, String text) throws IMException
    {
        log.println("Opening IM session with target.");
        SametimeIMMessageTarget stTarget = (SametimeIMMessageTarget)target;

        Im im = imService.createIm(stTarget.getUser(), EncLevel.ENC_LEVEL_ALL, ImTypes.IM_TYPE_CHAT);
        im.addImListener(new SametimeNotificationBot(text));

        im.open();
    }

    /* (non-Javadoc)
     * @see hudson.plugins.sametime.im.IMConnection#setPresence(hudson.plugins.sametime.im.IMPresence)
     */
    @Override
    public void setPresence(IMPresence presence) throws IMException
    {
        if(null == login || !commService.isLoggedIn())
            return;

        switch(presence)
        {
        case AVAILABLE :
            // exclude no-one
            login.changeMyPrivacy(new STPrivacyList(true));
            break;
        case UNAVAILABLE : // default state
        default :
            // include no-one
            login.changeMyPrivacy(new STPrivacyList(false));
        }
    }

    /**
     * Get the session we are currently using
     * @return The session
     */
    public STSession getSession()
    {
        return session;
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.community.LoginListener#loggedIn(com.lotus.sametime.community.LoginEvent)
     */
    @Override
    public void loggedIn(LoginEvent le)
    {
        log.println("Loggedin successfully.");
        login = le.getLogin();

        log.println("Registering for IM Service.");
        imService = (InstantMessagingService) session.getCompApi(InstantMessagingService.COMP_NAME);
        imService.registerImType(ImTypes.IM_TYPE_CHAT);
    }

    /* (non-Javadoc)
     * @see com.lotus.sametime.community.LoginListener#loggedOut(com.lotus.sametime.community.LoginEvent)
     */
    @Override
    public void loggedOut(LoginEvent le)
    {
        session.stop();
        session.unloadSession();
    }

}
