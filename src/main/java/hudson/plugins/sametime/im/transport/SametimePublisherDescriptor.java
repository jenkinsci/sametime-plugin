/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.Util;
import hudson.util.FormFieldValidator;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.plugins.sametime.im.IMException;
import hudson.plugins.sametime.im.IMMessageTargetConversionException;
import hudson.plugins.sametime.tools.Assert;
import hudson.tasks.Publisher;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.IOException;

/**
 * Descriptor for the SametimePublisher.  A Descriptor is an object that has metadata about a {@link Describable}
 * object, and also serves as a factory.
 *
 * @author Jamie Burrell
 * @since 18 Jan 2008
 */
public class SametimePublisherDescriptor extends Descriptor<Publisher>
{
    private static final String PREFIX = "sametimePlugin.";
    /** Name for the PORT parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_PORT = SametimePublisherDescriptor.PREFIX + "port";
    /** Name for the HOSTNAME parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_HOSTNAME = SametimePublisherDescriptor.PREFIX + "hostname";
    /** Name for the PRESENCE parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_PRESENCE = SametimePublisherDescriptor.PREFIX + "exposePresence";
    /** Name for the PASSWORD parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_PASSWORD = SametimePublisherDescriptor.PREFIX + "password";
    /** Name for the NICKNAME parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_NICKNAME = SametimePublisherDescriptor.PREFIX + "nick";
    /** Name for the TARGETS parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_TARGETS = SametimePublisherDescriptor.PREFIX + "targets";
    /** Name for the STRATEGY parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_STRATEGY = SametimePublisherDescriptor.PREFIX + "strategy";
    /** Name for the NOTIFY_START parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_NOTIFY_START = SametimePublisherDescriptor.PREFIX + "notifyStart";
    /** Name for the NOTIFY_SUSPECTS parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_NOTIFY_SUSPECTS = SametimePublisherDescriptor.PREFIX + "notifySuspects";
    /** Name for the NOTIFY_FIXERS parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_NOTIFY_FIXERS = SametimePublisherDescriptor.PREFIX + "notifyFixers";
    /** Name for the INITIAL_GROUPCHATS parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_INITIAL_GROUPCHATS = SametimePublisherDescriptor.PREFIX + "initialGroupChats";
    /** Name for the COMMAND_PREFIX parameter, as it appears in the jelly scripts   */
    public static final String PARAMETERNAME_COMMAND_PREFIX = SametimePublisherDescriptor.PREFIX + "commandPrefix";
    /** Default value for the STRATEGY_ALL parameter  */
    public static final String PARAMETERVALUE_STRATEGY_ALL = "all";
    /** Default value for the STRATEGY_FAILURE parameter  */
    public static final String PARAMETERVALUE_STRATEGY_FAILURE = "failure";
    /** Default value for the STRATEGY_STATE_CHANGE parameter  */
    public static final String PARAMETERVALUE_STRATEGY_STATE_CHANGE = "change";
    /** Default value for the STRATEGY_VALUES parameter  */
    public static final String[] PARAMETERVALUE_STRATEGY_VALUES = {
    	PARAMETERVALUE_STRATEGY_ALL,
    	PARAMETERVALUE_STRATEGY_FAILURE,
    	PARAMETERVALUE_STRATEGY_STATE_CHANGE
    };
    /** Default value for the STRATEGY_DEFAULT parameter  */
    public static final String PARAMETERVALUE_STRATEGY_DEFAULT = PARAMETERVALUE_STRATEGY_STATE_CHANGE;
    /**  DEFAULT_COMMAND_PREFIX  */
    public static final String DEFAULT_COMMAND_PREFIX = "!";

    private int port = 5222;
    private String hostname = null;
    private String hudsonNickname = "hudson";
    private String hudsonPassword = "secret";
    private boolean exposePresence = true;
    private String initialGroupChats = null;
    private String commandPrefix = DEFAULT_COMMAND_PREFIX;

