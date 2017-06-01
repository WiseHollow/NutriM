package net.johnbrooks.nutrim.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import net.johnbrooks.nutrim.ProfileLoadException;
import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;

public class SplashActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        MyApplicationContexts.getLatestContextWrapper(SplashActivity.this);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent;
                if (Profile.getProfile() != null)
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                else
                    intent = new Intent(SplashActivity.this, NewAccountActivity.class);

                startActivity(intent);
                finish();
            }
        }, 2500);

        Log.d(SplashActivity.class.getSimpleName(), "Loading profile from file.");
        try
        {
            Profile.loadProfile();
        } catch (ProfileLoadException e)
        {
            e.printStackTrace();
        }
    }
}
