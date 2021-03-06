package net.johnbrooks.nutrim.utilities;

/**
 * Created by John on 6/1/2017.
 */

public class ProfileLoadException extends Exception
{
    public ProfileLoadException()
    {
        super("Profile could not be loaded from file. Nothing to load.");
    }

    public ProfileLoadException(String message)
    {
        super(message);
    }
}
