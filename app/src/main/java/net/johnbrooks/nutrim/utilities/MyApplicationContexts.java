package net.johnbrooks.nutrim.utilities;

import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by John on 6/1/2017.
 */

public class MyApplicationContexts
{
    private static ContextWrapper latestContextWrapper;
    public static ContextWrapper getLatestContextWrapper(ContextWrapper contextWrapper)
    {
        if (contextWrapper != null)
            latestContextWrapper = contextWrapper;
        return latestContextWrapper;
    }
    public static SharedPreferences getSharedPreferences()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(latestContextWrapper.getApplicationContext());
        return sharedPreferences;
    }
}
