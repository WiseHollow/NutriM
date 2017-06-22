package net.johnbrooks.nutrim.utilities;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import net.johnbrooks.nutrim.R;

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
    public static void refreshActivityTheme(Activity activity)
    {
        if (Profile.getProfile() != null)
        {
            int id = -1;

            switch(Profile.getProfile().getTheme())
            {
                case LIGHT:
                    id = R.style.LightTheme;
                    break;
                case DARK:
                    id = R.style.DarkTheme;
                    break;
            }

            if (id != -1)
                activity.setTheme(id);
        }
    }
}
