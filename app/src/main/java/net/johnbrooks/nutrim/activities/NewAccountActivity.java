package net.johnbrooks.nutrim.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import net.johnbrooks.nutrim.R;
import net.johnbrooks.nutrim.utilities.MyApplicationContexts;
import net.johnbrooks.nutrim.utilities.Profile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NewAccountActivity extends AppCompatActivity
{
    private EditText et_fullName;
    private EditText et_weight;
    private EditText et_height;
    private EditText et_birthday;

    private ImageView iv_fullName;
    private ImageView iv_weight;
    private ImageView iv_height;
    private ImageView iv_birthday;

    private Spinner s_measurementSystem;

    private int pickedYear;
    private int pickedMonth;
    private int pickedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        MyApplicationContexts.refreshActivityTheme(NewAccountActivity.this);
        setContentView(R.layout.activity_new_account);
        MyApplicationContexts.getLatestContextWrapper(NewAccountActivity.this);

        et_fullName = (EditText) findViewById(R.id.newAccountActivity_editText_fullname);
        et_weight = (EditText) findViewById(R.id.newAccountActivity_editText_weightLbs);
        et_height = (EditText) findViewById(R.id.newAccountActivity_editText_heightInches);
        et_birthday = (EditText) findViewById(R.id.newAccountActivity_editText_birthday);

        iv_fullName = (ImageView) findViewById(R.id.newAccountActivity_imageView_fullname);
        iv_weight = (ImageView) findViewById(R.id.newAccountActivity_imageView_weightLbs);
        iv_height = (ImageView) findViewById(R.id.newAccountActivity_imageView_heightInches);
        iv_birthday = (ImageView) findViewById(R.id.newAccountActivity_imageView_birthday);

        s_measurementSystem = (Spinner) findViewById(R.id.newAccountActivity_spinner_measurementSystem);

        s_measurementSystem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Profile.MeasurementSystem measurementSystem = Profile.MeasurementSystem.values()[position];

                if (measurementSystem == Profile.MeasurementSystem.METRIC)
                {
                    et_weight.setHint("Weight (Kg)");
                    et_height.setHint("Height (Cm)");
                }
                else
                {
                    et_weight.setHint("Weight (Lbs)");
                    et_height.setHint("Height (Inches)");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        findViewById(R.id.newAccountActivity_button_finish).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkFullName() && checkWeight() && checkHeight() && checkBirthday())
                {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(pickedYear, pickedMonth, pickedDay);
                    Date birthday = calendar.getTime();

                    Profile.MeasurementSystem measurementSystem = null;
                    try
                    {
                        measurementSystem = Profile.MeasurementSystem.valueOf(s_measurementSystem.getSelectedItem().toString().toUpperCase());
                    }
                    catch (Exception ex)
                    {
                        Log.d(NewAccountActivity.class.getSimpleName(), "Cannot convert spinner value to enum (" + s_measurementSystem.getSelectedItem().toString().toUpperCase() + ").");
                        ex.printStackTrace();
                    }

                    float weightKg = Integer.parseInt(et_weight.getText().toString()) * (Profile.MeasurementSystem.IMPERIAL == measurementSystem ? 0.45359237f : 1);
                    float heightCm = (Integer.parseInt(et_height.getText().toString()) / (Profile.MeasurementSystem.IMPERIAL == measurementSystem ? 0.393701f : 1));

                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String birthdayString = dateFormat.format(birthday);

                    Log.d(NewAccountActivity.class.getSimpleName(), "Saving profile...");
                    Log.d(NewAccountActivity.class.getSimpleName(), "Full name: " + et_fullName.getText().toString());
                    Log.d(NewAccountActivity.class.getSimpleName(), "Height (cm): " + heightCm);
                    Log.d(NewAccountActivity.class.getSimpleName(), "Weight (kg): " + weightKg);
                    Log.d(NewAccountActivity.class.getSimpleName(), "Birthday: " + birthdayString);

                    Profile profile = Profile.createProfile(et_fullName.getText().toString(), birthday, weightKg, heightCm);
                    if (measurementSystem != null)
                        profile.setMeasurementSystem(measurementSystem);
                    profile.save(NewAccountActivity.this);
                    Intent intent = new Intent(NewAccountActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(NewAccountActivity.this);
                    dlgAlert.setMessage("Please take a quick moment to fill out the fields before continuing.");
                    dlgAlert.setTitle("Whoops!");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            }
        });

        et_fullName.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                checkFullName();
                return false;
            }
        });

        et_height.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                checkHeight();
                return false;
            }
        });

        et_weight.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                checkWeight();
                return false;
            }
        });

        //et_birthday.setEnabled(false);
        et_birthday.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {
                    Log.d(NewAccountActivity.class.getSimpleName(), "Opening Date Picker...");
                    et_birthday.clearFocus();

                    Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    pickedYear = mYear;

                    final Dialog dpDialog = new Dialog(NewAccountActivity.this);
                    dpDialog.setContentView(R.layout.dialog_birthday);
                    dpDialog.setTitle("Birthday Picker");
                    dpDialog.show();

                    DatePicker datePicker = (DatePicker) dpDialog.findViewById(R.id.dialog_dp);
                    datePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener()
                    {
                        @Override
                        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                        {
                            if (year != pickedYear || monthOfYear != pickedMonth || dayOfMonth != pickedDay)
                            {
                                pickedYear = year ;
                                pickedMonth = monthOfYear;
                                pickedDay = dayOfMonth;
                                return;
                            }

                            pickedYear = year;
                            pickedMonth = monthOfYear;
                            pickedDay = dayOfMonth;


                            Log.d(NewAccountActivity.class.getSimpleName(), "Birthday Changed to: " + pickedMonth + "/" + pickedDay + "/" + pickedYear);
                            dpDialog.cancel();
                            checkBirthday();
                        }
                    });
                }
            }
        });
    }

    private boolean checkFullName()
    {
        if (et_fullName.getText().toString().length() <= 3)
        {
            iv_fullName.setBackgroundResource(R.drawable.ic_highlight_off_black_24dp);
            return false;
        }
        else
        {
            iv_fullName.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
            return true;
        }
    }

    private boolean checkWeight()
    {
        int weight;
        try
        {
            weight = Integer.parseInt(et_weight.getText().toString());
        }
        catch(NumberFormatException ex)
        {
            Log.d(NewAccountActivity.class.getSimpleName(), "Could not parse int from et_weight");
            return false;
        }

        if (weight < 50)
        {
            iv_weight.setBackgroundResource(R.drawable.ic_highlight_off_black_24dp);
            return false;
        }
        else
        {
            iv_weight.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
            return true;
        }
    }

    private boolean checkHeight()
    {
        int height;
        try
        {
            height = Integer.parseInt(et_height.getText().toString());
        }
        catch(NumberFormatException ex)
        {
            Log.d(NewAccountActivity.class.getSimpleName(), "Could not parse int from et_height");
            return false;
        }

        if (height < 36)
        {
            iv_height.setBackgroundResource(R.drawable.ic_highlight_off_black_24dp);
            return false;
        }
        else
        {
            iv_height.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
            return true;
        }
    }

    private boolean checkBirthday()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(pickedYear, pickedMonth, pickedDay);
        Date date = calendar.getTime();

        long requirement = TimeUnit.MILLISECONDS.convert(13 * 365, TimeUnit.DAYS);
        Log.d(NewAccountActivity.class.getSimpleName(), "Requirement: " + requirement);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        et_birthday.setText(dateFormat.format(date));

        if (new Date().getTime() - date.getTime() < requirement)
        {
            iv_birthday.setBackgroundResource(R.drawable.ic_highlight_off_black_24dp);
            return false;
        }
        else
        {
            iv_birthday.setBackgroundResource(R.drawable.ic_check_circle_black_24dp);
            return true;
        }
    }
}
