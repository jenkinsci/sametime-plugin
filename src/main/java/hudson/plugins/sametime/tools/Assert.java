package hudson.plugins.sametime.tools;


/**
 * Convenience.
 * @author Uwe Schaefer
 */
public final class Assert
{
    private static class AssertionFailedException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        /**
         * Constructs a new exception with the given message.
         */
        AssertionFailedException(final String detail)
        {
            super(detail);
        }
    }

    /**
     * Asserts that given object is not null.
     * @param object to check against null
     * @throws AssertionFailedException if object was null
     */
    public static final void isNotNull(final Object object) throws AssertionFailedException
    {
        Assert.isNotNull(object, "Object was null");
    }

    /**
     * Asserts that given object is not null.
     * @param obj Object to check against null
     * @param msg Message to display if it is null
     * @throws AssertionFailedException if object was null
     */
    public static final void isNotNull(final Object obj, final String msg)
    {
        if (obj == null)
        {
            Assert.raiseException(msg);
        }
    }

    /**
     * Raise the exception
     * @param error The message.
     */
    private static final void raiseException(final String error)
    {
        throw new AssertionFailedException(error);
    }

    /**
     * Constructor.
     */
    private Assert()
    {
        // hide
    }

}
