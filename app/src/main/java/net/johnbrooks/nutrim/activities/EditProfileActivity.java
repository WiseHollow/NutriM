package net.johnbrooks.nutrim.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.Profile;

public class EditProfileActivity extends AppCompatActivity
{
    private EditText et_fullName;
    private EditText et_height;
    private EditText et_weight;
    private EditText et_birthday;
    private EditText et_age;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //
        // Prepare back button
        //

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //

        Profile profile = Profile.getProfile();
        if (profile == null)
        {
            Log.d(EditProfileActivity.class.getSimpleName(), "Profile is null. Exiting activity...");
            finish();
        }
        else
        {
            et_fullName = (EditText) findViewById(R.id.editProfileActivity_et_fullName);
            et_height = (EditText) findViewById(R.id.editProfileActivity_et_height);
            et_weight = (EditText) findViewById(R.id.editProfileActivity_et_weight);
            et_birthday = (EditText) findViewById(R.id.editProfileActivity_et_birthday);
            et_age = (EditText) findViewById(R.id.editProfileActivity_et_age);
            findViewById(R.id.editProfileActivity_button_finish).setOnClickListener(onClickFinish());

            et_fullName.setText(profile.getFullName());

            if (profile.getMeasurementSystem() == Profile.MeasurementSystem.IMPERIAL)
            {
                ((TextView) findViewById(R.id.editProfileActivity_tv_height)).setText("Height (Inches)");
                ((TextView) findViewById(R.id.editProfileActivity_tv_weight)).setText("Weight (Lbs)");

                et_height.setText(profile.getHeightInches() + "");
                et_weight.setText(profile.getWeightLbs() + "");
            }
            else if (profile.getMeasurementSystem() == Profile.MeasurementSystem.METRIC)
            {
                ((TextView) findViewById(R.id.editProfileActivity_tv_height)).setText("Height (Cm)");
                ((TextView) findViewById(R.id.editProfileActivity_tv_weight)).setText("Weight (Kg)");

                et_height.setText("" + profile.getHeightCm() + "");
                et_weight.setText(profile.getWeightKg() + "");
            }

            et_birthday.setText(Profile.dateFormat.format(profile.getBirthday()));
            et_age.setText("" + profile.getAge());
        }
    }

    private View.OnClickListener onClickFinish()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fullName = et_fullName.getText().toString();
                float height = Float.parseFloat(et_height.getText().toString());
                float weight = Float.parseFloat(et_weight.getText().toString());

                if (fullName.equalsIgnoreCase("") || height == 0 || weight == 0)
                {
                    Toast toast = Toast.makeText(EditProfileActivity.this, "Invalid inputs", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                Profile profile = Profile.getProfile();
                if (profile != null)
                {
                    profile.setFullName(fullName);
                    if (Profile.MeasurementSystem.IMPERIAL == profile.getMeasurementSystem())
                    {
                        profile.setWeightLbs((int) weight);
                        profile.setHeightInches(height);
                    }
                    else
                    {
                        profile.setWeightKg(weight);
                        profile.setHeightCm(height);
                    }
                    profile.save(EditProfileActivity.this);
                }

                finish();
                if (MyProfileActivity.getInstance() != null)
                {
                    Toast toast = Toast.makeText(MyProfileActivity.getInstance(), "Profile saved!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        };
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
