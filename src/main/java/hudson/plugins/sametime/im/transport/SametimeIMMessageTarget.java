/**
 * Hudson Sametime Plugin
 */
package hudson.plugins.sametime.im.transport;

import hudson.plugins.sametime.im.IMMessageTarget;
import hudson.plugins.sametime.tools.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.lotus.sametime.core.types.STUser;

/**
 * Contains both the String and object representations of a Sametime user Id (ie a resolved lookup).
 * @author Jamie Burrell
 * @since 16 Jan 2008
 * @version 1.0
 */
public class SametimeIMMessageTarget implements IMMessageTarget
{
    // The logger for the SametimeIMMessageTarget class.
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(SametimeIMMessageTarget.class);

    private static final long serialVersionUID = 1L;
    private final String value;
    private final STUser user;

    /**
     * Constructor.
     * @param user The resolved user object (from the ST lookup service)
     * @param userName The userName we looked up to get the user.
     */
    public SametimeIMMessageTarget(final STUser user, final String userName)
    {
        Assert.isNotNull(user, "Parameter 'user' must not be null.");
        Assert.isNotNull(userName, "Parameter 'userName' must not be null.");
        this.user = user;
        this.value = userName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object arg0)
    {
        if (arg0 == null)
        {
            return false;
        }
        if (arg0 == this)
        {
            return true;
        }
        if (arg0 instanceof SametimeIMMessageTarget)
        {
            final SametimeIMMessageTarget other = (SametimeIMMessageTarget) arg0;
            boolean retval = true;

            retval &= this.value.equals(other.value);

            return retval;
        }
        else
        {
            return false;
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.value;
    }

    /**
     * Gets the resolved user for this target.
     * @return The user representation
     */
    public STUser getUser()
    {
        return user;
    }
}
