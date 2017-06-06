package net.johnbrooks.nutrim.utilities;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by John on 6/6/2017.
 */

public class Network
{
    public static boolean isAccessible()
    {
        ContextWrapper context = MyApplicationContexts.getLatestContextWrapper(null);
        if (context == null)
            return false;

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = false;

        if (connectivityManager != null && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && connectivityManager.getActiveNetwork() != null))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (connectivityManager.getNetworkInfo(connectivityManager.getActiveNetwork()).getState() == NetworkInfo.State.CONNECTED)
                    connected = true;
            }
            else if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            {
                connected = true;
            }
        }

        return connected;
    }
}
