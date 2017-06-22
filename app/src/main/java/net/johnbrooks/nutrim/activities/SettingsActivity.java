package net.johnbrooks.nutrim.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;

public class SettingsActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplicationContexts.refreshActivityTheme(SettingsActivity.this);
        setContentView(R.layout.activity_settings);

        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        final Profile profile = Profile.getProfile();
        if (profile == null)
        {
            Log.d(EditProfileActivity.class.getSimpleName(), "Profile is null. Exiting activity...");
            finish();
        }
        else
        {
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

            Spinner themeSpinner = (Spinner) findViewById(R.id.settingsActivity_theme);
            Profile.Theme t = profile.getTheme();
            themeSpinner.setSelection(t.ordinal());

            Spinner measurementSpinner = (Spinner) findViewById(R.id.settingsActivity_spinner_measurement);
            Profile.MeasurementSystem m = profile.getMeasurementSystem();
            measurementSpinner.setSelection(m.ordinal());

            measurementSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (position < Profile.MeasurementSystem.values().length)
                    {
                        Profile.MeasurementSystem measurementSystem = Profile.MeasurementSystem.values()[position];
                        if (measurementSystem != profile.getMeasurementSystem())
                        {
                            profile.setMeasurementSystem(measurementSystem);
                            profile.save(SettingsActivity.this);

                            Toast toast = Toast.makeText(SettingsActivity.this, "Updated measurement system!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    if (position < Profile.Theme.values().length)
                    {
                        Profile.Theme theme = Profile.Theme.values()[position];
                        if (!theme.name().equalsIgnoreCase(profile.getTheme().name()))
                        {
                            profile.setTheme(theme);
                            profile.save(SettingsActivity.this);

                            Toast toast = Toast.makeText(SettingsActivity.this, "Updated application theme!", Toast.LENGTH_SHORT);
                            toast.show();

                            if (theme == Profile.Theme.LIGHT)
                                getApplicationContext().setTheme(R.style.LightTheme);
                            else if (theme == Profile.Theme.DARK)
                                getApplicationContext().setTheme(R.style.DarkTheme);

                            //activity.startActivity(new Intent(activity, activity.getClass()));
                            finish();
                            HomeActivity.getInstance().finish();

                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }
    }

    private void openClearFoodConfirmation()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setCancelable(true)
                .setTitle("Clear All Meals")
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
                            Log.d(SettingsActivity.class.getSimpleName(), "Clearing all meals...");
                            profile.setCaloriesToday(0);
                            profile.getItemsConsumed().clear();
                            profile.getLog().getItemsConsumed().clear();
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
