package net.johnbrooks.nutrim.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;

public class SettingsActivity extends AppCompatActivity
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create button actions

        findViewById(R.id.settingsActivity_button_clearFood).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openClearFoodConfirmation();
            }
        });

        findViewById(R.id.settingsActivity_button_resetProfile).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openResetProfileConfirmation();
            }
        });
    }

    private void openClearFoodConfirmation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true)
                .setTitle("Clear Today's Meals")
                .setMessage("Are you sure?\nThis action cannot be undone.")
                .setNegativeButton("Nope", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d(SettingsActivity.class.getSimpleName(), "Cancelling action...");
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Profile profile = Profile.getProfile();
                        if (profile != null)
                        {
                            Log.d(SettingsActivity.class.getSimpleName(), "Clearing today's meals...");
                            profile.setCaloriesToday(0);
                            profile.getItemsConsumed().clear();
                            profile.save(MyApplicationContexts.getLatestContextWrapper(SettingsActivity.this));
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openResetProfileConfirmation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true)
                .setTitle("Reset Profile")
                .setMessage("Are you sure?\nThis action cannot be undone.")
                .setNegativeButton("Nope", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Log.d(SettingsActivity.class.getSimpleName(), "Cancelling action...");
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Profile profile = Profile.getProfile();
                        if (profile != null)
                        {
                            Log.d(SettingsActivity.class.getSimpleName(), "Resetting user profile...");
                            profile.delete(MyApplicationContexts.getLatestContextWrapper(SettingsActivity.this));
                            finish();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
