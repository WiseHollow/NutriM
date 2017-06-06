package net.johnbrooks.nutrim.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.Profile;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

public class MyProfileActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //
        // Get view content for activity
        //

        refreshActivityContent();
    }

    public boolean refreshActivityContent()
    {
        Profile profile = Profile.getProfile();
        if (profile == null)
        {
            Log.d(MyProfileActivity.class.getSimpleName(), "Could not refresh activity contents. Active profile is null.");
            return false;
        }
        else
        {
            // get

            TextView tv_fullName = (TextView) findViewById(R.id.myProfileActivity_tv_fullName);
            TextView tv_height = (TextView) findViewById(R.id.myProfileActivity_tv_height);
            TextView tv_weight = (TextView) findViewById(R.id.myProfileActivity_tv_weight);

            TextView tv_birthday = (TextView) findViewById(R.id.myProfileActivity_tv_birthday);
            TextView tv_age = (TextView) findViewById(R.id.myProfileActivity_tv_age);

            // set

            tv_fullName.setText(profile.getFullName());

            if (profile.getMeasurementSystem() == Profile.MeasurementSystem.IMPERIAL)
            {
                int[] m = profile.getFeetAndInches();
                tv_height.setText("" + m[0] + " Feet " + m[1] + " Inches");
                tv_weight.setText(profile.getWeightLbs() + " Lbs");
            }
            else if (profile.getMeasurementSystem() == Profile.MeasurementSystem.METRIC)
            {
                tv_height.setText("" + profile.getHeightCm() + " cm");
                tv_weight.setText(profile.getWeightKg() + " Kg");
            }

            tv_birthday.setText(Profile.dateFormat.format(profile.getBirthday()));
            tv_age.setText("" + profile.getAge());

            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