    /**
     * Constructor.
     */
    public SametimePublisherDescriptor()
    {
        super(SametimePublisher.class);
        load();
        try
        {
            SametimeIMConnectionProvider.getInstance().createConnection(this);
        }
        catch (final IMException dontCare)
        {
            // Server temporarily unavailable ?
            dontCare.printStackTrace();
        }
    }

    /**
     * Sets the hostname, amd attempts to get a response from the host itself.
     * @param req The form request.
     * @throws FormException
     */
    private void applyHostname(final HttpServletRequest req) throws FormException
    {
        final String s = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_HOSTNAME);
        if (s != null && s.trim().length() > 0)
        {
            try
            {
                InetAddress.getByName(s); // try to resolve
                this.hostname = s;
            }
            catch (final UnknownHostException e)
            {
                throw new FormException("Cannot find Host '" + s + "'.",
                        SametimePublisherDescriptor.PARAMETERNAME_HOSTNAME);
            }
        }
        else
        {
            this.hostname = null;
        }
    }

    /**
     * Sets the user name, and does some validation.
     * @param req The form request
     * @throws FormException
     */
    private void applyNickname(final HttpServletRequest req) throws FormException
    {
        this.hudsonNickname = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_NICKNAME);
        if (this.hostname != null && (this.hudsonNickname == null || this.hudsonNickname.trim().length() == 0))
        {
            throw new FormException("Account/Nickname cannot be empty.",
                    SametimePublisherDescriptor.PARAMETERNAME_NICKNAME);
        }
    }

    /**
     * Sets the password, and does some validation.
     * @param req The form request
     * @throws FormException
     */
    private void applyPassword(final HttpServletRequest req) throws FormException
    {
        this.hudsonPassword = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_PASSWORD);
        if (this.hostname != null && this.hudsonPassword == null || this.hudsonPassword.trim().length() == 0)
        {
            throw new FormException("Password cannot be empty.", SametimePublisherDescriptor.PARAMETERNAME_PASSWORD);
        }
    }

    /**
     * Sets the port, and does some validation.
     * @param req The form request
     * @throws FormException
     */
    private void applyPort(final HttpServletRequest req) throws FormException
    {
        final String p = Util.fixEmptyAndTrim(req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_PORT));
        if (p != null)
        {
            try
            {
                final int i = Integer.parseInt(p);
                if (i < 0 || i > 65535)
                {
                    throw new FormException("Port out of range.", SametimePublisherDescriptor.PARAMETERNAME_PORT);
                }
                this.port = i;
            }
            catch (final NumberFormatException e)
            {
                throw new FormException("Port cannot be parsed.", SametimePublisherDescriptor.PARAMETERNAME_PORT);
            }
        } else {
            this.port = 5222;
        }
    }

    /**
     * Sets the presence option.
     * @param req The form request
     */
    private void applyPresence(final HttpServletRequest req)
    {
        this.exposePresence = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_PRESENCE) != null;
    }

    /**
     * Sets the group chats to initally enter
     * @param req The form request
     */
    private void applyInitialGroupChats(final HttpServletRequest req) {
    	this.initialGroupChats = Util.fixEmptyAndTrim(req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_INITIAL_GROUPCHATS));
    }

    /**
     * Sets the prefix for bot commands
     * @param req The form request
     */
    private void applyCommandPrefix(final HttpServletRequest req) {
    	String commandPrefix = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_COMMAND_PREFIX);
    	if (commandPrefix != null && commandPrefix.trim().length() > 0) {
    		this.commandPrefix = commandPrefix;
    	} else {
    		this.commandPrefix = DEFAULT_COMMAND_PREFIX;
    	}
    }

    /**
     * This human readable name is used in the configuration screen.
     */
    @Override
    public String getDisplayName()
    {
        return "Sametime Notification";
    }

    /**
     * Returns the text to be put into the form field.
     * If the port is default, leave it empty.
     * @return The text.
     */
    public String getPortString() {
        if(port==5222)  return null;
        else            return String.valueOf(port);
    }

    /**
     * Whether to show up on Sametime as accessible.
     * @return <code>true</code> or <code>false</code>
     */
    public boolean isExposePresence()
    {
        return this.exposePresence;
    }

    /**
     * Gets the whitespace separated list of group chats to join,
     * or null if nothing is configured.
     * @return The list.
     */
    public String getInitialGroupChats() {
    	return Util.fixEmptyAndTrim(this.initialGroupChats);
    }

    /**
     * Accessor for the prefix used in sending commands.
     * @return The prefix.
     */
    public String getCommandPrefix() {
    	return this.commandPrefix;
    }

    /**
     * Creates a new instance of {@link SametimePublisher} from a submitted form.
     */
    @Override
    public SametimePublisher newInstance(final StaplerRequest req) throws FormException
    {
        Assert.isNotNull(req, "Parameter 'req' must not be null.");
        final String t = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_TARGETS);
        String n = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_STRATEGY);
        if (n == null) {
        	n = PARAMETERVALUE_STRATEGY_DEFAULT;
        } else {
        	boolean foundStrategyValueMatch = false;
        	for (final String strategyValue : PARAMETERVALUE_STRATEGY_VALUES) {
        		if (strategyValue.equals(n)) {
        			foundStrategyValueMatch = true;
        			break;
        		}
        	}
        	if (! foundStrategyValueMatch) {
        		n = PARAMETERVALUE_STRATEGY_DEFAULT;
        	}
        }
        final String s = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_NOTIFY_START);
        final String ns = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_NOTIFY_SUSPECTS);
        final String nf = req.getParameter(SametimePublisherDescriptor.PARAMETERNAME_NOTIFY_FIXERS);
        try
        {
            return new SametimePublisher(t, n,
            		(s != null && "on".equals(s)),
            		(ns != null && "on".equals(ns)),
            		(nf != null && "on".equals(nf)));
        }
        catch (final IMMessageTargetConversionException e)
        {
            throw new FormException(e, SametimePublisherDescriptor.PARAMETERNAME_TARGETS);
        }
    }

    /**
     * Shuts down the connection etc.
     */
    public void shutdown()
    {
        final SametimeIMConnectionProvider factory = SametimeIMConnectionProvider.getInstance();
        factory.releaseConnection();
    }

	/* (non-Javadoc)
	 * @see hudson.model.Descriptor#configure(org.kohsuke.stapler.StaplerRequest)
	 */
	@Override
	public boolean configure(StaplerRequest req) throws hudson.model.Descriptor.FormException {
		Assert.isNotNull(req, "Parameter 'req' must not be null.");

        applyPresence(req);
        applyHostname(req);
        applyPort(req);
        applyNickname(req);
        applyPassword(req);
        applyInitialGroupChats(req);
        applyCommandPrefix(req);

        try
        {
            SametimeIMConnectionProvider.getInstance().createConnection(this);
        }
        catch (final Exception e)
        {
            throw new FormException("Unable to create Client: " + e, null);
        }
        save();
        return super.configure(req);
	}

    /**
     * Validates the server name.
     * @param req The Stapler request
     * @param rsp The response
     * @throws IOException
     * @throws ServletException
     */
    public void doServerCheck(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        new FormFieldValidator(req, rsp, false) {
            @Override
            protected void check() throws IOException, ServletException {
                String v = Util.fixEmptyAndTrim(request.getParameter("value"));
                if (v == null)
                    ok();
                else {
                    try {
                        InetAddress.getByName(v);
                        ok();
                    } catch (UnknownHostException e) {
                        error("Unknown host "+v);
                    }
                }
            }
        }.process();
    }

    /**
     * Getter method for the port field.
     *
     * @return The port property.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Getter method for the hostname field.
     *
     * @return The hostname property.
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * Getter method for the hudsonNickname field.
     *
     * @return The hudsonNickname property.
     */
    public String getHudsonNickname()
    {
        return hudsonNickname;
    }

    /**
     * Getter method for the hudsonPassword field.
     *
     * @return The hudsonPassword property.
     */
    public String getHudsonPassword()
    {
        return hudsonPassword;
    }
}