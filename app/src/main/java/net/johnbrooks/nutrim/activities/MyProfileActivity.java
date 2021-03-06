package net.johnbrooks.nutrim.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;
import net.johnbrooks.nutrim.wrapper.NutritionIXItem;

public class MyProfileActivity extends AppCompatActivity
{
    private static MyProfileActivity instance;
    public static MyProfileActivity getInstance() { return instance; }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplicationContexts.refreshActivityTheme(MyProfileActivity.this);
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

        findViewById(R.id.myProfileActivity_button_editProfile).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    protected void onResume()
    {
        super.onResume();

        instance = this;
        refreshActivityContent();
        refreshListOfConsumedGoods();
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

    public void refreshListOfConsumedGoods()
    {
        if (Profile.getProfile() != null)
        {
            LinearLayout layout_consumed = (LinearLayout) findViewById(R.id.myProfileActivity_layout_foodIcons);
            layout_consumed.removeAllViews();

            if (!Profile.getProfile().getLog().getBreakdown().isEmpty())
            {
                final NutritionIXItem[] items = Profile.getProfile().getLog().getItemsConsumed().keySet().toArray(new NutritionIXItem[Profile.getProfile().getLog().getItemsConsumed().keySet().size()]);
                final Integer[] amounts = Profile.getProfile().getLog().getItemsConsumed().values().toArray(new Integer[Profile.getProfile().getLog().getItemsConsumed().values().size()]);

                for (int i = 0; i < Profile.getProfile().getLog().getBreakdown().size(); i++)
                {
                    final int index = i;
                    LinearLayout layout = Profile.getProfile().getLog().getBreakdown().get(i);
                    layout_consumed.addView(layout);
                    layout.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (index <= items.length)
                            {
                                NutritionIXItem ixItem = items[index];
                                if (ixItem != null)
                                    ixItem.openDetails(MyProfileActivity.this, amounts[index]);
                            }
                        }
                    });
                }
            }
            else
            {
                TextView tv_nothing = new TextView(MyProfileActivity.this);
                tv_nothing.setText("Nothing to show yet.");
                layout_consumed.addView(tv_nothing);
            }
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
