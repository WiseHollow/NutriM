package net.johnbrooks.nutrim.utilities;

import android.app.Activity;
import android.content.ContextWrapper;

/**
 * Created by John on 6/1/2017.
 */

public class MyApplicationContexts
{
    private static ContextWrapper latestContextWrapper;
    public static ContextWrapper getLatestContextWrapper(Activity activity)
    {
        if (activity != null)
            latestContextWrapper = activity;
        return latestContextWrapper;
    }
}
